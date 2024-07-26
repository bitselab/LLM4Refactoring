package org.reextractor.refactoring;

import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.StatementNodeTree;
import org.remapper.dto.StatementType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReplaceIfElseWithTernaryRefactoring implements Refactoring {

    private StatementNodeTree originalVariable;
    private StatementNodeTree ifConditional;
    private StatementNodeTree ternaryOperator;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public ReplaceIfElseWithTernaryRefactoring(StatementNodeTree originalVariable, StatementNodeTree ifConditional, StatementNodeTree ternaryOperator, DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.originalVariable = originalVariable;
        this.ifConditional = ifConditional;
        this.ternaryOperator = ternaryOperator;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.REPLACE_IF_ELSE_WITH_TERNARY;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(originalVariable.codeRange()
                .setDescription("original variable declaration")
                .setCodeElement(originalVariable.getExpression()));
        ranges.add(ifConditional.codeRange()
                .setDescription("inlined conditional")
                .setCodeElement(ifConditional.getExpression()));
        List<StatementNodeTree> descendants = ifConditional.getDescendants();
        descendants.sort(Comparator.comparingInt(StatementNodeTree::getPosition));
        for (StatementNodeTree statement : descendants) {
            if (statement.getType() == StatementType.BLOCK)
                continue;
            ranges.add(statement.codeRange()
                    .setDescription("original code")
                    .setCodeElement(statement.getExpression().strip() ));
        }
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(ternaryOperator.codeRange()
                .setDescription("new ternary operator")
                .setCodeElement(ternaryOperator.getExpression()));
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with ternary operator")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(ifConditional.getExpression());
        sb.append(" with ");
        sb.append(ternaryOperator.getExpression().lastIndexOf(";\n") != -1 ? ternaryOperator.getExpression().substring(0, ternaryOperator.getExpression().lastIndexOf(";\n")) : ternaryOperator);
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public String getIfConditional() {
        return ifConditional.getExpression();
    }

    public String getTernaryOperator() {
        return ternaryOperator.getExpression();
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
