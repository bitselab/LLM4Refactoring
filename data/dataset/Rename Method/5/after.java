package org.apache.flink.table.planner.hint;

import org.apache.flink.table.api.TableException;
import org.apache.flink.table.planner.plan.rules.logical.WrapJsonAggFunctionArgumentsRule;
import org.apache.flink.table.planner.plan.schema.FlinkPreparingTableBase;

import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.hint.Hintable;
import org.apache.calcite.rel.hint.RelHint;
import org.apache.calcite.rel.logical.LogicalFilter;
import org.apache.calcite.rel.logical.LogicalJoin;
import org.apache.calcite.rel.logical.LogicalProject;
import org.apache.calcite.rel.logical.LogicalSnapshot;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.rex.RexShuttle;
import org.apache.calcite.rex.RexSubQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Utility class for Flink hints. */
public abstract class FlinkHints {
    // ~ Static fields/initializers ---------------------------------------------

    public static final String HINT_NAME_OPTIONS = "OPTIONS";

    // ~ Internal alias tag hint
    public static final String HINT_ALIAS = "ALIAS";

    // ~ Option name for hints on join or correlate
    public static final String LEFT_INPUT = "LEFT";
    public static final String RIGHT_INPUT = "RIGHT";

    /**
     * Internal hint that JSON aggregation function arguments have been wrapped already. See {@link
     * WrapJsonAggFunctionArgumentsRule}.
     */
    public static final String HINT_NAME_JSON_AGGREGATE_WRAPPED = "JSON_AGGREGATE_WRAPPED";

    // ~ Tools ------------------------------------------------------------------

    /**
     * Returns the OPTIONS hint options from the given list of table hints {@code tableHints}, never
     * null.
     */
    public static Map<String, String> getHintedOptions(List<RelHint> tableHints) {
        return tableHints.stream()
                .filter(hint -> hint.hintName.equalsIgnoreCase(HINT_NAME_OPTIONS))
                .findFirst()
                .map(hint -> hint.kvOptions)
                .orElse(Collections.emptyMap());
    }

    /**
     * Merges the dynamic table options from {@code hints} and static table options from table
     * definition {@code props}.
     *
     * <p>The options in {@code hints} would override the ones in {@code props} if they have the
     * same option key.
     *
     * @param hints Dynamic table options, usually from the OPTIONS hint
     * @param props Static table options defined in DDL or connect API
     * @return New options with merged dynamic table options, or the old {@code props} if there is
     *     no dynamic table options
     */
    public static Map<String, String> mergeTableOptions(
            Map<String, String> hints, Map<String, String> props) {
        if (hints.size() == 0) {
            return props;
        }
        Map<String, String> newProps = new HashMap<>();
        newProps.putAll(props);
        newProps.putAll(hints);
        return Collections.unmodifiableMap(newProps);
    }

    public static Optional<String> getTableAlias(RelNode node) {
        if (node instanceof Hintable) {
            Hintable aliasNode = (Hintable) node;
            List<String> aliasNames =
                    aliasNode.getHints().stream()
                            .filter(h -> h.hintName.equalsIgnoreCase(FlinkHints.HINT_ALIAS))
                            .flatMap(h -> h.listOptions.stream())
                            .collect(Collectors.toList());
            if (aliasNames.size() > 0) {
                return Optional.of(aliasNames.get(0));
            } else if (canTransposeToTableScan(node)) {
                return getTableAlias(node.getInput(0));
            }
        }
        return Optional.empty();
    }

    public static boolean canTransposeToTableScan(RelNode node) {
        return node instanceof LogicalProject // computed column on table
                || node instanceof LogicalFilter
                || node instanceof LogicalSnapshot;
    }

    /** Returns the qualified name of a table scan, otherwise returns empty. */
    public static Optional<String> getTableName(RelOptTable table) {
        if (table == null) {
            return Optional.empty();
        }

        String tableName;
        if (table instanceof FlinkPreparingTableBase) {
            tableName = StringUtils.join(((FlinkPreparingTableBase) table).getNames(), '.');
        } else {
            throw new TableException(
                    String.format(
                            "Could not get the table name with the unknown table class `%s`",
                            table.getClass().getCanonicalName()));
        }

        return Optional.of(tableName);
    }

