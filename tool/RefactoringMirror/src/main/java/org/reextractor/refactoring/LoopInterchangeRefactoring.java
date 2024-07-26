package org.reextractor.refactoring;

import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.StatementNodeTree;

import java.util.ArrayList;
import java.util.List;

public class LoopInterchangeRefactoring implements Refactoring {

    private StatementNodeTree originalLoop;
    private StatementNodeTree interchangedLoop;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public LoopInterchangeRefactoring(StatementNodeTree originalLoop, StatementNodeTree interchangedLoop,
                                      DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.originalLoop = originalLoop;
        this.interchangedLoop = interchangedLoop;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.LOOP_INTERCHANGE;
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
        ranges.add(interchangedLoop.codeRange()
                .setDescription("interchanged loop declaration")
                .setCodeElement(interchangedLoop.getExpression()));
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with loop interchange")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(originalLoop);
        sb.append(" to ");
        sb.append(interchangedLoop);
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public String getOriginalLoop() {
        return originalLoop.getExpression();
    }

    public String getInterchangedLoop() {
        return interchangedLoop.getExpression();
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
