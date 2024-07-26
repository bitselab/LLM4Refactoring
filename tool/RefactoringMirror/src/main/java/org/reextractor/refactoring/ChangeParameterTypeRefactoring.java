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

public class ChangeParameterTypeRefactoring implements Refactoring {

    private VariableDeclaration originalVariable;
    private VariableDeclaration changedTypeVariable;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public ChangeParameterTypeRefactoring(VariableDeclaration originalVariable, VariableDeclaration changedTypeVariable,
                                          DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.originalVariable = originalVariable;
        this.changedTypeVariable = changedTypeVariable;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.CHANGE_PARAMETER_TYPE;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo variableLocation = new LocationInfo(
                (CompilationUnit) originalVariable.getRoot(), operationBefore.getFilePath(), originalVariable);
        ranges.add(variableLocation.codeRange()
                .setDescription("original variable declaration")
                .setCodeElement(VariableUtils.variable2String(originalVariable)));
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo variableLocation = new LocationInfo(
                (CompilationUnit) changedTypeVariable.getRoot(), operationAfter.getFilePath(), changedTypeVariable);
        ranges.add(variableLocation.codeRange()
                .setDescription("changed-type variable declaration")
                .setCodeElement(VariableUtils.variable2String(changedTypeVariable)));
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with changed variable type")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(VariableUtils.variable2String(originalVariable));
        sb.append(" to ");
        sb.append(VariableUtils.variable2String(changedTypeVariable));
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public VariableDeclaration getOriginalVariable() {
        return originalVariable;
    }

    public VariableDeclaration getChangedTypeVariable() {
        return changedTypeVariable;
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
