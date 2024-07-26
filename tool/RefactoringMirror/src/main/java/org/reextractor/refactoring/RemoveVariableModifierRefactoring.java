package org.reextractor.refactoring;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.reextractor.util.MethodUtils;
import org.reextractor.util.VariableUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.LocationInfo;

import java.util.ArrayList;
import java.util.List;

public class RemoveVariableModifierRefactoring implements Refactoring {

    private String modifier;
    private VariableDeclaration variableBefore;
    private VariableDeclaration variableAfter;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public RemoveVariableModifierRefactoring(String modifier, VariableDeclaration variableBefore, VariableDeclaration variableAfter,
                                             DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.modifier = modifier;
        this.variableBefore = variableBefore;
        this.variableAfter = variableAfter;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.REMOVE_VARIABLE_MODIFIER;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo parameterLocation = new LocationInfo(
                (CompilationUnit) variableBefore.getRoot(), operationBefore.getFilePath(), variableBefore);
        ranges.add(parameterLocation.codeRange()
                .setDescription("original variable declaration")
                .setCodeElement(VariableUtils.variable2String(variableBefore)));
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo parameterLocation = new LocationInfo(
                (CompilationUnit) variableAfter.getRoot(), operationAfter.getFilePath(), variableAfter);
        ranges.add(parameterLocation.codeRange()
                .setDescription("variable declaration with removed modifier")
                .setCodeElement(VariableUtils.variable2String(variableAfter)));
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with removed variable modifier")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(modifier);
        sb.append(" in variable ");
        sb.append(VariableUtils.variable2String(variableBefore));
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationBefore));
        sb.append(" from class ");
        sb.append(operationBefore.getNamespace());
        return sb.toString();
    }

    public String getModifier() {
        return modifier;
    }

    public VariableDeclaration getVariableBefore() {
        return variableBefore;
    }

    public VariableDeclaration getVariableAfter() {
        return variableAfter;
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
