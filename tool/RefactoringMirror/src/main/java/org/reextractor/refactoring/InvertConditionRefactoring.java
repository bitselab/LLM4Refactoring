package org.reextractor.refactoring;

import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.StatementNodeTree;

import java.util.ArrayList;
import java.util.List;

public class InvertConditionRefactoring implements Refactoring {

    private StatementNodeTree originalConditional;
    private StatementNodeTree invertedConditional;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public InvertConditionRefactoring(StatementNodeTree originalConditional, StatementNodeTree invertedConditional,
                                      DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.originalConditional = originalConditional;
        this.invertedConditional = invertedConditional;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.INVERT_CONDITION;
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
        ranges.add(invertedConditional.codeRange()
                .setDescription("inverted conditional")
                .setCodeElement(invertedConditional.getExpression()));
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with inverted conditional")
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
        sb.append(invertedConditional.getExpression());
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public String getOriginalConditional() {
        return originalConditional.getExpression();
    }

    public String getInvertedConditional() {
        return invertedConditional.getExpression();
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
