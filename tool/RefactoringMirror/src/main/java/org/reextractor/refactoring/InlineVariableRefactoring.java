package org.reextractor.refactoring;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.reextractor.util.MethodUtils;
import org.reextractor.util.VariableUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.LocationInfo;
import org.remapper.dto.StatementNodeTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InlineVariableRefactoring implements Refactoring {

    private VariableDeclaration variableDeclaration;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;
    private Map<StatementNodeTree, StatementNodeTree> references;

    public InlineVariableRefactoring(VariableDeclaration variableDeclaration, DeclarationNodeTree operationBefore,
                                     DeclarationNodeTree operationAfter, Map<StatementNodeTree, StatementNodeTree> references) {
        this.variableDeclaration = variableDeclaration;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
        this.references = references;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.INLINE_VARIABLE;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        LocationInfo variableLocation = new LocationInfo(
                (CompilationUnit) variableDeclaration.getRoot(), operationBefore.getFilePath(), variableDeclaration);
        ranges.add(variableLocation.codeRange()
                .setDescription("inlined variable declaration")
                .setCodeElement(VariableUtils.variable2String(variableDeclaration).strip()));
        for (StatementNodeTree mapping : references.keySet()) {
            ranges.add(mapping.codeRange()
                    .setDescription("statement with the name of the inlined variable")
                    .setCodeElement(mapping.getExpression().strip()));
        }
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore).strip()));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        for (StatementNodeTree mapping : references.keySet()) {
            ranges.add(references.get(mapping).codeRange()
                    .setDescription("statement with the initializer of the inlined variable")
                    .setCodeElement(references.get(mapping).getExpression().strip()));
        }
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with inlined variable")
                .setCodeElement(MethodUtils.method2String(operationAfter).strip()));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(VariableUtils.variable2String(variableDeclaration));
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationBefore));
        sb.append(" from class ");
        sb.append(operationBefore.getNamespace());
        return sb.toString();
    }

    public VariableDeclaration getVariableDeclaration() {
        return variableDeclaration;
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
