package org.reextractor.refactoring;

import org.remapper.dto.DeclarationNodeTree;

public class PushDownOperationRefactoring extends MoveOperationRefactoring {

    public PushDownOperationRefactoring(DeclarationNodeTree originalOperation, DeclarationNodeTree movedOperation) {
        super(originalOperation, movedOperation);
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.PUSH_DOWN_OPERATION;
    }
}
