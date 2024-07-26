package org.reextractor.refactoring;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Type;
import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.LocationInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChangeThrownExceptionTypeRefactoring implements Refactoring {

    private Set<Type> originalTypes;
    private Set<Type> changedTypes;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public ChangeThrownExceptionTypeRefactoring(Set<Type> originalTypes, Set<Type> changedTypes,
                                                DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.originalTypes = originalTypes;
        this.changedTypes = changedTypes;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.CHANGE_THROWN_EXCEPTION_TYPE;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        for (Type originalType : originalTypes) {
            LocationInfo typeLocation = new LocationInfo(
                    (CompilationUnit) originalType.getRoot(), operationBefore.getFilePath(), originalType);
            ranges.add(typeLocation.codeRange()
                    .setDescription("original exception type")
                    .setCodeElement(originalType.toString()));
        }
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        for (Type changedType : changedTypes) {
            LocationInfo typeLocation = new LocationInfo(
                    (CompilationUnit) changedType.getRoot(), operationBefore.getFilePath(), changedType);
            ranges.add(typeLocation.codeRange()
                    .setDescription("changed exception type")
                    .setCodeElement(changedType.toString()));
        }
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with changed thrown exception type")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        if (originalTypes.size() == 1)
            sb.append(originalTypes.iterator().next().toString());
        else {
            int len = 0;
            sb.append("[");
            for (Type type : originalTypes) {
                sb.append(type.toString());
                if (len < originalTypes.size() - 1)
                    sb.append(", ");
                len += 1;
            }
            sb.append("]");
        }
        sb.append(" to ");
        if (changedTypes.size() == 1)
            sb.append(changedTypes.iterator().next().toString());
        else {
            int len = 0;
            sb.append("[");
            for (Type type : changedTypes) {
                sb.append(type.toString());
                if (len < changedTypes.size() - 1)
                    sb.append(", ");
                len += 1;
            }
            sb.append("]");
        }
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public Set<Type> getOriginalTypes() {
        return originalTypes;
    }

    public Set<Type> getChangedTypes() {
        return changedTypes;
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
