package org.reextractor.refactoring;

import org.reextractor.dto.Visibility;
import org.reextractor.util.ClassUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.List;

public class ChangeClassAccessModifierRefactoring implements Refactoring {

    private Visibility originalAccessModifier;
    private Visibility changedAccessModifier;
    private DeclarationNodeTree classBefore;
    private DeclarationNodeTree classAfter;

    public ChangeClassAccessModifierRefactoring(Visibility originalAccessModifier, Visibility changedAccessModifier,
                                                DeclarationNodeTree classBefore, DeclarationNodeTree classAfter) {
        this.originalAccessModifier = originalAccessModifier;
        this.changedAccessModifier = changedAccessModifier;
        this.classBefore = classBefore;
        this.classAfter = classAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.CHANGE_CLASS_ACCESS_MODIFIER;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(classBefore.codeRange()
                .setDescription("original class declaration")
                .setCodeElement(ClassUtils.typeDeclaration2String(classBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(classAfter.codeRange()
                .setDescription("class declaration with changed access modifier")
                .setCodeElement(ClassUtils.typeDeclaration2String(classAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(originalAccessModifier);
        sb.append(" to ");
        sb.append(changedAccessModifier);
        sb.append(" in class ");
        sb.append(ClassUtils.typeDeclaration2String(classAfter));
        return sb.toString();
    }

    public Visibility getOriginalAccessModifier() {
        return originalAccessModifier;
    }

    public Visibility getChangedAccessModifier() {
        return changedAccessModifier;
    }

    public DeclarationNodeTree getClassBefore() {
        return classBefore;
    }

    public DeclarationNodeTree getClassAfter() {
        return classAfter;
    }
}
