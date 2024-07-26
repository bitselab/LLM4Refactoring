package org.reextractor.refactoring;

import org.remapper.dto.DeclarationNodeTree;

public class PullUpAttributeRefactoring extends MoveAttributeRefactoring {

    public PullUpAttributeRefactoring(DeclarationNodeTree originalAttribute, DeclarationNodeTree movedAttribute) {
        super(originalAttribute, movedAttribute);
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.PULL_UP_ATTRIBUTE;
    }
}
