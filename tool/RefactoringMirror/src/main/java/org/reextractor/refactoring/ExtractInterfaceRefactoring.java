package org.reextractor.refactoring;

import org.reextractor.util.ClassUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ExtractInterfaceRefactoring implements Refactoring {

    private DeclarationNodeTree extractedClass;
    private Set<DeclarationNodeTree> subclassSetBefore;
    private Set<DeclarationNodeTree> subclassSetAfter;

    public ExtractInterfaceRefactoring(DeclarationNodeTree extractedClass, Set<DeclarationNodeTree> subclassSetBefore,
                                        Set<DeclarationNodeTree> subclassSetAfter) {
        this.extractedClass = extractedClass;
        this.subclassSetBefore = subclassSetBefore;
        this.subclassSetAfter = subclassSetAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.EXTRACT_INTERFACE;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        for (DeclarationNodeTree subclass : subclassSetBefore) {
            ranges.add(subclass.codeRange()
                    .setDescription("original sub-type declaration")
                    .setCodeElement(ClassUtils.typeDeclaration2String(subclass)));
        }
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        for (DeclarationNodeTree subclass : subclassSetAfter) {
            ranges.add(subclass.codeRange()
                    .setDescription("sub-type declaration after extraction")
                    .setCodeElement(ClassUtils.typeDeclaration2String(subclass)));
        }
        ranges.add(extractedClass.codeRange()
                .setDescription("extracted super-type declaration")
                .setCodeElement(ClassUtils.typeDeclaration2String(extractedClass)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(ClassUtils.typeDeclaration2String(extractedClass));
        sb.append(" from classes [");
        for (DeclarationNodeTree subclass : subclassSetBefore) {
            sb.append(ClassUtils.typeDeclaration2String(subclass));
            sb.append(",");
        }
        sb.delete(sb.length() - 1, sb.length());
        sb.append("]");
        return sb.toString();
    }

    public DeclarationNodeTree getExtractedClass() {
        return extractedClass;
    }

    public Set<String> getSubclassSetBefore() {
        Set<String> subclassSet = new LinkedHashSet<String>();
        for (DeclarationNodeTree umlClass : this.subclassSetBefore) {
            subclassSet.add(umlClass.getNamespace() + "." + umlClass.getName());
        }
        return subclassSet;
    }

    public Set<DeclarationNodeTree> getUMLSubclassSetBefore() {
        return new LinkedHashSet<DeclarationNodeTree>(subclassSetBefore);
    }

    public Set<String> getSubclassSetAfter() {
        Set<String> subclassSet = new LinkedHashSet<String>();
        for (DeclarationNodeTree umlClass : this.subclassSetAfter) {
            subclassSet.add(umlClass.getNamespace() + "." + umlClass.getName());
        }
        return subclassSet;
    }

    public Set<DeclarationNodeTree> getUMLSubclassSetAfter() {
        return new LinkedHashSet<DeclarationNodeTree>(subclassSetAfter);
    }
}
