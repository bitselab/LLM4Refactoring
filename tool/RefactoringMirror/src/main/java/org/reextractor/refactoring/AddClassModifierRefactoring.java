package org.reextractor.refactoring;

import org.reextractor.util.ClassUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.List;

public class AddClassModifierRefactoring implements Refactoring {

    private String modifier;
    private DeclarationNodeTree classBefore;
    private DeclarationNodeTree classAfter;

    public AddClassModifierRefactoring(String modifier, DeclarationNodeTree classBefore, DeclarationNodeTree classAfter) {
        this.modifier = modifier;
        this.classBefore = classBefore;
        this.classAfter = classAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.ADD_CLASS_MODIFIER;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(this.classBefore.codeRange()
                .setDescription("original class declaration")
                .setCodeElement(ClassUtils.typeDeclaration2String(classBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(this.classAfter.codeRange()
                .setDescription("class declaration with added modifier")
                .setCodeElement(ClassUtils.typeDeclaration2String(classAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(modifier);
        sb.append(" in class ");
        sb.append(ClassUtils.typeDeclaration2String(classAfter));
        return sb.toString();
    }

    public String getModifier() {
        return modifier;
    }

    public DeclarationNodeTree getClassBefore() {
        return classBefore;
    }

    public DeclarationNodeTree getClassAfter() {
        return classAfter;
    }
}
