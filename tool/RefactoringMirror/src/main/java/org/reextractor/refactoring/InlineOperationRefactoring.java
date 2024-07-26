package org.reextractor.refactoring;

import org.apache.commons.lang3.tuple.Pair;
import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.StatementNodeTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class InlineOperationRefactoring implements Refactoring {

    private DeclarationNodeTree inlinedOperation;
    private DeclarationNodeTree targetOperationBeforeInline;
    private DeclarationNodeTree targetOperationAfterInline;
    private List<StatementNodeTree> inlinedCodeFragmentsFromInlinedOperation;
    private List<StatementNodeTree> inlinedCodeFragmentsInTargetOperation;

    public InlineOperationRefactoring(DeclarationNodeTree targetOperationBeforeInline, DeclarationNodeTree targetOperationAfterInline,
                                      DeclarationNodeTree inlinedOperation, Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements) {
        this.targetOperationBeforeInline = targetOperationBeforeInline;
        this.targetOperationAfterInline = targetOperationAfterInline;
        this.inlinedOperation = inlinedOperation;
        this.inlinedCodeFragmentsFromInlinedOperation = new ArrayList<>();
        this.inlinedCodeFragmentsInTargetOperation = new ArrayList<>();
        for (Pair<StatementNodeTree, StatementNodeTree> matchedStatement : matchedStatements) {
            StatementNodeTree left = matchedStatement.getLeft();
            StatementNodeTree right = matchedStatement.getRight();
            if(left.getRoot() == inlinedOperation.getMethodNode() && right.getRoot() == targetOperationAfterInline.getMethodNode()) {
                inlinedCodeFragmentsFromInlinedOperation.add(left);
                inlinedCodeFragmentsInTargetOperation.add(right);
            }
        }
        inlinedCodeFragmentsFromInlinedOperation.sort(Comparator.comparingInt(StatementNodeTree::getPosition));
        inlinedCodeFragmentsInTargetOperation.sort(Comparator.comparingInt(StatementNodeTree::getPosition));
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.INLINE_OPERATION;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(inlinedOperation.codeRange()
                .setDescription("inlined method declaration")
                .setCodeElement(MethodUtils.method2String(inlinedOperation).strip()));
        for (StatementNodeTree inlinedCodeFragment : inlinedCodeFragmentsFromInlinedOperation) {
            ranges.add(inlinedCodeFragment.codeRange()
                    .setDescription("inlined code from inlined method declaration")
                    .setCodeElement(inlinedCodeFragment.getExpression().strip()));
        }
        ranges.add(targetOperationBeforeInline.codeRange()
                .setDescription("target method declaration before inline")
                .setCodeElement(MethodUtils.method2String(targetOperationBeforeInline).strip()));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(targetOperationAfterInline.codeRange()
                .setDescription("target method declaration after inline")
                .setCodeElement(MethodUtils.method2String(targetOperationAfterInline).strip()));
        for (StatementNodeTree inlinedCodeFragment : inlinedCodeFragmentsInTargetOperation) {
            ranges.add(inlinedCodeFragment.codeRange()
                    .setDescription("inlined code in target method declaration")
                    .setCodeElement(inlinedCodeFragment.getExpression().strip()));
        }
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(MethodUtils.method2String(inlinedOperation));
        sb.append(" inlined to ");
        sb.append(MethodUtils.method2String(targetOperationAfterInline));
        sb.append(" in class ");
        sb.append(targetOperationAfterInline.getNamespace());
        return sb.toString();
    }

    public DeclarationNodeTree getInlinedOperation() {
        return inlinedOperation;
    }

    public DeclarationNodeTree getTargetOperationBeforeInline() {
        return targetOperationBeforeInline;
    }

    public DeclarationNodeTree getTargetOperationAfterInline() {
        return targetOperationAfterInline;
    }
}
