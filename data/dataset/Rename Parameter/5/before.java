package org.apache.flink.table.planner.hint;

import org.apache.calcite.rel.BiRel;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelShuttleImpl;
import org.apache.calcite.rel.hint.Hintable;
import org.apache.calcite.rel.hint.RelHint;
import org.apache.calcite.rel.logical.LogicalCorrelate;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql2rel.SqlToRelConverter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Due to Calcite will expand the whole SQL RelNode tree that contains query block, query hints
 * (including join hints and state ttl hints) will be propagated from root to leaves in the whole
 * RelNode tree. This shuttle is used to clear the query hints that are propagated into the query
 * block incorrectly.
 *
 * <p>See more at {@link
 * org.apache.calcite.sql2rel.SqlToRelConverter#convertFrom(SqlToRelConverter.Blackboard, SqlNode,
 * List)}.
 */
public class ClearQueryHintsWithInvalidPropagationShuttle extends QueryHintsRelShuttle {

    @Override
    protected RelNode visitBiRel(BiRel biRel) {
        List<RelHint> hints = ((Hintable) biRel).getHints();

        Set<String> allHintNames =
                hints.stream().map(hint -> hint.hintName).collect(Collectors.toSet());

        // there are no query hints on this Join/Correlate node
        if (allHintNames.stream().noneMatch(FlinkHints::isQueryHint)) {
            return super.visit(biRel);
        }

        Optional<RelHint> firstAliasHint =
                hints.stream()
                        .filter(hint -> FlinkHints.HINT_ALIAS.equals(hint.hintName))
                        .findFirst();

        // there are no alias hints on this Join/Correlate node
        if (!firstAliasHint.isPresent()) {
            return super.visit(biRel);
        }

        List<RelHint> queryHintsFromOuterQueryBlock =
                hints.stream()
                        .filter(
                                hint ->
                                        FlinkHints.isQueryHint(hint.hintName)
                                                // if the size of inheritPath is bigger than 0, it
                                                // means that this query hint is propagated from its
                                                // parent
                                                && hint.inheritPath.size()
                                                > firstAliasHint.get().inheritPath.size())
                        .collect(Collectors.toList());

        if (queryHintsFromOuterQueryBlock.isEmpty()) {
            return super.visit(biRel);
        }

        RelNode newRelNode = biRel;
        ClearOuterQueryHintShuttle clearOuterQueryHintShuttle;

        for (RelHint outerQueryHint : queryHintsFromOuterQueryBlock) {
            clearOuterQueryHintShuttle = new ClearOuterQueryHintShuttle(outerQueryHint);
            newRelNode = newRelNode.accept(clearOuterQueryHintShuttle);
        }

        return super.visit(newRelNode);
    }

    /**
     * A shuttle to clean the query hints which are in outer query block and should not affect the
     * query-block inside.
     *
     * <p>Only the nodes that query hints could attach may be cleared. See more at {@link
     * FlinkHintStrategies}.
     */
    private static class ClearOuterQueryHintShuttle extends RelShuttleImpl {
        // the current inheritPath about the query hint that need be removed
        private final Deque<Integer> currentInheritPath;

        // the query hint that need be removed
        private final RelHint queryHintNeedRemove;

        public ClearOuterQueryHintShuttle(RelHint joinHintNeedRemove) {
            this.queryHintNeedRemove = joinHintNeedRemove;
            this.currentInheritPath = new ArrayDeque<>();
            this.currentInheritPath.addAll(joinHintNeedRemove.inheritPath);
        }

        @Override
        protected RelNode visitChild(RelNode parent, int i, RelNode child) {
            currentInheritPath.addLast(i);
            RelNode newNode = super.visitChild(parent, i, child);
            currentInheritPath.removeLast();
            return newNode;
        }

        @Override
        public RelNode visit(LogicalCorrelate correlate) {
            return visitBiRel(correlate);
        }

        @Override
        public RelNode visit(LogicalJoin join) {
            return visitBiRel(join);
        }

        private RelNode visitBiRel(BiRel biRel) {
            Hintable hBiRel = (Hintable) biRel;
            List<RelHint> hints = new ArrayList<>(hBiRel.getHints());
            Optional<RelHint> invalidQueryHint = getInvalidQueryHint(hints);

            // if this node contains the query hint that needs to be removed
            if (invalidQueryHint.isPresent()) {
                hints.remove(invalidQueryHint.get());
                return super.visit(hBiRel.withHints(hints));
            }

            return super.visit(biRel);
        }

        /**
         * Get the invalid query hint in this node.
         *
         * <p>The invalid node meets the following requirement:
         *
         * <p>1. This hint name is same with the query hint that needs to be removed
         *
         * <p>2.The length of this hint should be same with the length of propagating this removed
         * query hint.
         *
         * <p>3. The inherited path of this hint should match the inherited path of this removed
         * query hint.
         *
         * @param hints all hints
         * @return return the invalid query hint if exists, else return empty
         */
        private Optional<RelHint> getInvalidQueryHint(List<RelHint> hints) {
            for (RelHint hint : hints) {
                if (hint.hintName.equals(queryHintNeedRemove.hintName)
                        && isMatchInvalidInheritPath(
                        new ArrayList<>(currentInheritPath), hint.inheritPath)) {
                    return Optional.of(hint);
                }
            }
            return Optional.empty();
        }

        private boolean isMatchInvalidInheritPath(
                List<Integer> invalidInheritPath, List<Integer> checkedInheritPath) {
            if (invalidInheritPath.size() != checkedInheritPath.size()) {
                return false;
            }

            for (int i = 0; i < invalidInheritPath.size(); i++) {
                if (!Objects.equals(invalidInheritPath.get(i), checkedInheritPath.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }
}