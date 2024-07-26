package org.reextractor.refactoring;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Type;
import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.LocationInfo;

import java.util.ArrayList;
import java.util.List;

public class ChangeReturnTypeRefactoring implements Refactoring {

    private Type originalType;
    private Type changedType;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public ChangeReturnTypeRefactoring(Type originalType, Type changedType,
                                       DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.originalType = originalType;
        this.changedType = changedType;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.CHANGE_RETURN_TYPE;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo typeLocation = new LocationInfo(
                (CompilationUnit) originalType.getRoot(), operationBefore.getFilePath(), originalType);
        ranges.add(typeLocation.codeRange()
                .setDescription("original return type")
                .setCodeElement(originalType.toString()));
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo typeLocation = new LocationInfo(
                (CompilationUnit) changedType.getRoot(), operationAfter.getFilePath(), changedType);
        ranges.add(typeLocation.codeRange()
                .setDescription("changed return type")
                .setCodeElement(changedType.toString()));
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with changed return type")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(originalType);
        sb.append(" to ");
        sb.append(changedType);
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public Type getOriginalType() {
        return originalType;
    }

    public Type getChangedType() {
        return changedType;
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
