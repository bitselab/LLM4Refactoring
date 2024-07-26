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

public class ModifyAttributeAnnotationRefactoring implements Refactoring {

    private Annotation annotationBefore;
    private Annotation annotationAfter;
    private DeclarationNodeTree attributeBefore;
    private DeclarationNodeTree attributeAfter;

    public ModifyAttributeAnnotationRefactoring(Annotation annotationBefore, Annotation annotationAfter, DeclarationNodeTree attributeBefore, DeclarationNodeTree attributeAfter) {
        this.annotationBefore = annotationBefore;
        this.annotationAfter = annotationAfter;
        this.attributeBefore = attributeBefore;
        this.attributeAfter = attributeAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.MODIFY_ATTRIBUTE_ANNOTATION;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo annotationLocation = new LocationInfo(
                (CompilationUnit) annotationBefore.getRoot(), attributeBefore.getFilePath(), annotationBefore);
        ranges.add(annotationLocation.codeRange()
                .setDescription("original annotation")
                .setCodeElement(AnnotationUtils.annotation2String(annotationBefore)));
        ranges.add(attributeBefore.codeRange()
                .setDescription("original attribute declaration")
                .setCodeElement(AttributeUtils.attribute2String(attributeBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo annotationLocation = new LocationInfo(
                (CompilationUnit) annotationAfter.getRoot(), attributeAfter.getFilePath(), annotationAfter);
        ranges.add(annotationLocation.codeRange()
                .setDescription("modified annotation")
                .setCodeElement(AnnotationUtils.annotation2String(annotationAfter)));
        ranges.add(attributeAfter.codeRange()
                .setDescription("attribute declaration with modified annotation")
                .setCodeElement(AttributeUtils.attribute2String(attributeAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(AnnotationUtils.annotation2String(annotationBefore));
        sb.append(" to ");
        sb.append(AnnotationUtils.annotation2String(annotationAfter));
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

    public Annotation getAnnotationBefore() {
        return annotationBefore;
    }

    public Annotation getAnnotationAfter() {
        return annotationAfter;
    }

    public DeclarationNodeTree getAttributeBefore() {
        return attributeBefore;
    }

    public DeclarationNodeTree getAttributeAfter() {
        return attributeAfter;
    }
}
