package org.reextractor.refactoring;

import org.remapper.dto.DeclarationNodeTree;

public class PullUpOperationRefactoring extends MoveOperationRefactoring {

    public PullUpOperationRefactoring(DeclarationNodeTree originalOperation, DeclarationNodeTree movedOperation) {
        super(originalOperation, movedOperation);
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.PULL_UP_OPERATION;
    }
}
