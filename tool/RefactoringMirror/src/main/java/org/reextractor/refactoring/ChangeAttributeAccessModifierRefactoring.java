package org.reextractor.refactoring;

import org.reextractor.dto.Visibility;
import org.reextractor.util.AttributeUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.List;

public class ChangeAttributeAccessModifierRefactoring implements Refactoring {

    private Visibility originalAccessModifier;
    private Visibility changedAccessModifier;
    private DeclarationNodeTree attributeBefore;
    private DeclarationNodeTree attributeAfter;

    public ChangeAttributeAccessModifierRefactoring(Visibility originalAccessModifier, Visibility changedAccessModifier,
                                                    DeclarationNodeTree attributeBefore, DeclarationNodeTree attributeAfter) {
        this.originalAccessModifier = originalAccessModifier;
        this.changedAccessModifier = changedAccessModifier;
        this.attributeBefore = attributeBefore;
        this.attributeAfter = attributeAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.CHANGE_ATTRIBUTE_ACCESS_MODIFIER;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(attributeBefore.codeRange()
                .setDescription("original attribute declaration")
                .setCodeElement(AttributeUtils.attribute2String(attributeBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(attributeAfter.codeRange()
                .setDescription("attribute declaration with changed access modifier")
                .setCodeElement(AttributeUtils.attribute2String(attributeAfter)));
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
        sb.append(" in attribute ");
        sb.append(AttributeUtils.attribute2String(attributeAfter));
        sb.append(" from class ");
        sb.append(attributeAfter.getNamespace());
        return sb.toString();
    }

    public Visibility getOriginalAccessModifier() {
        return originalAccessModifier;
    }

    public Visibility getChangedAccessModifier() {
        return changedAccessModifier;
    }

    public DeclarationNodeTree getAttributeBefore() {
        return attributeBefore;
    }

    public DeclarationNodeTree getAttributeAfter() {
        return attributeAfter;
    }
}
