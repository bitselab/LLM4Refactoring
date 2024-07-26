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

public class RemoveMethodAnnotationRefactoring implements Refactoring {

    private Annotation annotation;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public RemoveMethodAnnotationRefactoring(Annotation annotation, DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.annotation = annotation;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.REMOVE_METHOD_ANNOTATION;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo annotationLocation = new LocationInfo(
                (CompilationUnit) annotation.getRoot(), operationBefore.getFilePath(), annotation);
        ranges.add(annotationLocation.codeRange()
                .setDescription("removed annotation")
                .setCodeElement(AnnotationUtils.annotation2String(annotation)));
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with removed annotation")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(AnnotationUtils.annotation2String(annotation));
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationBefore));
        sb.append(" from class ");
        sb.append(operationBefore.getNamespace());
        return sb.toString();
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
