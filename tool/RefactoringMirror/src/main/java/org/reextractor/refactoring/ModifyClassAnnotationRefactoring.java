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

public class ModifyClassAnnotationRefactoring implements Refactoring {

    private Annotation annotationBefore;
    private Annotation annotationAfter;
    private DeclarationNodeTree classBefore;
    private DeclarationNodeTree classAfter;

    public ModifyClassAnnotationRefactoring(Annotation annotationBefore, Annotation annotationAfter, DeclarationNodeTree classBefore, DeclarationNodeTree classAfter) {
        this.annotationBefore = annotationBefore;
        this.annotationAfter = annotationAfter;
        this.classBefore = classBefore;
        this.classAfter = classAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.MODIFY_CLASS_ANNOTATION;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo annotationLocation = new LocationInfo(
                (CompilationUnit) annotationBefore.getRoot(), classBefore.getFilePath(), annotationBefore);
        ranges.add(annotationLocation.codeRange()
                .setDescription("original annotation")
                .setCodeElement(AnnotationUtils.annotation2String(annotationBefore)));
        ranges.add(classBefore.codeRange()
                .setDescription("original class declaration")
                .setCodeElement(ClassUtils.typeDeclaration2String(classBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo annotationLocation = new LocationInfo(
                (CompilationUnit) annotationAfter.getRoot(), classAfter.getFilePath(), annotationAfter);
        ranges.add(annotationLocation.codeRange()
                .setDescription("modified annotation")
                .setCodeElement(AnnotationUtils.annotation2String(annotationAfter)));
        ranges.add(classAfter.codeRange()
                .setDescription("class declaration with modified annotation")
                .setCodeElement(ClassUtils.typeDeclaration2String(classAfter)));
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
        sb.append(" in class ");
        sb.append(ClassUtils.typeDeclaration2String(classAfter));
        return sb.toString();
    }

    public Annotation getAnnotationBefore() {
        return annotationBefore;
    }

    public Annotation getAnnotationAfter() {
        return annotationAfter;
    }

    public DeclarationNodeTree getClassBefore() {
        return classBefore;
    }

    public DeclarationNodeTree getClassAfter() {
        return classAfter;
    }
}