    public static String stringifyHints(List<RelHint> hints) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (RelHint h : hints) {
            if (h.hintName.equalsIgnoreCase(FlinkHints.HINT_ALIAS)) {
                continue;
            }
            if (!first) {
                sb.append(", ");
            }
            sb.append(h.hintName);
            if (h.listOptions.size() > 0) {
                String listStr = h.listOptions.stream().collect(Collectors.joining(",", "(", ")"));
                sb.append(listStr);
            } else if (h.kvOptions.size() > 0) {
                String mapStr =
                        h.kvOptions.entrySet().stream()
                                .map(e -> e.getKey() + "=" + e.getValue())
                                .collect(Collectors.joining(", ", "(", ")"));
                sb.append(mapStr);
            }
            first = false;
        }
        return sb.toString();
    }

    /** Get all query hints. */
    public static List<RelHint> getAllQueryHints(List<RelHint> allHints) {
        return allHints.stream()
                .filter(
                        hint ->
                                JoinStrategy.isJoinStrategy(hint.hintName)
                                        || StateTtlHint.isStateTtlHint(hint.hintName))
                .collect(Collectors.toList());
    }

    /** Get all join hints. */
    public static List<RelHint> getAllJoinHints(List<RelHint> allHints) {
        return allHints.stream()
                .filter(hint -> JoinStrategy.isJoinStrategy(hint.hintName))
                .collect(Collectors.toList());
    }

    /** Get all state ttl hints. */
    public static List<RelHint> getAllStateTtlHints(List<RelHint> allHints) {
        return allHints.stream()
                .filter(hint -> StateTtlHint.isStateTtlHint(hint.hintName))
                .collect(Collectors.toList());
    }

    /**
     * Get all query block alias hints.
     *
     * <p>Because query block alias hints will be propagated from root to leaves, so maybe one node
     * will contain multi alias hints. But only the first one is the real query block name where
     * this node is.
     */
    public static List<RelHint> getQueryBlockAliasHints(List<RelHint> allHints) {
        return allHints.stream()
                .filter(hint -> hint.hintName.equals(FlinkHints.HINT_ALIAS))
                .collect(Collectors.toList());
    }

    public static RelNode capitalizeQueryHints(RelNode root) {
        return root.accept(new CapitalizeQueryHintsShuttle());
    }

    /** Resolve the RelNode of the sub query in the node and return a new node. */
    public static RelNode resolveSubQuery(RelNode node, Function<RelNode, RelNode> resolver) {
        if (node instanceof LogicalProject) {
            LogicalProject project = (LogicalProject) node;
            List<RexNode> newProjects =
                    project.getProjects().stream()
                            .map(p -> resolveSubQuery(p, resolver))
                            .collect(Collectors.toList());
            return project.copy(
                    project.getTraitSet(), project.getInput(), newProjects, project.getRowType());

        } else if (node instanceof LogicalFilter) {
            LogicalFilter filter = (LogicalFilter) node;
            RexNode newCondition = resolveSubQuery(filter.getCondition(), resolver);
            return filter.copy(filter.getTraitSet(), filter.getInput(), newCondition);

        } else if (node instanceof LogicalJoin) {
            LogicalJoin join = (LogicalJoin) node;
            RexNode newCondition = resolveSubQuery(join.getCondition(), resolver);
            return join.copy(
                    join.getTraitSet(),
                    newCondition,
                    join.getLeft(),
                    join.getRight(),
                    join.getJoinType(),
                    join.isSemiJoinDone());

        } else {
            return node;
        }
    }

    /** Resolve the RelNode of the sub query in conditions. */
    private static RexNode resolveSubQuery(RexNode rexNode, Function<RelNode, RelNode> resolver) {
        return rexNode.accept(
                new RexShuttle() {
                    @Override
                    public RexNode visitSubQuery(RexSubQuery subQuery) {
                        RelNode oldRel = subQuery.rel;
                        RelNode newRel = resolver.apply(oldRel);
                        if (oldRel != newRel) {
                            return super.visitSubQuery(subQuery.clone(newRel));
                        }
                        return subQuery;
                    }
                });
    }

    /** Clear the query hints on some nodes where these hints should not be attached. */
    public static RelNode clearQueryHintsOnUnmatchedNodes(RelNode root) {
        return root.accept(
                new ClearQueryHintsOnUnmatchedNodesShuttle(root.getCluster().getHintStrategies()));
    }

    /** Check if the hint is a query hint. */
    public static boolean isQueryHint(String hintName) {
        return JoinStrategy.isJoinStrategy(hintName) || StateTtlHint.isStateTtlHint(hintName);
    }

    /**
     * Currently, lookup join hints and state ttl hints are KV hints. And regular join hints are
     * LIST hints.
     */
    public static boolean isKVQueryHint(String hintName) {
        return JoinStrategy.isLookupHint(hintName) || StateTtlHint.isStateTtlHint(hintName);
    }
}