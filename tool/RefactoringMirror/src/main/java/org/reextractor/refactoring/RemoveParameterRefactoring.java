package org.reextractor.refactoring;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.reextractor.util.MethodUtils;
import org.reextractor.util.VariableUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.LocationInfo;

import java.util.ArrayList;
import java.util.List;

public class RemoveParameterRefactoring implements Refactoring {

    private SingleVariableDeclaration parameter;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public RemoveParameterRefactoring(SingleVariableDeclaration parameter, DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.parameter = parameter;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.REMOVE_PARAMETER;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo parameterLocation = new LocationInfo(
                (CompilationUnit) parameter.getRoot(), operationBefore.getFilePath(), parameter);
        ranges.add(parameterLocation.codeRange()
                .setDescription("removed parameter")
                .setCodeElement(VariableUtils.variable2String(parameter)));
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with removed parameter")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(VariableUtils.variable2String(parameter));
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationBefore));
        sb.append(" from class ");
        sb.append(operationBefore.getNamespace());
        return sb.toString();
    }

    public SingleVariableDeclaration getParameter() {
        return parameter;
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
