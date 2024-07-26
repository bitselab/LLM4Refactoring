package org.reextractor.refactoring;

import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.StatementNodeTree;

import java.util.ArrayList;
import java.util.List;

public class ChangeLoopTypeRefactoring implements Refactoring {

    private StatementNodeTree originalLoop;
    private StatementNodeTree changedLoop;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public ChangeLoopTypeRefactoring(StatementNodeTree originalLoop, StatementNodeTree changedLoop,
                                     DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.originalLoop = originalLoop;
        this.changedLoop = changedLoop;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.CHANGE_LOOP_TYPE;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(originalLoop.codeRange()
                .setDescription("original loop declaration")
                .setCodeElement(originalLoop.getExpression()));
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(changedLoop.codeRange()
                .setDescription("changed loop declaration")
                .setCodeElement(changedLoop.getExpression()));
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with changed loop type")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(originalLoop.getType().getName());
        sb.append(" to ");
        sb.append(changedLoop.getType().getName());
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public String getOriginalLoopType() {
        return originalLoop.getType().getName();
    }

    public String getChangedLoopType() {
        return changedLoop.getType().getName();
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
