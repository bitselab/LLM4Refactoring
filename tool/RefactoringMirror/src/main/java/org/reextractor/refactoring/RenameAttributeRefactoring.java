package org.reextractor.refactoring;

import org.reextractor.util.AttributeUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.List;

public class RenameAttributeRefactoring implements Refactoring {

    private DeclarationNodeTree originalAttribute;
    private DeclarationNodeTree renamedAttribute;

    public RenameAttributeRefactoring(DeclarationNodeTree originalAttribute, DeclarationNodeTree renamedAttribute) {
        this.originalAttribute = originalAttribute;
        this.renamedAttribute = renamedAttribute;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.RENAME_ATTRIBUTE;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(originalAttribute.codeRange()
                .setDescription("original attribute declaration")
                .setCodeElement(AttributeUtils.attribute2QualifiedString(originalAttribute).strip()));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(renamedAttribute.codeRange()
                .setDescription("renamed attribute declaration")
                .setCodeElement(AttributeUtils.attribute2QualifiedString(renamedAttribute).strip()));
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
        sb.append(AttributeUtils.attribute2QualifiedString(renamedAttribute));
        sb.append(" in class ").append(renamedAttribute.getNamespace());
        return sb.toString();
    }

    public DeclarationNodeTree getOriginalAttribute() {
        return originalAttribute;
    }

    public DeclarationNodeTree getRenamedAttribute() {
        return renamedAttribute;
    }
}
