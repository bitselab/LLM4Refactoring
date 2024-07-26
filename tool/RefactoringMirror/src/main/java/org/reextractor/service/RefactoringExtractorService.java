package org.reextractor.service;

import org.eclipse.jgit.lib.Repository;
import org.reextractor.handler.RefactoringHandler;

import java.io.File;

public interface RefactoringExtractorService {

    /**
     * Detect refactorings performed in the specified commit.
     *
     * @param repository A git repository (from JGit library).
     * @param commitId   The SHA key that identifies the commit.
     * @param handler    A handler object that is responsible to process the detected refactorings.
     */
    void detectAtCommit(Repository repository, String commitId, RefactoringHandler handler);

    /**
     * Detect refactorings performed in the specified commit.
     *
     * @param repository A git repository (from JGit library).
     * @param commitId   The SHA key that identifies the commit.
     * @param handler    A handler object that is responsible to process the detected refactorings.
     * @param timeout    A timeout, in seconds. When timeout is reached, the operation stops and returns no refactorings.
     */
    void detectAtCommit(Repository repository, String commitId, RefactoringHandler handler, int timeout);

    /**
     * Detect refactorings performed between two files representing two versions of Java programs.
     *
     * @param previousFile The file corresponding to the previous version.
     * @param nextFile The file corresponding to the next version.
     * @param handler A handler object that is responsible to process the detected refactorings.
     */
    void detectAtFiles(File previousFile, File nextFile, RefactoringHandler handler);
}
