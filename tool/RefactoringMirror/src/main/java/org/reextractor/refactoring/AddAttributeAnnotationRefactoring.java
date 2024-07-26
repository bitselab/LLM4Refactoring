package org.reextractor.refactoring;

import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.reextractor.util.AnnotationUtils;
import org.reextractor.util.AttributeUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.EntityType;
import org.remapper.dto.LocationInfo;

import java.util.ArrayList;
import java.util.List;

public class AddAttributeAnnotationRefactoring implements Refactoring {

    private Annotation annotation;
    private DeclarationNodeTree attributeBefore;
    private DeclarationNodeTree attributeAfter;

    public AddAttributeAnnotationRefactoring(Annotation annotation, DeclarationNodeTree attributeBefore, DeclarationNodeTree attributeAfter) {
        this.annotation = annotation;
        this.attributeBefore = attributeBefore;
        this.attributeAfter = attributeAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.ADD_ATTRIBUTE_ANNOTATION;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(this.attributeBefore.codeRange()
                .setDescription("original attribute declaration")
                .setCodeElement(AttributeUtils.attribute2String(attributeBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo annotationLocation = new LocationInfo(
                (CompilationUnit) annotation.getRoot(), attributeAfter.getFilePath(), annotation);
        ranges.add(annotationLocation.codeRange()
                .setDescription("added annotation")
                .setCodeElement(AnnotationUtils.annotation2String(annotation)));
        ranges.add(this.attributeAfter.codeRange()
                .setDescription("attribute declaration with added annotation")
                .setCodeElement(AttributeUtils.attribute2String(attributeAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(AnnotationUtils.annotation2String(annotation));
        if (attributeAfter.getType() == EntityType.ENUM_CONSTANT) {
            sb.append(" in enum constant ");
        } else {
            sb.append(" in attribute ");
        }
        sb.append(AttributeUtils.attribute2String(attributeAfter));
        sb.append(" from class ");
        sb.append(attributeAfter.getNamespace());
        return sb.toString();
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public DeclarationNodeTree getAttributeBefore() {
        return attributeBefore;
    }

    public DeclarationNodeTree getAttributeAfter() {
        return attributeAfter;
    }
}
