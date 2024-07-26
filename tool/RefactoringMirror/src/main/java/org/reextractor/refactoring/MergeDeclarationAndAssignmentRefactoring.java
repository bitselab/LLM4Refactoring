package org.reextractor.refactoring;

import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.StatementNodeTree;

import java.util.ArrayList;
import java.util.List;

public class MergeDeclarationAndAssignmentRefactoring implements Refactoring {

    private StatementNodeTree originalDeclaration;
    private StatementNodeTree originalAssignment;
    private StatementNodeTree mergedDeclaration;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public MergeDeclarationAndAssignmentRefactoring(StatementNodeTree originalDeclaration, StatementNodeTree originalAssignment, StatementNodeTree mergedDeclaration,
                                                    DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.originalDeclaration = originalDeclaration;
        this.originalAssignment = originalAssignment;
        this.mergedDeclaration = mergedDeclaration;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.MERGE_DECLARATION_AND_ASSIGNMENT;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(originalDeclaration.codeRange()
                .setDescription("original variable declaration")
                .setCodeElement(originalDeclaration.getExpression()));
        ranges.add(originalAssignment.codeRange()
                .setDescription("original assignment")
                .setCodeElement(originalAssignment.getExpression()));
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(mergedDeclaration.codeRange()
                .setDescription("merged variable declaration")
                .setCodeElement(mergedDeclaration.getExpression()));
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
        sb.append(originalDeclaration.getExpression().contains(";\n") ? originalDeclaration.getExpression().substring(0, originalDeclaration.getExpression().indexOf(";\n")) : originalDeclaration);
        sb.append(", ");
        sb.append(originalAssignment.getExpression().contains(";\n") ? originalAssignment.getExpression().substring(0, originalAssignment.getExpression().indexOf(";\n")) : originalAssignment);
        sb.append("]");
        sb.append(" to ");
        sb.append(mergedDeclaration.getExpression().contains(";\n") ? mergedDeclaration.getExpression().substring(0, mergedDeclaration.getExpression().indexOf(";\n")) : mergedDeclaration);
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public String getOriginalDeclaration() {
        return originalDeclaration.getExpression();
    }

    public String getOriginalAssignment() {
        return originalAssignment.getExpression();
    }

    public String getMergedDeclaration() {
        return mergedDeclaration.getExpression();
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
