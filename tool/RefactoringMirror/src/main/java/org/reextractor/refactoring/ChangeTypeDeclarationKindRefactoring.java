package org.reextractor.refactoring;

import org.reextractor.util.ClassUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.List;

public class ChangeTypeDeclarationKindRefactoring implements Refactoring {

    private DeclarationNodeTree classBefore;
    private DeclarationNodeTree classAfter;

    public ChangeTypeDeclarationKindRefactoring(DeclarationNodeTree classBefore, DeclarationNodeTree classAfter) {
        this.classBefore = classBefore;
        this.classAfter = classAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.CHANGE_TYPE_DECLARATION_KIND;
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
                .setDescription("class declaration with changed type declaration kind")
                .setCodeElement(ClassUtils.typeDeclaration2String(classAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(classBefore.getType().getName());
        sb.append(" to ");
        sb.append(classAfter.getType().getName());
        sb.append(" in type ");
        sb.append(ClassUtils.typeDeclaration2String(classAfter));
        return sb.toString();
    }

    public DeclarationNodeTree getClassBefore() {
        return classBefore;
    }

    public DeclarationNodeTree getClassAfter() {
        return classAfter;
    }
}
