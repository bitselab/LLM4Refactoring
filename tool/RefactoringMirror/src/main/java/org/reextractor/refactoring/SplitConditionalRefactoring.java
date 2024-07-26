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

public class SplitConditionalRefactoring implements Refactoring {

    private StatementNodeTree originalConditional;
    private Set<StatementNodeTree> splitConditionals;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public SplitConditionalRefactoring(StatementNodeTree originalConditional, Set<StatementNodeTree> splitConditionals,
                                       DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.originalConditional = originalConditional;
        this.splitConditionals = splitConditionals;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.SPLIT_CONDITIONAL;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(originalConditional.codeRange()
                .setDescription("original conditional")
                .setCodeElement(originalConditional.getExpression()));
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        for (StatementNodeTree splitConditional : splitConditionals) {
            ranges.add(splitConditional.codeRange()
                    .setDescription("split conditional")
                    .setCodeElement(splitConditional.getExpression()));
        }
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with split conditional")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(originalConditional.getExpression());
        sb.append(" to ");
        sb.append("[");
        int len = 0;
        for (StatementNodeTree splitConditional : splitConditionals) {
            sb.append(splitConditional.getExpression());
            if (len < splitConditionals.size() - 1)
                sb.append(", ");
            len++;
        }
        sb.append("]");
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public String getOriginalConditional() {
        return originalConditional.getExpression();
    }

    public Set<String> getSplitConditionals() {
        return splitConditionals.stream().map(StatementNodeTree::getExpression).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
