package org.reextractor.refactoring;

import org.apache.commons.lang3.tuple.Pair;
import org.reextractor.util.MethodUtils;
import org.remapper.dto.CodeRange;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.StatementNodeTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ExtractOperationRefactoring implements Refactoring {

    private DeclarationNodeTree extractedOperation;
    private DeclarationNodeTree sourceOperationBeforeExtraction;
    private DeclarationNodeTree sourceOperationAfterExtraction;
    private List<StatementNodeTree> extractedCodeFragmentsFromSourceOperation;
    private List<StatementNodeTree> extractedCodeFragmentsToExtractedOperation;

    public ExtractOperationRefactoring(DeclarationNodeTree sourceOperationBeforeExtraction, DeclarationNodeTree sourceOperationAfterExtraction,
                                       DeclarationNodeTree extractedOperation, Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements) {
        this.sourceOperationBeforeExtraction = sourceOperationBeforeExtraction;
        this.sourceOperationAfterExtraction = sourceOperationAfterExtraction;
        this.extractedOperation = extractedOperation;
        this.extractedCodeFragmentsFromSourceOperation = new ArrayList<>();
        this.extractedCodeFragmentsToExtractedOperation = new ArrayList<>();
        for (Pair<StatementNodeTree, StatementNodeTree> matchedStatement : matchedStatements) {
            StatementNodeTree left = matchedStatement.getLeft();
            StatementNodeTree right = matchedStatement.getRight();
            if(left.getRoot() == sourceOperationBeforeExtraction.getMethodNode() && right.getRoot() == extractedOperation.getMethodNode()) {
                extractedCodeFragmentsFromSourceOperation.add(left);
                extractedCodeFragmentsToExtractedOperation.add(right);
            }
        }
        extractedCodeFragmentsFromSourceOperation.sort(Comparator.comparingInt(StatementNodeTree::getPosition));
        extractedCodeFragmentsToExtractedOperation.sort(Comparator.comparingInt(StatementNodeTree::getPosition));
    }

    public RefactoringType getRefactoringType() {
        return RefactoringType.EXTRACT_OPERATION;
    }

    public List<CodeRange> leftSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(sourceOperationBeforeExtraction.codeRange()
                .setDescription("source method declaration before extraction")
                .setCodeElement(MethodUtils.method2String(sourceOperationBeforeExtraction).strip()));
        for(StatementNodeTree extractedCodeFragment : extractedCodeFragmentsFromSourceOperation) {
            ranges.add(extractedCodeFragment.codeRange()
                    .setDescription("extracted code from source method declaration")
                    .setCodeElement(extractedCodeFragment.getExpression().strip()));
        }
        return ranges;
    }

    public List<CodeRange> rightSide() {
        List<CodeRange> ranges = new ArrayList<>();
        ranges.add(extractedOperation.codeRange()
                .setDescription("extracted method declaration")
                .setCodeElement(MethodUtils.method2String(extractedOperation).strip()));
        for(StatementNodeTree extractedCodeFragment : extractedCodeFragmentsToExtractedOperation) {
            ranges.add(extractedCodeFragment.codeRange()
                    .setDescription("extracted code to extracted method declaration")
                    .setCodeElement(extractedCodeFragment.getExpression().strip()));
        }
        ranges.add(sourceOperationAfterExtraction.codeRange()
                .setDescription("source method declaration after extraction")
                .setCodeElement(MethodUtils.method2String(sourceOperationAfterExtraction).strip()));
        return ranges;
    }

    public String getName() {
        return this.getRefactoringType().getDisplayName();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append("\t");
        sb.append(MethodUtils.method2String(extractedOperation));
        sb.append(" extracted from ");
        sb.append(MethodUtils.method2String(sourceOperationBeforeExtraction));
        sb.append(" in class ");
        sb.append(getClassName());
        return sb.toString();
    }

    private String getClassName() {
        String sourceClassName = sourceOperationBeforeExtraction.getNamespace();
        String targetClassName = sourceOperationAfterExtraction.getNamespace();
        return sourceClassName.equals(targetClassName) ? sourceClassName : targetClassName;
    }

    public DeclarationNodeTree getExtractedOperation() {
        return extractedOperation;
    }

    public DeclarationNodeTree getSourceOperationBeforeExtraction() {
        return sourceOperationBeforeExtraction;
    }

    public DeclarationNodeTree getSourceOperationAfterExtraction() {
        return sourceOperationAfterExtraction;
    }
}
