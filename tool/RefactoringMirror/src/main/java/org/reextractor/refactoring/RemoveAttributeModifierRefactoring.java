package org.reextractor.refactoring;

import org.reextractor.util.AttributeUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.EntityType;

import java.util.ArrayList;
import java.util.List;

public class RemoveAttributeModifierRefactoring implements Refactoring {

    private String modifier;
    private DeclarationNodeTree attributeBefore;
    private DeclarationNodeTree attributeAfter;

    public RemoveAttributeModifierRefactoring(String modifier, DeclarationNodeTree attributeBefore, DeclarationNodeTree attributeAfter) {
        this.modifier = modifier;
        this.attributeBefore = attributeBefore;
        this.attributeAfter = attributeAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.REMOVE_ATTRIBUTE_MODIFIER;
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
                .setDescription("attribute declaration with removed modifier")
                .setCodeElement(AttributeUtils.attribute2String(attributeAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(modifier);
        if (attributeBefore.getType() == EntityType.ENUM_CONSTANT) {
            sb.append(" in enum constant ");
        } else {
            sb.append(" in attribute ");
        }
        sb.append(AttributeUtils.attribute2String(attributeBefore));
        sb.append(" from class ");
        sb.append(attributeBefore.getNamespace());
        return sb.toString();
    }

    public String getModifier() {
        return modifier;
    }

    public DeclarationNodeTree getAttributeBefore() {
        return attributeBefore;
    }

    public DeclarationNodeTree getAttributeAfter() {
        return attributeAfter;
    }
}
