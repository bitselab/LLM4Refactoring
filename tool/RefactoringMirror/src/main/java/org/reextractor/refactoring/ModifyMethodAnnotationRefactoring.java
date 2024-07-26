package org.reextractor.refactoring;

import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.reextractor.util.AnnotationUtils;
import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.LocationInfo;

import java.util.ArrayList;
import java.util.List;

public class ModifyMethodAnnotationRefactoring implements Refactoring {

    private Annotation annotationBefore;
    private Annotation annotationAfter;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public ModifyMethodAnnotationRefactoring(Annotation annotationBefore, Annotation annotationAfter, DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.annotationBefore = annotationBefore;
        this.annotationAfter = annotationAfter;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.MODIFY_METHOD_ANNOTATION;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo annotationLocation = new LocationInfo(
                (CompilationUnit) annotationBefore.getRoot(), operationBefore.getFilePath(), annotationBefore);
        ranges.add(annotationLocation.codeRange()
                .setDescription("original annotation")
                .setCodeElement(AnnotationUtils.annotation2String(annotationBefore)));
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo annotationLocation = new LocationInfo(
                (CompilationUnit) annotationAfter.getRoot(), operationAfter.getFilePath(), annotationAfter);
        ranges.add(annotationLocation.codeRange()
                .setDescription("modified annotation")
                .setCodeElement(AnnotationUtils.annotation2String(annotationAfter)));
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with modified annotation")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
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
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public Annotation getAnnotationBefore() {
        return annotationBefore;
    }

    public Annotation getAnnotationAfter() {
        return annotationAfter;
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
