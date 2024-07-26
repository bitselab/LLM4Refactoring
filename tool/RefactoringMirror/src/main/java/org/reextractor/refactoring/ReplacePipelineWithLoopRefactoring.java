package org.reextractor.refactoring;

import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.StatementNodeTree;
import org.remapper.dto.StatementType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReplacePipelineWithLoopRefactoring implements Refactoring {

    private StatementNodeTree pipeline;
    private StatementNodeTree loop;
    private DeclarationNodeTree operationBefore;
    private DeclarationNodeTree operationAfter;

    public ReplacePipelineWithLoopRefactoring(StatementNodeTree pipeline, StatementNodeTree loop, DeclarationNodeTree operationBefore, DeclarationNodeTree operationAfter) {
        this.pipeline = pipeline;
        this.loop = loop;
        this.operationBefore = operationBefore;
        this.operationAfter = operationAfter;
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.REPLACE_PIPELINE_WITH_LOOP;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(pipeline.codeRange()
                .setDescription("original code")
                .setCodeElement(pipeline.getExpression()));
        ranges.add(operationBefore.codeRange()
                .setDescription("original method declaration")
                .setCodeElement(MethodUtils.method2String(operationBefore)));
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(loop.codeRange()
                .setDescription("loop code")
                .setCodeElement(loop.getExpression()));
        List<StatementNodeTree> descendants = loop.getDescendants();
        descendants.sort(Comparator.comparingInt(StatementNodeTree::getPosition));
        for (StatementNodeTree statement : descendants) {
            if (statement.getType() == StatementType.BLOCK)
                continue;
            ranges.add(statement.codeRange()
                    .setDescription("loop code")
                    .setCodeElement(statement.getExpression()));
        }
        ranges.add(operationAfter.codeRange()
                .setDescription("method declaration with introduced pipeline")
                .setCodeElement(MethodUtils.method2String(operationAfter)));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(pipeline.getExpression().strip());
        sb.append(" with ");
        sb.append(loop.getExpression());
        sb.append(" in method ");
        sb.append(MethodUtils.method2String(operationAfter));
        sb.append(" from class ");
        sb.append(operationAfter.getNamespace());
        return sb.toString();
    }

    public String getPipeline() {
        return pipeline.getExpression();
    }

    public String getLoop() {
        return loop.getExpression();
    }

    public DeclarationNodeTree getOperationBefore() {
        return operationBefore;
    }

    public DeclarationNodeTree getOperationAfter() {
        return operationAfter;
    }
}
