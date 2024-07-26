package org.reextractor.refactoring;

import org.reextractor.util.AttributeUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.List;

public class MoveAttributeRefactoring implements Refactoring {

    private DeclarationNodeTree originalAttribute;
    private DeclarationNodeTree movedAttribute;

    public MoveAttributeRefactoring(DeclarationNodeTree originalAttribute, DeclarationNodeTree movedAttribute) {
        this.originalAttribute = originalAttribute;
        this.movedAttribute = movedAttribute;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.MOVE_ATTRIBUTE;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(originalAttribute.codeRange()
                .setDescription("original attribute declaration")
                .setCodeElement(AttributeUtils.attribute2String(originalAttribute).strip()));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(movedAttribute.codeRange()
                .setDescription("moved attribute declaration")
                .setCodeElement(AttributeUtils.attribute2String(movedAttribute).strip()));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(AttributeUtils.attribute2String(originalAttribute));
        sb.append(" from class ");
        sb.append(getSourceClassName());
        sb.append(" to ");
        sb.append(AttributeUtils.attribute2String(movedAttribute));
        sb.append(" from class ");
        sb.append(getTargetClassName());
        return sb.toString();
    }

    public String getSourceClassName() {
        return originalAttribute.getNamespace();
    }

    public String getTargetClassName() {
        return movedAttribute.getNamespace();
    }

    public DeclarationNodeTree getOriginalAttribute() {
        return originalAttribute;
    }

    public DeclarationNodeTree getMovedAttribute() {
        return movedAttribute;
    }
}
