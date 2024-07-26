package org.reextractor.refactoring;

import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.List;

public class MoveOperationRefactoring implements Refactoring {

    private DeclarationNodeTree originalOperation;
    private DeclarationNodeTree movedOperation;

    public MoveOperationRefactoring(DeclarationNodeTree originalOperation, DeclarationNodeTree movedOperation) {
        this.originalOperation = originalOperation;
        this.movedOperation = movedOperation;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.MOVE_OPERATION;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(originalOperation.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(originalOperation).strip()));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(movedOperation.codeRange()
                .setDescription("moved method declaration")
                .setCodeElement(MethodUtils.method2String(movedOperation).strip()));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(MethodUtils.method2String(originalOperation));
        sb.append(" from class ");
        sb.append(getSourceClassName());
        sb.append(" to ");
        sb.append(MethodUtils.method2String(movedOperation));
        sb.append(" from class ");
        sb.append(getTargetClassName());
        return sb.toString();
    }

    public String getSourceClassName() {
        return originalOperation.getNamespace();
    }

    public String getTargetClassName() {
        return movedOperation.getNamespace();
    }

    public DeclarationNodeTree getOriginalOperation() {
        return originalOperation;
    }

    public DeclarationNodeTree getMovedOperation() {
        return movedOperation;
    }
}
