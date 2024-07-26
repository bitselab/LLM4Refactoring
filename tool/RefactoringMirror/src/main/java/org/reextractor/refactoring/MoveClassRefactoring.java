package org.reextractor.refactoring;

import org.reextractor.util.ClassUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.List;

public class MoveClassRefactoring implements Refactoring {

    private DeclarationNodeTree originalClass;
    private DeclarationNodeTree movedClass;

    public MoveClassRefactoring(DeclarationNodeTree originalClass, DeclarationNodeTree movedClass) {
        this.originalClass = originalClass;
        this.movedClass = movedClass;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.MOVE_CLASS;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(originalClass.codeRange()
                .setDescription("original type declaration")
                .setCodeElement(ClassUtils.typeDeclaration2String(originalClass)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(movedClass.codeRange()
                .setDescription("moved type declaration")
                .setCodeElement(ClassUtils.typeDeclaration2String(movedClass)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(ClassUtils.typeDeclaration2String(originalClass));
        sb.append(" moved to ");
        sb.append(ClassUtils.typeDeclaration2String(movedClass));
        return sb.toString();
    }

    public DeclarationNodeTree getOriginalClass() {
        return originalClass;
    }

    public DeclarationNodeTree getMovedClass() {
        return movedClass;
    }
}
