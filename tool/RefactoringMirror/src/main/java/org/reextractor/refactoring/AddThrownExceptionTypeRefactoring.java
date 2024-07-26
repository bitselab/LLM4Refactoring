package org.reextractor.refactoring;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Type;
import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.LocationInfo;

import java.util.ArrayList;
import java.util.List;

public class AddThrownExceptionTypeRefactoring implements Refactoring {

    private Type exceptionType;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public AddThrownExceptionTypeRefactoring(Type exceptionType,
                                             DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.exceptionType = exceptionType;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.ADD_THROWN_EXCEPTION_TYPE;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo exceptionLocation = new LocationInfo(
                (CompilationUnit) exceptionType.getRoot(), operationAfter.getFilePath(), exceptionType);
        ranges.add(exceptionLocation.codeRange()
                .setDescription("added thrown exception type")
                .setCodeElement(exceptionType.toString()));
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with added thrown exception type")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(exceptionType.toString());
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public Type getExceptionType() {
        return exceptionType;
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
