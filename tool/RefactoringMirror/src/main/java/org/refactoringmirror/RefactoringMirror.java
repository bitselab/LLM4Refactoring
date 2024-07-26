package org.refactoringmirror;

import org.reextractor.handler.RefactoringHandler;
import org.reextractor.refactoring.Refactoring;
import org.reextractor.service.RefactoringExtractorService;
import org.reextractor.service.RefactoringExtractorServiceImpl;
import org.remapper.dto.CodeRange;

import java.io.File;
import java.util.List;

public class RefactoringMirror {

    public static void main(String[] args) {
        String path = "E:\\ReplicationPackage\\data\\bugs\\1";
        File before = new File(path + "\\before.java");
        File after = new File(path + "\\after.java");
        detect(before, after);
    }

    public static void detect(File before, File after) {
        RefactoringExtractorService service = new RefactoringExtractorServiceImpl();
        service.detectAtFiles(before, after, new RefactoringHandler() {
            @Override
            public void handle(String commitId, List<Refactoring> refactorings) {
                if (!refactorings.isEmpty()) {
                    System.out.println("The identified refactoring conducted by LLMs: ");
                    for (int i = 0; i < refactorings.size(); i++) {
                        Refactoring refactoring = refactorings.get(i);
                        System.out.println();
                        System.out.println((i + 1) + ".\t" + refactoring.toString());
                        List<CodeRange> codeRanges = refactoring.leftSide();
                        if (!codeRanges.isEmpty()) {
                            System.out.println("The code elements involved in the refactoring: [");
                            for (CodeRange codeRange : codeRanges) {
                                System.out.println(codeRange.toString());
                            }
                            System.out.println("]");
                        }
                    }
                }
            }

            @Override
            public void handleException(String commit, Exception e) {
                System.err.println("Error processing commit " + commit);
                e.printStackTrace(System.err);
            }
        });
    }
}
