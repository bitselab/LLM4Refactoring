package org.reextractor.refactoring;

import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.reextractor.util.AnnotationUtils;
import org.reextractor.util.ClassUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.LocationInfo;

import java.util.ArrayList;
import java.util.List;

public class AddClassAnnotationRefactoring implements Refactoring {

    private Annotation annotation;
    private DeclarationNodeTree classBefore;
    private DeclarationNodeTree classAfter;

    public AddClassAnnotationRefactoring(Annotation annotation, DeclarationNodeTree classBefore, DeclarationNodeTree classAfter) {
        this.annotation = annotation;
        this.classBefore = classBefore;
        this.classAfter = classAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.ADD_CLASS_ANNOTATION;
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
        LocationInfo annotationLocation = new LocationInfo(
                (CompilationUnit) annotation.getRoot(), classAfter.getFilePath(), annotation);
        ranges.add(annotationLocation.codeRange()
                .setDescription("added annotation")
                .setCodeElement(AnnotationUtils.annotation2String(annotation)));
        ranges.add(this.classAfter.codeRange()
                .setDescription("class declaration with added annotation")
                .setCodeElement(ClassUtils.typeDeclaration2String(classAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(AnnotationUtils.annotation2String(annotation));
        sb.append(" in class ");
        sb.append(ClassUtils.typeDeclaration2String(classAfter));
        return sb.toString();
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public DeclarationNodeTree getClassBefore() {
        return classBefore;
    }

    public DeclarationNodeTree getClassAfter() {
        return classAfter;
    }
}
