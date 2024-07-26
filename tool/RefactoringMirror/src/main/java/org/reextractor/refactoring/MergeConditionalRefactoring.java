package org.reextractor.refactoring;

import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.StatementNodeTree;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MergeConditionalRefactoring implements Refactoring {

    private Set<StatementNodeTree> mergedConditionals;
    private StatementNodeTree newConditional;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public MergeConditionalRefactoring(Set<StatementNodeTree> mergedConditionals, StatementNodeTree newConditional,
                                       DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.mergedConditionals = mergedConditionals;
        this.newConditional = newConditional;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.MERGE_CONDITIONAL;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        for (StatementNodeTree mergedConditional : mergedConditionals) {
            ranges.add(mergedConditional.codeRange()
                    .setDescription("merged conditional")
                    .setCodeElement(mergedConditional.getExpression()));
        }
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(newConditional.codeRange()
                .setDescription("new conditional")
                .setCodeElement(newConditional.getExpression()));
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with merged conditional")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append("[");
        int len = 0;
        for (StatementNodeTree mergedConditional : mergedConditionals) {
            sb.append(mergedConditional.getExpression());
            if (len < mergedConditionals.size() - 1)
                sb.append(", ");
            len++;
        }
        sb.append("]");
        sb.append(" to ");
        sb.append(newConditional.getExpression());
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public Set<String> getMergedConditionals() {
        return mergedConditionals.stream().map(StatementNodeTree::getExpression).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public String getNewConditional() {
        return newConditional.getExpression();
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
