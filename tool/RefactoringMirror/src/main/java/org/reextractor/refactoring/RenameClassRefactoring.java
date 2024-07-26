package org.reextractor.refactoring;

import org.reextractor.util.ClassUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.List;

public class RenameClassRefactoring implements Refactoring {

    private DeclarationNodeTree originalClass;
    private DeclarationNodeTree renamedClass;

    public RenameClassRefactoring(DeclarationNodeTree originalClass, DeclarationNodeTree renamedClass) {
        this.originalClass = originalClass;
        this.renamedClass = renamedClass;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.RENAME_CLASS;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(originalClass.codeRange()
                .setDescription("original type declaration")
                .setCodeElement(ClassUtils.typeDeclaration2String(originalClass)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(renamedClass.codeRange()
                .setDescription("renamed type declaration")
                .setCodeElement(ClassUtils.typeDeclaration2String(renamedClass)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(ClassUtils.typeDeclaration2String(originalClass));
        sb.append(" renamed to ");
        sb.append(ClassUtils.typeDeclaration2String(renamedClass));
        return sb.toString();
    }

    public DeclarationNodeTree getOriginalClass() {
        return originalClass;
    }

    public DeclarationNodeTree getRenamedClass() {
        return renamedClass;
    }
}
