package org.reextractor.refactoring;

import org.remapper.dto.DeclarationNodeTree;

public class PushDownAttributeRefactoring extends MoveAttributeRefactoring {

    public PushDownAttributeRefactoring(DeclarationNodeTree originalAttribute, DeclarationNodeTree movedAttribute) {
        super(originalAttribute, movedAttribute);
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.PUSH_DOWN_ATTRIBUTE;
    }
}
