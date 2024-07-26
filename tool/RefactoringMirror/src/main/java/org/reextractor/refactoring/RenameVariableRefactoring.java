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

public class RenameVariableRefactoring implements Refactoring {

    private VariableDeclaration originalVariable;
    private VariableDeclaration renamedVariable;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public RenameVariableRefactoring(VariableDeclaration originalVariable, VariableDeclaration renamedVariable,
                                     DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.originalVariable = originalVariable;
        this.renamedVariable = renamedVariable;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.RENAME_VARIABLE;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo variableLocation = new LocationInfo(
                (CompilationUnit) originalVariable.getRoot(), operationBefore.getFilePath(), originalVariable);
        ranges.add(variableLocation.codeRange()
                .setDescription("original variable declaration")
                .setCodeElement(VariableUtils.variable2String(originalVariable).strip()));
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore).strip()));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo variableLocation = new LocationInfo(
                (CompilationUnit) renamedVariable.getRoot(), operationAfter.getFilePath(), renamedVariable);
        ranges.add(variableLocation.codeRange()
                .setDescription("renamed variable declaration")
                .setCodeElement(VariableUtils.variable2String(renamedVariable).strip()));
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with renamed variable")
                .setCodeElement(MethodUtils.method2String(operationAfter).strip()));
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
        sb.append(VariableUtils.variable2String(renamedVariable));
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public VariableDeclaration getOriginalVariable() {
        return originalVariable;
    }

    public VariableDeclaration getRenamedVariable() {
        return renamedVariable;
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
