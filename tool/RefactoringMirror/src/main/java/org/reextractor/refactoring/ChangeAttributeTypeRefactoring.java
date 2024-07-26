package org.reextractor.refactoring;

import org.reextractor.util.AttributeUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.List;

public class ChangeAttributeTypeRefactoring implements Refactoring {

    private DeclarationNodeTree originalAttribute;
    private DeclarationNodeTree changedTypeAttribute;

    public ChangeAttributeTypeRefactoring(DeclarationNodeTree originalAttribute, DeclarationNodeTree changedTypeAttribute) {
        this.originalAttribute = originalAttribute;
        this.changedTypeAttribute = changedTypeAttribute;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.CHANGE_ATTRIBUTE_TYPE;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(originalAttribute.codeRange()
                .setDescription("original attribute declaration")
                .setCodeElement(AttributeUtils.attribute2QualifiedString(originalAttribute)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(changedTypeAttribute.codeRange()
                .setDescription("changed-type attribute declaration")
                .setCodeElement(AttributeUtils.attribute2QualifiedString(changedTypeAttribute)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(AttributeUtils.attribute2QualifiedString(originalAttribute));
        sb.append(" to ");
        sb.append(AttributeUtils.attribute2QualifiedString(changedTypeAttribute));
        sb.append(" in class ").append(changedTypeAttribute.getNamespace());
        return sb.toString();
    }

    public DeclarationNodeTree getOriginalAttribute() {
        return originalAttribute;
    }

    public DeclarationNodeTree getChangedTypeAttribute() {
        return changedTypeAttribute;
    }
}
