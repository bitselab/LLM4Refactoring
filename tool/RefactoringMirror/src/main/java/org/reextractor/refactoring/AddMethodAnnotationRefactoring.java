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

public class AddMethodAnnotationRefactoring implements Refactoring {

    private Annotation annotation;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public AddMethodAnnotationRefactoring(Annotation annotation, DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.annotation = annotation;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.ADD_METHOD_ANNOTATION;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(this.operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo annotationLocation = new LocationInfo(
                (CompilationUnit) annotation.getRoot(), operationAfter.getFilePath(), annotation);
        ranges.add(annotationLocation.codeRange()
                .setDescription("added annotation")
                .setCodeElement(AnnotationUtils.annotation2String(annotation)));
        ranges.add(this.operationAfter.codeRange()
                .setDescription("method declaration with added annotation")
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
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
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
