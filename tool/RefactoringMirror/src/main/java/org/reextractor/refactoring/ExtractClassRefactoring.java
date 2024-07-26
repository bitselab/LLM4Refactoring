package org.reextractor.refactoring;

import org.reextractor.util.AttributeUtils;
import org.reextractor.util.ClassUtils;
import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExtractClassRefactoring implements Refactoring {

    private DeclarationNodeTree extractedClass;
    private DeclarationNodeTree originalClass;
    private DeclarationNodeTree nextClass;
    private Map<DeclarationNodeTree, DeclarationNodeTree> extractedOperations;
    private Map<DeclarationNodeTree, DeclarationNodeTree> extractedAttributes;

    public ExtractClassRefactoring(DeclarationNodeTree originalClass, DeclarationNodeTree nextClass, DeclarationNodeTree extractedClass,
                                   Map<DeclarationNodeTree, DeclarationNodeTree> extractedOperations,
                                   Map<DeclarationNodeTree, DeclarationNodeTree> extractedAttributes) {
        this.originalClass = originalClass;
        this.nextClass = nextClass;
        this.extractedClass = extractedClass;
        this.extractedOperations = extractedOperations;
        this.extractedAttributes = extractedAttributes;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.EXTRACT_CLASS;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(originalClass.codeRange()
                .setDescription("original type declaration")
                .setCodeElement(ClassUtils.typeDeclaration2String(originalClass).strip()));
        for (DeclarationNodeTree extractedOperation : extractedOperations.keySet()) {
            ranges.add(extractedOperation.codeRange()
                    .setDescription("original method declaration")
                    .setCodeElement(MethodUtils.method2String(extractedOperation).strip()));
        }
        for (DeclarationNodeTree extractedAttribute : extractedAttributes.keySet()) {
            ranges.add(extractedAttribute.codeRange()
                    .setDescription("original attribute declaration")
                    .setCodeElement(AttributeUtils.attribute2String(extractedAttribute).strip()));
        }
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(nextClass.codeRange()
                .setDescription("type declaration after extraction")
                .setCodeElement(ClassUtils.typeDeclaration2String(nextClass).strip()));
        ranges.add(extractedClass.codeRange()
                .setDescription("extracted type declaration")
                .setCodeElement(ClassUtils.typeDeclaration2String(extractedClass).strip()));
        for (DeclarationNodeTree extractedOperation : extractedOperations.keySet()) {
            ranges.add(extractedOperations.get(extractedOperation).codeRange()
                    .setDescription("extracted method declaration")
                    .setCodeElement(MethodUtils.method2String(extractedOperations.get(extractedOperation)).strip()));
        }
        for (DeclarationNodeTree extractedAttribute : extractedAttributes.keySet()) {
            ranges.add(extractedAttributes.get(extractedAttribute).codeRange()
                    .setDescription("extracted attribute declaration")
                    .setCodeElement(AttributeUtils.attribute2String(extractedAttributes.get(extractedAttribute)).strip()));
        }
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(extractedClass.getNamespace()).append(".").append(extractedClass.getName());
        sb.append(" from class ");
        sb.append(originalClass.getNamespace()).append(".").append(originalClass.getName());
        return sb.toString();
    }

    public DeclarationNodeTree getExtractedClass() {
        return extractedClass;
    }

    public DeclarationNodeTree getOriginalClass() {
        return originalClass;
    }

    public DeclarationNodeTree getNextClass() {
        return nextClass;
    }
}
