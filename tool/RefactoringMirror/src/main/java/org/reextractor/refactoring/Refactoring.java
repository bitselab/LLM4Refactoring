package org.reextractor.refactoring;

import org.remapper.dto.CodeRange;

import java.util.List;

public interface Refactoring {

    RefactoringType getRefactoringType();

    List<CodeRange> leftSide();

    List<CodeRange> rightSide();

    public String getName();

    public String toString();
}
