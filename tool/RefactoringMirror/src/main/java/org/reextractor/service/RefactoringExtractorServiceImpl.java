package org.reextractor.service;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.reextractor.dto.AnnotationListDiff;
import org.reextractor.dto.Visibility;
import org.reextractor.handler.RefactoringHandler;
import org.reextractor.refactoring.*;
import org.reextractor.util.MethodUtils;
import org.reextractor.util.StringUtils;
import org.remapper.dto.*;
import org.remapper.handler.MatchingHandler;
import org.remapper.service.EntityMatcherService;
import org.remapper.service.EntityMatcherServiceImpl;
import org.remapper.util.DiceFunction;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class RefactoringExtractorServiceImpl implements RefactoringExtractorService {

    @Override
    public void detectAtCommit(Repository repository, String commitId, RefactoringHandler handler) {
        RevWalk walk = new RevWalk(repository);
        try {
            RevCommit commit = walk.parseCommit(repository.resolve(commitId));
            if (commit.getParentCount() > 0) {
                walk.parseCommit(commit.getParent(0));
                this.detectRefactorings(repository, handler, commit);
            }
        } catch (MissingObjectException ignored) {
        } catch (Exception e) {
            handler.handleException(commitId, e);
        } finally {
            walk.close();
            walk.dispose();
        }
    }

    @Override
    public void detectAtCommit(Repository repository, String commitId, RefactoringHandler handler, int timeout) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<?> f = null;
        try {
            Runnable r = () -> detectAtCommit(repository, commitId, handler);
            f = service.submit(r);
            f.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            f.cancel(true);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            service.shutdown();
        }
    }

    protected void detectRefactorings(Repository repository, final RefactoringHandler handler, RevCommit currentCommit) throws Exception {
        List<Refactoring> refactoringsAtRevision;
        EntityMatcherService service = new EntityMatcherServiceImpl();
        String commitId = currentCommit.getId().getName();
        if (currentCommit.getParentCount() > 0) {
            MatchPair matchPair = service.matchEntities(repository, currentCommit, new MatchingHandler() {
            });
            refactoringsAtRevision = detectRefactorings(matchPair);
        } else {
            refactoringsAtRevision = Collections.emptyList();
        }
        handler.handle(commitId, refactoringsAtRevision);
    }

    public void detectAtFiles(File previousFile, File nextFile, RefactoringHandler handler) {
        List<Refactoring> refactoringsAtRevision = Collections.emptyList();
        EntityMatcherService service = new EntityMatcherServiceImpl();
        String id = previousFile.getName() + " -> " + nextFile.getName();
        try {
            MatchPair matchPair = service.matchEntities(previousFile, nextFile, new MatchingHandler() {
            });
            refactoringsAtRevision = detectRefactorings(matchPair);
        } catch (Exception e) {
            handler.handleException(id, e);
        }
        handler.handle(id, refactoringsAtRevision);
    }

    protected List<Refactoring> detectRefactorings(MatchPair matchPair) {
        List<Refactoring> refactorings = new ArrayList<>();
        Set<Pair<DeclarationNodeTree, DeclarationNodeTree>> matchedEntities = matchPair.getMatchedEntities();
        Set<DeclarationNodeTree> inlinedEntities = matchPair.getInlinedEntities();
        Set<DeclarationNodeTree> extractedEntities = matchPair.getExtractedEntities();
        Set<DeclarationNodeTree> addedEntities = matchPair.getAddedEntities();
        Set<DeclarationNodeTree> deletedEntities = matchPair.getDeletedEntities();
        Set<Pair<MethodNode, MethodNode>> methodNodePairs = mapMethodNodePairs(matchedEntities, inlinedEntities, extractedEntities);
        Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements = matchPair.getMatchedStatements();
        Set<StatementNodeTree> deletedStatements = matchPair.getDeletedStatements();
        Set<StatementNodeTree> addedStatements = matchPair.getAddedStatements();

        detectRefactoringsInMatchedEntities(matchedEntities, refactorings);
        detectRefactoringsBetweenMatchedAndAddedEntities(matchedEntities, extractedEntities, addedEntities, matchedStatements, refactorings);
        detectRefactoringsBetweenMatchedDeletedEntities(matchedEntities, inlinedEntities, deletedEntities, matchedStatements, matchPair, refactorings);

        detectRefactoringsInMatchedStatements(matchedStatements, refactorings);
        detectRefactoringsBetweenMatchedAndAddedStatements(methodNodePairs, matchedStatements, addedStatements, refactorings);
        detectRefactoringsBetweenMatchedAndDeletedStatements(methodNodePairs, matchedStatements, deletedStatements, refactorings);
        detectRefactoringsBetweenAddedAndDeletedStatements(methodNodePairs, addedStatements, deletedStatements, matchPair, refactorings);
        return refactorings;
    }

    private Set<Pair<MethodNode, MethodNode>> mapMethodNodePairs(Set<Pair<DeclarationNodeTree, DeclarationNodeTree>> matchedEntities,
                                                                 Set<DeclarationNodeTree> inlinedEntities,
                                                                 Set<DeclarationNodeTree> extractedEntities) {
        Set<Pair<MethodNode, MethodNode>> methodNodePairs = new LinkedHashSet<>();
        for (Pair<DeclarationNodeTree, DeclarationNodeTree> matchedEntity : matchedEntities) {
            DeclarationNodeTree left = matchedEntity.getLeft();
            DeclarationNodeTree right = matchedEntity.getRight();
            if (left.getType() == EntityType.METHOD && right.getType() == EntityType.METHOD)
                methodNodePairs.add(Pair.of(left.getMethodNode(), right.getMethodNode()));
        }
        for (DeclarationNodeTree inlinedEntity : inlinedEntities) {
            if (inlinedEntity.getType() == EntityType.METHOD) {
                List<EntityInfo> dependencies = inlinedEntity.getDependencies();
                for (Pair<DeclarationNodeTree, DeclarationNodeTree> matchedEntity : matchedEntities) {
                    DeclarationNodeTree left = matchedEntity.getLeft();
                    DeclarationNodeTree right = matchedEntity.getRight();
                    if (left.getType() == EntityType.METHOD && right.getType() == EntityType.METHOD) {
                        if (dependencies.contains(left.getEntity())) {
                            methodNodePairs.add(Pair.of(inlinedEntity.getMethodNode(), right.getMethodNode()));
                        }
                    }
                }
            }
        }
        for (DeclarationNodeTree extractedEntity : extractedEntities) {
            if (extractedEntity.getType() == EntityType.METHOD) {
                List<EntityInfo> dependencies = extractedEntity.getDependencies();
                for (Pair<DeclarationNodeTree, DeclarationNodeTree> matchedEntity : matchedEntities) {
                    DeclarationNodeTree left = matchedEntity.getLeft();
                    DeclarationNodeTree right = matchedEntity.getRight();
                    if (left.getType() == EntityType.METHOD && right.getType() == EntityType.METHOD) {
                        if (dependencies.contains(right.getEntity())) {
                            methodNodePairs.add(Pair.of(left.getMethodNode(), extractedEntity.getMethodNode()));
                        }
                    }
                }
            }
        }
        return methodNodePairs;
    }

    private void detectRefactoringsInMatchedEntities(Set<Pair<DeclarationNodeTree, DeclarationNodeTree>> matchedEntities,
                                                     List<Refactoring> refactorings) {
        for (Pair<DeclarationNodeTree, DeclarationNodeTree> pair : matchedEntities) {
            DeclarationNodeTree oldEntity = pair.getLeft();
            DeclarationNodeTree newEntity = pair.getRight();
            boolean isMove = !oldEntity.getNamespace().equals(newEntity.getNamespace()) &&
                    !matchedEntities.contains(Pair.of(oldEntity.getParent(), newEntity.getParent()));
            if (oldEntity.getType() == EntityType.METHOD && newEntity.getType() == EntityType.METHOD) {
                processOperations(isMove, matchedEntities, oldEntity, newEntity, refactorings);
            } else if (oldEntity.getType() == EntityType.FIELD && newEntity.getType() == EntityType.FIELD) {
                processAttributes(isMove, matchedEntities, oldEntity, newEntity, refactorings);
            } else if (oldEntity.getType() == EntityType.ENUM_CONSTANT && newEntity.getType() == EntityType.ENUM_CONSTANT) {
                processEnumConstants(isMove, oldEntity, newEntity, refactorings);
            } else if ((oldEntity.getType() == EntityType.CLASS || oldEntity.getType() == EntityType.INTERFACE ||
                    oldEntity.getType() == EntityType.ENUM) || (newEntity.getType() == EntityType.CLASS ||
                    newEntity.getType() == EntityType.INTERFACE || newEntity.getType() == EntityType.ENUM)) {
                processClasses(isMove, oldEntity, newEntity, refactorings);
            }
        }
    }

    private void processOperations(boolean isMove, Set<Pair<DeclarationNodeTree, DeclarationNodeTree>> matchedEntities,
                                   DeclarationNodeTree oldEntity, DeclarationNodeTree newEntity, List<Refactoring> refactorings) {
        MethodDeclaration removedOperation = (MethodDeclaration) oldEntity.getDeclaration();
        MethodDeclaration addedOperation = (MethodDeclaration) newEntity.getDeclaration();
        if (isMove) {
            if (isSubTypeOf(matchedEntities, oldEntity, newEntity)) {
                PullUpOperationRefactoring refactoring = new PullUpOperationRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            } else if (isSubTypeOf(matchedEntities, newEntity, oldEntity)) {
                PushDownOperationRefactoring refactoring = new PushDownOperationRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            } else {
                if (!oldEntity.getName().equals(newEntity.getName())) {
                    MoveAndRenameOperationRefactoring refactoring = new MoveAndRenameOperationRefactoring(oldEntity, newEntity);
                    refactorings.add(refactoring);
                } else {
                    MoveOperationRefactoring refactoring = new MoveOperationRefactoring(oldEntity, newEntity);
                    refactorings.add(refactoring);
                }
            }
        } else {
            if (!oldEntity.getName().equals(newEntity.getName()) &&
                    !(removedOperation.isConstructor() && addedOperation.isConstructor())) {
                RenameOperationRefactoring refactoring = new RenameOperationRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            }
        }
        checkForOperationModifierChanges(oldEntity, newEntity, refactorings);
        checkForOperationParameterChanges(oldEntity, newEntity, refactorings);
    }

    private void processAttributes(boolean isMove, Set<Pair<DeclarationNodeTree, DeclarationNodeTree>> matchedEntities, DeclarationNodeTree oldEntity, DeclarationNodeTree newEntity,
                                   List<Refactoring> refactorings) {
        FieldDeclaration removedAttribute = (FieldDeclaration) oldEntity.getDeclaration();
        FieldDeclaration addedAttribute = (FieldDeclaration) newEntity.getDeclaration();
        if (isMove) {
            if (isSubTypeOf(matchedEntities, oldEntity, newEntity)) {
                PullUpAttributeRefactoring refactoring = new PullUpAttributeRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            } else if (isSubTypeOf(matchedEntities, newEntity, oldEntity)) {
                PushDownAttributeRefactoring refactoring = new PushDownAttributeRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            } else {
                if (!oldEntity.getName().equals(newEntity.getName())) {
                    MoveAndRenameAttributeRefactoring refactoring = new MoveAndRenameAttributeRefactoring(oldEntity, newEntity);
                    refactorings.add(refactoring);
                } else {
                    MoveAttributeRefactoring refactoring = new MoveAttributeRefactoring(oldEntity, newEntity);
                    refactorings.add(refactoring);
                }
            }
        } else {
            if (!oldEntity.getName().equals(newEntity.getName())) {
                RenameAttributeRefactoring refactoring = new RenameAttributeRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            }
        }
        checkForAttributeModifierChanges(oldEntity, newEntity, refactorings);
        List<MethodDeclaration> oldMethods = new ArrayList<>();
        List<MethodDeclaration> newMethods = new ArrayList<>();
        oldEntity.getDeclaration().accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration node) {
                oldMethods.add(node);
                return true;
            }
        });
        newEntity.getDeclaration().accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration node) {
                newMethods.add(node);
                return true;
            }
        });
        for (MethodDeclaration oldMethod : oldMethods) {
            for (MethodDeclaration newMethod : newMethods) {
                String originalType = oldMethod.getReturnType2() == null ? "" : oldMethod.getReturnType2().toString();
                String changedType = newMethod.getReturnType2() == null ? "" : newMethod.getReturnType2().toString();
                if (oldMethod.getName().getIdentifier().equals(newMethod.getName().getIdentifier()) &&
                        StringUtils.equals(originalType, changedType) &&
                        oldMethod.parameters().size() == newMethod.parameters().size() && equalsParameters(oldMethod, newMethod)) {
                    DeclarationNodeTree leftEntity = new LeafNode((CompilationUnit) oldMethod.getRoot(), oldEntity.getLocationInfo().getFilePath(), oldMethod);
                    DeclarationNodeTree rightEntity = new LeafNode((CompilationUnit) newMethod.getRoot(), newEntity.getLocationInfo().getFilePath(), newMethod);
                    leftEntity.setDeclaration(oldMethod);
                    leftEntity.setType(EntityType.METHOD);
                    leftEntity.setParent(new InternalNode((CompilationUnit) oldMethod.getRoot(), oldEntity.getLocationInfo().getFilePath(), oldMethod));
                    leftEntity.getParent().setType(EntityType.CLASS);
                    if (oldMethod.getParent() instanceof AnonymousClassDeclaration && oldMethod.getParent().getParent() instanceof ClassInstanceCreation)
                        leftEntity.setNamespace(oldEntity.getNamespace() + "." + oldEntity.getName() + ".new " +
                                ((ClassInstanceCreation) oldMethod.getParent().getParent()).getType().toString());
                    rightEntity.setDeclaration(newMethod);
                    rightEntity.setType(EntityType.METHOD);
                    rightEntity.setParent(new InternalNode((CompilationUnit) oldMethod.getRoot(), oldEntity.getLocationInfo().getFilePath(), oldMethod));
                    rightEntity.getParent().setType(EntityType.CLASS);
                    if (newMethod.getParent() instanceof AnonymousClassDeclaration && newMethod.getParent().getParent() instanceof ClassInstanceCreation)
                        rightEntity.setNamespace(newEntity.getNamespace() + "." + newEntity.getName() + ".new " +
                                ((ClassInstanceCreation) newMethod.getParent().getParent()).getType().toString());
                    checkForOperationModifierChanges(leftEntity, rightEntity, refactorings);
                }
            }
        }
    }

    private void processEnumConstants(boolean isMove, DeclarationNodeTree oldEntity, DeclarationNodeTree newEntity,
                                      List<Refactoring> refactorings) {
        if (isMove) {
            if (!oldEntity.getName().equals(newEntity.getName())) {
                MoveAndRenameAttributeRefactoring refactoring = new MoveAndRenameAttributeRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            } else {
                MoveAttributeRefactoring refactoring = new MoveAttributeRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            }
        } else {
            if (!oldEntity.getName().equals(newEntity.getName())) {
                RenameAttributeRefactoring refactoring = new RenameAttributeRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            }
        }
    }

    private void processClasses(boolean isMove, DeclarationNodeTree oldEntity, DeclarationNodeTree newEntity,
                                List<Refactoring> refactorings) {
        if (isMove) {
            if (!oldEntity.getName().equals(newEntity.getName())) {
                MoveAndRenameClassRefactoring refactoring = new MoveAndRenameClassRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            } else {
                MoveClassRefactoring refactoring = new MoveClassRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            }
        } else {
            if (!oldEntity.getName().equals(newEntity.getName())) {
                RenameClassRefactoring refactoring = new RenameClassRefactoring(oldEntity, newEntity);
                refactorings.add(refactoring);
            }
        }
        if (oldEntity.getType() != newEntity.getType()) {
            ChangeTypeDeclarationKindRefactoring refactoring = new ChangeTypeDeclarationKindRefactoring(oldEntity, newEntity);
            refactorings.add(refactoring);
        }
        checkForClassModifierChanges(oldEntity, newEntity, refactorings);
    }

    private void checkForOperationModifierChanges(DeclarationNodeTree oldEntity, DeclarationNodeTree newEntity, List<Refactoring> refactorings) {
        MethodDeclaration removedOperation = (MethodDeclaration) oldEntity.getDeclaration();
        MethodDeclaration addedOperation = (MethodDeclaration) newEntity.getDeclaration();
        int methodModifiers1 = removedOperation.getModifiers();
        int methodModifiers2 = addedOperation.getModifiers();
        Visibility originalAccessModifier = getVisibility(methodModifiers1, oldEntity);
        Visibility changedAccessModifier = getVisibility(methodModifiers2, newEntity);
        if (originalAccessModifier != changedAccessModifier) {
            ChangeOperationAccessModifierRefactoring refactoring = new ChangeOperationAccessModifierRefactoring(originalAccessModifier, changedAccessModifier, oldEntity, newEntity);
            refactorings.add(refactoring);
        }
    }

    private Visibility getVisibility(int modifiers, DeclarationNodeTree entity) {
        Visibility visibility;
        boolean isInterfaceMethod = entity.getParent().getType() == EntityType.INTERFACE;
        if ((modifiers & Modifier.PUBLIC) != 0)
            visibility = Visibility.PUBLIC;
        else if ((modifiers & Modifier.PROTECTED) != 0)
            visibility = Visibility.PROTECTED;
        else if ((modifiers & Modifier.PRIVATE) != 0)
            visibility = Visibility.PRIVATE;
        else if (isInterfaceMethod)
            visibility = Visibility.PUBLIC;
        else
            visibility = Visibility.PACKAGE;
        return visibility;
    }

    private void checkForOperationParameterChanges(DeclarationNodeTree oldEntity, DeclarationNodeTree newEntity,
                                                   List<Refactoring> refactorings) {
        MethodDeclaration removedOperation = (MethodDeclaration) oldEntity.getDeclaration();
        MethodDeclaration addedOperation = (MethodDeclaration) newEntity.getDeclaration();
        List<SingleVariableDeclaration> addedParameters = new ArrayList<>();
        List<SingleVariableDeclaration> removedParameters = new ArrayList<>();
        List<AbstractMap.SimpleEntry<SingleVariableDeclaration, SingleVariableDeclaration>> matchedParameters = new ArrayList<>();
        List<Pair<SingleVariableDeclaration, SingleVariableDeclaration>> parameterDiffList = new ArrayList<>();
        for (Object obj1 : removedOperation.parameters()) {
            SingleVariableDeclaration parameter1 = (SingleVariableDeclaration) obj1;
            boolean found = false;
            for (Object obj2 : addedOperation.parameters()) {
                SingleVariableDeclaration parameter2 = (SingleVariableDeclaration) obj2;
                if (equalsIncludingName(parameter1, parameter2)) {
                    matchedParameters.add(new AbstractMap.SimpleEntry<>(parameter1, parameter2));
                    found = true;
                    break;
                }
            }
            if (!found) {
                removedParameters.add(parameter1);
            }
        }
        for (Object obj1 : addedOperation.parameters()) {
            SingleVariableDeclaration parameter1 = (SingleVariableDeclaration) obj1;
            boolean found = false;
            for (Object obj2 : removedOperation.parameters()) {
                SingleVariableDeclaration parameter2 = (SingleVariableDeclaration) obj2;
                if (equalsIncludingName(parameter1, parameter2)) {
                    matchedParameters.add(new AbstractMap.SimpleEntry<>(parameter2, parameter1));
                    found = true;
                    break;
                }
            }
            if (!found) {
                addedParameters.add(parameter1);
            }
        }
        for (AbstractMap.SimpleEntry<SingleVariableDeclaration, SingleVariableDeclaration> matchedParameter : matchedParameters) {
            SingleVariableDeclaration parameter1 = matchedParameter.getKey();
            SingleVariableDeclaration parameter2 = matchedParameter.getValue();
            parameterDiffList.add(Pair.of(parameter1, parameter2));
        }
        int matchedParameterCount = matchedParameters.size() / 2;
        List<String> parameterNames1 = new ArrayList<>();
        List<SingleVariableDeclaration> parameters1 = removedOperation.parameters();
        for (SingleVariableDeclaration singleVariableDeclaration : parameters1) {
            String identifier = singleVariableDeclaration.getName().getIdentifier();
            parameterNames1.add(identifier);
        }
        for (SingleVariableDeclaration removedParameter : removedParameters) {
            parameterNames1.remove(removedParameter.getName().getIdentifier());
        }
        List<String> parameterNames2 = new ArrayList<>();
        List<SingleVariableDeclaration> parameters2 = addedOperation.parameters();
        for (SingleVariableDeclaration parameter : parameters2) {
            String identifier = parameter.getName().getIdentifier();
            parameterNames2.add(identifier);
        }
        for (SingleVariableDeclaration addedParameter : addedParameters) {
            parameterNames2.remove(addedParameter.getName().getIdentifier());
        }
        if (matchedParameterCount == parameterNames1.size() && matchedParameterCount == parameterNames2.size() &&
                parameterNames1.size() > 1 && !parameterNames1.equals(parameterNames2)) {
            ReorderParameterRefactoring refactoring = new ReorderParameterRefactoring(oldEntity, newEntity);
            refactorings.add(refactoring);
        }
        //first round match parameters with the same name
        for (Iterator<SingleVariableDeclaration> removedParameterIterator = removedParameters.iterator(); removedParameterIterator.hasNext(); ) {
            SingleVariableDeclaration removedParameter = removedParameterIterator.next();
            for (Iterator<SingleVariableDeclaration> addedParameterIterator = addedParameters.iterator(); addedParameterIterator.hasNext(); ) {
                SingleVariableDeclaration addedParameter = addedParameterIterator.next();
                if (removedParameter.getName().getIdentifier().equals(addedParameter.getName().getIdentifier())) {
                    parameterDiffList.add(Pair.of(removedParameter, addedParameter));
                    addedParameterIterator.remove();
                    removedParameterIterator.remove();
                    break;
                }
            }
        }
        //second round match parameters with the same type
        for (Iterator<SingleVariableDeclaration> removedParameterIterator = removedParameters.iterator(); removedParameterIterator.hasNext(); ) {
            SingleVariableDeclaration removedParameter = removedParameterIterator.next();
            for (Iterator<SingleVariableDeclaration> addedParameterIterator = addedParameters.iterator(); addedParameterIterator.hasNext(); ) {
                SingleVariableDeclaration addedParameter = addedParameterIterator.next();
                if (removedParameter.getType().toString().equals(addedParameter.getType().toString()) &&
                        !existsAnotherAddedParameterWithTheSameType(removedOperation, addedOperation, addedParameters, addedParameter)) {
                    parameterDiffList.add(Pair.of(removedParameter, addedParameter));
                    addedParameterIterator.remove();
                    removedParameterIterator.remove();
                    break;
                }
            }
        }
        //third round match parameters with different type and name
        List<SingleVariableDeclaration> removedParametersWithoutReturnType = removedOperation.parameters();
        List<SingleVariableDeclaration> addedParametersWithoutReturnType = addedOperation.parameters();
        if (matchedParameterCount == removedParametersWithoutReturnType.size() - 1 && matchedParameterCount == addedParametersWithoutReturnType.size() - 1) {
            for (Iterator<SingleVariableDeclaration> removedParameterIterator = removedParameters.iterator(); removedParameterIterator.hasNext(); ) {
                SingleVariableDeclaration removedParameter = removedParameterIterator.next();
                int indexOfRemovedParameter = indexOfParameter(removedParametersWithoutReturnType, removedParameter);
                for (Iterator<SingleVariableDeclaration> addedParameterIterator = addedParameters.iterator(); addedParameterIterator.hasNext(); ) {
                    SingleVariableDeclaration addedParameter = addedParameterIterator.next();
                    int indexOfAddedParameter = indexOfParameter(addedParametersWithoutReturnType, addedParameter);
                    if (indexOfRemovedParameter == indexOfAddedParameter &&
                            usedParameters(removedOperation, addedOperation, removedParameter, addedParameter)) {
                        parameterDiffList.add(Pair.of(removedParameter, addedParameter));
                        addedParameterIterator.remove();
                        removedParameterIterator.remove();
                        break;
                    }
                }
            }
        }

        //fourth round match parameters with the same type
        if (removedParameters.size() == addedParameters.size()) {
            boolean sameType = true;
            for (int i = 0; i < removedParameters.size(); i++) {
                SingleVariableDeclaration removedParameter = removedParameters.get(i);
                SingleVariableDeclaration addedParameter = addedParameters.get(i);
                if (!removedParameter.getType().toString().equals(addedParameter.getType().toString())) {
                    sameType = false;
                    break;
                }
            }
            if (sameType) {
                for (int i = 0; i < removedParameters.size(); i++) {
                    SingleVariableDeclaration removedParameter = removedParameters.get(i);
                    SingleVariableDeclaration addedParameter = addedParameters.get(i);
                    parameterDiffList.add(Pair.of(removedParameter, addedParameter));
                }
                removedParameters.clear();
                addedParameters.clear();
            }
        }
        getParameterRefactorings(parameterDiffList, addedParameters, removedParameters, oldEntity, newEntity, refactorings);
    }

    private void getParameterRefactorings(List<Pair<SingleVariableDeclaration, SingleVariableDeclaration>> parameterDiffList,
                                          List<SingleVariableDeclaration> addedParameters, List<SingleVariableDeclaration> removedParameters,
                                          DeclarationNodeTree oldEntity, DeclarationNodeTree newEntity, List<Refactoring> refactorings) {
        for (Pair<SingleVariableDeclaration, SingleVariableDeclaration> parameterDiff : parameterDiffList) {
            SingleVariableDeclaration parameter1 = parameterDiff.getLeft();
            SingleVariableDeclaration parameter2 = parameterDiff.getRight();
            if (!parameter1.getName().getIdentifier().equals(parameter2.getName().getIdentifier())) {
                RenameParameterRefactoring refactoring = new RenameParameterRefactoring(parameter1, parameter2, oldEntity, newEntity);
                refactorings.add(refactoring);
            }
        }
    }

    private boolean usedParameters(MethodDeclaration removedOperation, MethodDeclaration addedOperation,
                                   SingleVariableDeclaration removedParameter, SingleVariableDeclaration addedParameter) {
        List<String> removedOperationVariables = getAllVariables(removedOperation);
        List<String> addedOperationVariables = getAllVariables(addedOperation);
        if (removedOperationVariables.contains(removedParameter.getName().getIdentifier()) ==
                addedOperationVariables.contains(addedParameter.getName().getIdentifier())) {
            if (!removedOperation.isConstructor() && !addedOperation.isConstructor()) {
                return !removedOperationVariables.contains(addedParameter.getName().getIdentifier()) &&
                        !addedOperationVariables.contains(removedParameter.getName().getIdentifier());
            } else {
                return true;
            }
        }
        return false;
    }

    private List<String> getAllVariables(MethodDeclaration methodDeclaration) {
        List<String> operationVariables = new ArrayList<>();
        methodDeclaration.accept(new ASTVisitor() {
            @Override
            public boolean visit(VariableDeclarationFragment node) {
                operationVariables.add(node.getName().getIdentifier());
                return true;
            }
        });
        return operationVariables;
    }

    private int indexOfParameter(List<SingleVariableDeclaration> parameters, SingleVariableDeclaration parameter) {
        int index = 0;
        for (SingleVariableDeclaration p : parameters) {
            if (equalsIncludingName(p, parameter)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private boolean existsAnotherAddedParameterWithTheSameType(MethodDeclaration removedOperation, MethodDeclaration addedOperation,
                                                               List<SingleVariableDeclaration> addedParameters, SingleVariableDeclaration parameter) {
        if (hasTwoParametersWithTheSameType(removedOperation) && hasTwoParametersWithTheSameType(addedOperation)) {
            return false;
        }
        for (SingleVariableDeclaration addedParameter : addedParameters) {
            if (!addedParameter.getName().equals(parameter.getName()) &&
                    addedParameter.getType().toString().equals(parameter.getType().toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTwoParametersWithTheSameType(MethodDeclaration methodDeclaration) {
        List<SingleVariableDeclaration> parameterTypes = methodDeclaration.parameters();
        if (parameterTypes.size() == 2) {
            if (parameterTypes.get(0).getType().toString().equals(parameterTypes.get(1).getType().toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean equalsIncludingName(SingleVariableDeclaration parameter1, SingleVariableDeclaration parameter2) {
        return parameter1.getName().getIdentifier().equals(parameter2.getName().getIdentifier()) &&
                parameter1.getType().toString().equals(parameter2.getType().toString()) &&
                parameter1.isVarargs() == parameter2.isVarargs();
    }

    private void checkForAttributeModifierChanges(DeclarationNodeTree oldEntity, DeclarationNodeTree newEntity, List<Refactoring> refactorings) {
        FieldDeclaration removedAttribute = (FieldDeclaration) oldEntity.getDeclaration();
        FieldDeclaration addedAttribute = (FieldDeclaration) newEntity.getDeclaration();
        int attributeModifiers1 = removedAttribute.getModifiers();
        int attributeModifiers2 = addedAttribute.getModifiers();
        Visibility originalAccessModifier = getVisibility(attributeModifiers1, oldEntity);
        Visibility changedAccessModifier = getVisibility(attributeModifiers2, newEntity);
        if (originalAccessModifier != changedAccessModifier) {
            ChangeAttributeAccessModifierRefactoring refactoring = new ChangeAttributeAccessModifierRefactoring(originalAccessModifier, changedAccessModifier, oldEntity, newEntity);
            refactorings.add(refactoring);
        }
    }

    private void checkForClassModifierChanges(DeclarationNodeTree oldEntity, DeclarationNodeTree newEntity, List<Refactoring> refactorings) {
        AbstractTypeDeclaration removedClass = (AbstractTypeDeclaration) oldEntity.getDeclaration();
        AbstractTypeDeclaration addedClass = (AbstractTypeDeclaration) newEntity.getDeclaration();
        int methodModifiers1 = removedClass.getModifiers();
        int methodModifiers2 = addedClass.getModifiers();
        Visibility originalAccessModifier = getVisibility(methodModifiers1, oldEntity);
        Visibility changedAccessModifier = getVisibility(methodModifiers2, newEntity);
        if (originalAccessModifier != changedAccessModifier) {
            ChangeClassAccessModifierRefactoring refactoring = new ChangeClassAccessModifierRefactoring(originalAccessModifier, changedAccessModifier, oldEntity, newEntity);
            refactorings.add(refactoring);
        }
    }

    private void detectRefactoringsBetweenMatchedAndAddedEntities(Set<Pair<DeclarationNodeTree, DeclarationNodeTree>> matchedEntities,
                                                                  Set<DeclarationNodeTree> extractedEntities, Set<DeclarationNodeTree> addedEntities,
                                                                  Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                                                  List<Refactoring> refactorings) {
        for (DeclarationNodeTree extractedEntity : addedEntities) {
            if (extractedEntity.getType() == EntityType.METHOD) {
                List<EntityInfo> dependencies = extractedEntity.getDependencies();
                for (Pair<DeclarationNodeTree, DeclarationNodeTree> pair : matchedEntities) {
                    DeclarationNodeTree oldEntity = pair.getLeft();
                    DeclarationNodeTree newEntity = pair.getRight();
                    if (oldEntity.getType() == EntityType.METHOD && newEntity.getType() == EntityType.METHOD) {
                        if (!dependencies.contains(newEntity.getEntity()) && !hasMethodInvocation(newEntity, extractedEntity))
                            continue;
                        if (MethodUtils.isNewFunction(oldEntity.getDeclaration(), newEntity.getDeclaration()))
                            continue;
                        if (MethodUtils.isGetter((MethodDeclaration) extractedEntity.getDeclaration()) || MethodUtils.isSetter((MethodDeclaration) extractedEntity.getDeclaration()))
                            continue;
                        double dice = DiceFunction.calculateBodyDice((LeafNode) oldEntity, (LeafNode) newEntity, (LeafNode) extractedEntity);
                        if (dice < 0.16 && !matchedLOCAreGreaterThanUnmatchedLOC(oldEntity, newEntity, extractedEntity, true, matchedStatements))
                            continue;
                        boolean isMove = !oldEntity.getNamespace().equals(extractedEntity.getNamespace()) &&
                                !matchedEntities.contains(Pair.of(oldEntity.getParent(), extractedEntity.getParent()));
                        if (isMove) {
                            ExtractAndMoveOperationRefactoring refactoring = new ExtractAndMoveOperationRefactoring(oldEntity, newEntity, extractedEntity, matchedStatements);
                            refactorings.add(refactoring);
                        } else {
                            ExtractOperationRefactoring refactoring = new ExtractOperationRefactoring(oldEntity, newEntity, extractedEntity, matchedStatements);
                            refactorings.add(refactoring);
                        }
                    }
                }
            }
        }
        for (DeclarationNodeTree addedEntity : addedEntities) {
            if (addedEntity.getType() == EntityType.CLASS || addedEntity.getType() == EntityType.INTERFACE) {
                Set<DeclarationNodeTree> mapping = new HashSet<>();
                Map<DeclarationNodeTree, DeclarationNodeTree> extractedOperations = new TreeMap<>(Comparator.comparingInt(startLine -> startLine.getLocationInfo().getStartLine()));
                Map<DeclarationNodeTree, DeclarationNodeTree> extractedAttributes = new TreeMap<>(Comparator.comparingInt(startLine -> startLine.getLocationInfo().getStartLine()));
                for (Pair<DeclarationNodeTree, DeclarationNodeTree> pair : matchedEntities) {
                    DeclarationNodeTree oldEntity = pair.getLeft();
                    DeclarationNodeTree newEntity = pair.getRight();
                    if (oldEntity.getType() == EntityType.METHOD && newEntity.getType() == EntityType.METHOD && newEntity.getParent() == addedEntity) {
                        mapping.add(oldEntity.getParent());
                        extractedOperations.put(oldEntity, newEntity);
                    }
                    if (oldEntity.getType() == EntityType.FIELD && newEntity.getType() == EntityType.FIELD && newEntity.getParent() == addedEntity) {
                        mapping.add(oldEntity.getParent());
                        extractedAttributes.put(oldEntity, newEntity);
                    }
                }
                Set<DeclarationNodeTree> subclassSetBefore = new LinkedHashSet<>();
                Set<DeclarationNodeTree> subclassSetAfter = new LinkedHashSet<>();
                for (Pair<DeclarationNodeTree, DeclarationNodeTree> pair : matchedEntities) {
                    DeclarationNodeTree oldEntity = pair.getLeft();
                    if (mapping.contains(oldEntity)) {
                        DeclarationNodeTree newEntity = pair.getRight();
                        if (newEntity.getType() == EntityType.CLASS || newEntity.getType() == EntityType.INTERFACE) {
                            TypeDeclaration newClass = (TypeDeclaration) newEntity.getDeclaration();
                            TypeDeclaration addedClass = (TypeDeclaration) addedEntity.getDeclaration();
                            if (!isSubTypeOf(newClass, addedClass) && !isSubTypeOf(addedClass, newClass)) {
                                ExtractClassRefactoring refactoring = new ExtractClassRefactoring(oldEntity, newEntity, addedEntity, extractedOperations, extractedAttributes);
                                refactorings.add(refactoring);
                            }
                            if (isSubTypeOf(newClass, addedClass)) {
                                subclassSetBefore.add(oldEntity);
                                subclassSetAfter.add(newEntity);
                            } else if (isSubTypeOf(addedClass, newClass)) {
                                ExtractSubClassRefactoring refactoring = new ExtractSubClassRefactoring(oldEntity, newEntity, addedEntity, extractedOperations, extractedAttributes);
                                refactorings.add(refactoring);
                            }
                        }
                    }
                }
                if (!subclassSetBefore.isEmpty()) {
                    if (addedEntity.getType() == EntityType.INTERFACE) {
                        ExtractInterfaceRefactoring refactoring = new ExtractInterfaceRefactoring(addedEntity, subclassSetBefore, subclassSetAfter);
                        refactorings.add(refactoring);
                    } else {
                        ExtractSuperClassRefactoring refactoring = new ExtractSuperClassRefactoring(addedEntity, subclassSetBefore, subclassSetAfter);
                        refactorings.add(refactoring);
                    }
                }
            }
        }
    }

    private boolean hasMethodInvocation(DeclarationNodeTree node1, DeclarationNodeTree node2) {
        MethodDeclaration declaration1 = (MethodDeclaration) node1.getDeclaration();
        MethodDeclaration declaration2 = (MethodDeclaration) node2.getDeclaration();
        List<MethodInvocation> invocations = new ArrayList<>();
        declaration1.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodInvocation node) {
                if (node.getName().getIdentifier().equals(declaration2.getName().getIdentifier()) &&
                        node.arguments().size() == declaration2.parameters().size())
                    invocations.add(node);
                return true;
            }
        });
        return !invocations.isEmpty() && StringUtils.equals(node1.getNamespace(), node2.getNamespace());
    }

    private boolean matchedLOCAreGreaterThanUnmatchedLOC(DeclarationNodeTree oldEntity, DeclarationNodeTree newEntity,
                                                         DeclarationNodeTree anotherEntity, boolean isExtracted,
                                                         Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements) {
        MethodNode methodNode = anotherEntity.getMethodNode();
        List<StatementNodeTree> statements = methodNode.getMatchedStatements();
        int totalLOC = methodNode.getAllControls().size() + methodNode.getAllBlocks().size() + methodNode.getAllOperations().size() - 1;
        int matchedLOC = 0;
        // isExtracted == true ? extract method : inline method
        if (isExtracted) {
            for (StatementNodeTree snt : statements) {
                for (Pair<StatementNodeTree, StatementNodeTree> pair : matchedStatements) {
                    StatementNodeTree oldStatement = pair.getLeft();
                    StatementNodeTree newStatement = pair.getRight();
                    if (snt == newStatement && oldStatement.getRoot().getMethodEntity() == oldEntity) {
                        matchedLOC += 1;
                    }
                }
            }
        } else {
            for (StatementNodeTree snt : statements) {
                for (Pair<StatementNodeTree, StatementNodeTree> pair : matchedStatements) {
                    StatementNodeTree oldStatement = pair.getLeft();
                    StatementNodeTree newStatement = pair.getRight();
                    if (snt == oldStatement && newStatement.getRoot().getMethodEntity() == newEntity) {
                        matchedLOC += 1;
                    }
                }
            }
        }
        int unMatchedLOC = totalLOC - matchedLOC;
        return matchedLOC > unMatchedLOC;
    }

    private void detectRefactoringsBetweenMatchedDeletedEntities(Set<Pair<DeclarationNodeTree, DeclarationNodeTree>> matchedEntities,
                                                                 Set<DeclarationNodeTree> inlinedEntities, Set<DeclarationNodeTree> deletedEntities,
                                                                 Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                                                 MatchPair matchPair, List<Refactoring> refactorings) {
        for (DeclarationNodeTree inlinedEntity : deletedEntities) {
            if (inlinedEntity.getType() == EntityType.METHOD) {
                List<EntityInfo> dependencies = inlinedEntity.getDependencies();
                for (Pair<DeclarationNodeTree, DeclarationNodeTree> pair : matchedEntities) {
                    DeclarationNodeTree oldEntity = pair.getLeft();
                    DeclarationNodeTree newEntity = pair.getRight();
                    if (oldEntity.getType() == EntityType.METHOD && newEntity.getType() == EntityType.METHOD) {
                        if (!dependencies.contains(oldEntity.getEntity()) && !hasMethodInvocation(oldEntity, inlinedEntity))
                            continue;
                        if (MethodUtils.isNewFunction(newEntity.getDeclaration(), oldEntity.getDeclaration()))
                            continue;
                        if (MethodUtils.isGetter((MethodDeclaration) inlinedEntity.getDeclaration()) || MethodUtils.isSetter((MethodDeclaration) inlinedEntity.getDeclaration()))
                            continue;
                        double dice = DiceFunction.calculateBodyDice((LeafNode) newEntity, (LeafNode) oldEntity, (LeafNode) inlinedEntity);
                        if (dice < 0.16 && !matchedLOCAreGreaterThanUnmatchedLOC(oldEntity, newEntity, inlinedEntity, false, matchedStatements))
                            continue;
                        boolean isMove = !inlinedEntity.getNamespace().equals(newEntity.getNamespace()) &&
                                !matchedEntities.contains(Pair.of(inlinedEntity.getParent(), newEntity.getParent()));
                        if (isMove) {
                            MoveAndInlineOperationRefactoring refactoring = new MoveAndInlineOperationRefactoring(oldEntity, newEntity, inlinedEntity, matchedStatements);
                            refactorings.add(refactoring);
                        } else {
                            InlineOperationRefactoring refactoring = new InlineOperationRefactoring(oldEntity, newEntity, inlinedEntity, matchedStatements);
                            refactorings.add(refactoring);
                        }
                    }
                }
            }
        }
        for (DeclarationNodeTree deletedEntity : deletedEntities) {
            if (deletedEntity.getType() != EntityType.METHOD)
                continue;
            DeclarationNodeTree parent = deletedEntity.getParent();
            if (parent.getType() != EntityType.CLASS)
                continue;
            for (Pair<DeclarationNodeTree, DeclarationNodeTree> matchedEntity : matchedEntities) {
                DeclarationNodeTree oldEntity = matchedEntity.getLeft();
                DeclarationNodeTree newEntity = matchedEntity.getRight();
                if (oldEntity.getType() != EntityType.CLASS && oldEntity.getType() != EntityType.INTERFACE)
                    continue;
                TypeDeclaration deletedClass = (TypeDeclaration) parent.getDeclaration();
                TypeDeclaration oldClass = (TypeDeclaration) oldEntity.getDeclaration();
                if (!isSubTypeOf(deletedClass, oldClass))
                    continue;
                List<DeclarationNodeTree> children = newEntity.getChildren();
                boolean isOverride = false;
                DeclarationNodeTree overrideMethod = null;
                for (DeclarationNodeTree child : children) {
                    if (child.getType() != EntityType.METHOD)
                        continue;
                    String deletedParams = deletedEntity.getEntity().getParams();
                    String newParams = child.getEntity().getParams();
                    if (StringUtils.equals(deletedParams, newParams)) {
                        isOverride = true;
                        overrideMethod = child;
                        break;
                    }
                }
                if (isOverride && DiceFunction.calculateSimilarity(matchPair, deletedEntity, overrideMethod) >= DiceFunction.minSimilarity) {
                    PullUpOperationRefactoring refactoring = new PullUpOperationRefactoring(deletedEntity, overrideMethod);
                    refactorings.add(refactoring);
                }
            }
        }
    }

    private DeclarationNodeTree findMatchedEntity(Set<Pair<DeclarationNodeTree, DeclarationNodeTree>> matchedEntities, DeclarationNodeTree entity) {
        for (Pair<DeclarationNodeTree, DeclarationNodeTree> pair : matchedEntities) {
            if (pair.getLeft() == entity)
                return pair.getRight();
            if (pair.getRight() == entity)
                return pair.getLeft();
        }
        return null;
    }

    private boolean isSubTypeOf(Set<Pair<DeclarationNodeTree, DeclarationNodeTree>> matchedEntities, DeclarationNodeTree oldEntity, DeclarationNodeTree newEntity) {
        DeclarationNodeTree oldParent = oldEntity.getParent();
        DeclarationNodeTree newParent = newEntity.getParent();
        if ((oldParent.getType() == EntityType.CLASS || oldParent.getType() == EntityType.INTERFACE) &&
                (newParent.getType() == EntityType.CLASS || newParent.getType() == EntityType.INTERFACE)) {
            TypeDeclaration removedClass = (TypeDeclaration) oldParent.getDeclaration();
            TypeDeclaration addedClass = (TypeDeclaration) newParent.getDeclaration();
            DeclarationNodeTree matchedAddedEntity = findMatchedEntity(matchedEntities, newParent);
            TypeDeclaration matchedAddedClass = matchedAddedEntity == null ? null : (TypeDeclaration) (matchedAddedEntity.getDeclaration());
            DeclarationNodeTree matchedDeletedEntity = findMatchedEntity(matchedEntities, oldParent);
            TypeDeclaration matchedDeletedClass = matchedDeletedEntity == null ? null : (TypeDeclaration) (matchedDeletedEntity.getDeclaration());
            return (matchedAddedClass != null && isSubTypeOf(removedClass, matchedAddedClass))
                    || (matchedDeletedClass != null && isSubTypeOf(matchedDeletedClass, addedClass));
        }
        return false;
    }

    private boolean isSubTypeOf(TypeDeclaration removedClass, TypeDeclaration addedClass) {
        ITypeBinding removedBinding = removedClass.resolveBinding();
        ITypeBinding addedBinding = addedClass.resolveBinding();
        boolean superClass;
        boolean isInterface;
        if (removedBinding != null) {
            if (removedBinding.getSuperclass() != null) {
                if (addedBinding != null) {
                    superClass = removedBinding.getSuperclass().getTypeDeclaration().isEqualTo(addedBinding) ||
                            StringUtils.equals(removedBinding.getSuperclass().getQualifiedName(), addedBinding.getQualifiedName());
                    if (superClass)
                        return true;
                }
            }
            ITypeBinding[] interfaces = removedBinding.getInterfaces();
            for (ITypeBinding typeBinding : interfaces) {
                if (addedBinding != null) {
                    isInterface = typeBinding.getTypeDeclaration().isEqualTo(addedBinding) ||
                            StringUtils.equals(typeBinding.getQualifiedName(), addedBinding.getQualifiedName());
                    if (isInterface)
                        return true;
                }
            }
        }
        return false;
    }

    private void detectRefactoringsInMatchedStatements(Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                                       List<Refactoring> refactorings) {
        for (Pair<StatementNodeTree, StatementNodeTree> pair : matchedStatements) {
            StatementNodeTree oldStatement = pair.getLeft();
            StatementNodeTree newStatement = pair.getRight();
            DeclarationNodeTree oldEntity = oldStatement.getRoot().getMethodEntity();
            DeclarationNodeTree newEntity = newStatement.getRoot().getMethodEntity();
            if (oldStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT && newStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT) {
                processVariableDeclarationStatement(oldStatement, newStatement, oldEntity, newEntity, refactorings);
            }
            if (oldStatement.getType() == StatementType.ENHANCED_FOR_STATEMENT && newStatement.getType() == StatementType.ENHANCED_FOR_STATEMENT) {
                processEnhancedForStatement(oldStatement, newStatement, oldEntity, newEntity, refactorings);
            }
            if (oldStatement.getType() == StatementType.FOR_STATEMENT && newStatement.getType() == StatementType.FOR_STATEMENT) {
                processForStatement(oldStatement, newStatement, oldEntity, newEntity, refactorings);
            }
            if (oldStatement.getType() == StatementType.CATCH_CLAUSE && newStatement.getType() == StatementType.CATCH_CLAUSE) {
                processCatchClause(oldStatement, newStatement, oldEntity, newEntity, refactorings);
            }
            if (oldStatement.getType() == StatementType.TRY_STATEMENT && newStatement.getType() == StatementType.TRY_STATEMENT) {
                processTryStatement(oldStatement, newStatement, oldEntity, newEntity, refactorings);
            }
            if ((oldStatement.getType() == StatementType.FOR_STATEMENT || oldStatement.getType() == StatementType.ENHANCED_FOR_STATEMENT ||
                    oldStatement.getType() == StatementType.WHILE_STATEMENT || oldStatement.getType() == StatementType.DO_STATEMENT) &&
                    (newStatement.getType() == StatementType.FOR_STATEMENT || newStatement.getType() == StatementType.ENHANCED_FOR_STATEMENT ||
                            newStatement.getType() == StatementType.WHILE_STATEMENT || newStatement.getType() == StatementType.DO_STATEMENT)) {
                processLoopStatement(oldStatement, newStatement, oldEntity, newEntity, matchedStatements, refactorings);
            }
            if ((oldStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT || oldStatement.getType() == StatementType.EXPRESSION_STATEMENT ||
                    oldStatement.getType() == StatementType.RETURN_STATEMENT) &&
                    (newStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT || newStatement.getType() == StatementType.EXPRESSION_STATEMENT ||
                            newStatement.getType() == StatementType.RETURN_STATEMENT)) {
                processAnonymousWithLambda(oldStatement, newStatement, oldEntity, newEntity, refactorings);
            }
            if (oldStatement.getType() == StatementType.IF_STATEMENT && newStatement.getType() == StatementType.IF_STATEMENT) {
                processInvertCondition(oldStatement, newStatement, oldEntity, newEntity, matchedStatements, refactorings);
            }
            if (oldStatement instanceof OperationNode && newStatement instanceof OperationNode) {
                List<LambdaExpression> oldLambdaExpressions = new ArrayList<>();
                List<LambdaExpression> newLambdaExpressions = new ArrayList<>();
                oldStatement.getStatement().accept(new ASTVisitor() {
                    @Override
                    public boolean visit(LambdaExpression node) {
                        oldLambdaExpressions.add(node);
                        return true;
                    }
                });
                newStatement.getStatement().accept(new ASTVisitor() {
                    @Override
                    public boolean visit(LambdaExpression node) {
                        newLambdaExpressions.add(node);
                        return true;
                    }
                });
                if (oldLambdaExpressions.size() == 1 && newLambdaExpressions.size() == 1) {
                    LambdaExpression oldLambdaExpression = oldLambdaExpressions.get(0);
                    LambdaExpression newLambdaExpression = newLambdaExpressions.get(0);
                    List<VariableDeclaration> oldParameters = oldLambdaExpression.parameters();
                    List<VariableDeclaration> newParameters = newLambdaExpression.parameters();
                    if (oldParameters.size() == 1 && newParameters.size() == 1) {
                        VariableDeclaration oldFragment = oldParameters.get(0);
                        VariableDeclaration newFragment = newParameters.get(0);
                        if (!oldFragment.getName().getIdentifier().equals(newFragment.getName().getIdentifier())) {
                            RenameVariableRefactoring refactoring = new RenameVariableRefactoring(oldFragment, newFragment, oldEntity, newEntity);
                            refactorings.add(refactoring);
                        }
                    }
                }
                List<MethodDeclaration> oldMethods = new ArrayList<>();
                List<MethodDeclaration> newMethods = new ArrayList<>();
                oldStatement.getStatement().accept(new ASTVisitor() {
                    @Override
                    public boolean visit(MethodDeclaration node) {
                        oldMethods.add(node);
                        return true;
                    }
                });
                newStatement.getStatement().accept(new ASTVisitor() {
                    @Override
                    public boolean visit(MethodDeclaration node) {
                        newMethods.add(node);
                        return true;
                    }
                });
                for (MethodDeclaration oldMethod : oldMethods) {
                    for (MethodDeclaration newMethod : newMethods) {
                        String originalType = oldMethod.getReturnType2() == null ? "" : oldMethod.getReturnType2().toString();
                        String changedType = newMethod.getReturnType2() == null ? "" : newMethod.getReturnType2().toString();
                        if (oldMethod.getName().getIdentifier().equals(newMethod.getName().getIdentifier()) &&
                                StringUtils.equals(originalType, changedType) &&
                                oldMethod.parameters().size() == newMethod.parameters().size() && equalsParameters(oldMethod, newMethod)) {
                            DeclarationNodeTree leftEntity = new LeafNode((CompilationUnit) oldMethod.getRoot(), oldStatement.getLocationInfo().getFilePath(), oldMethod);
                            DeclarationNodeTree rightEntity = new LeafNode((CompilationUnit) newMethod.getRoot(), newStatement.getLocationInfo().getFilePath(), newMethod);
                            leftEntity.setDeclaration(oldMethod);
                            leftEntity.setType(EntityType.METHOD);
                            leftEntity.setParent(new InternalNode((CompilationUnit) oldMethod.getRoot(), oldStatement.getLocationInfo().getFilePath(), oldMethod));
                            leftEntity.getParent().setType(EntityType.CLASS);
                            if (oldMethod.getParent() instanceof AnonymousClassDeclaration && oldMethod.getParent().getParent() instanceof ClassInstanceCreation)
                                leftEntity.setNamespace(oldStatement.getRoot().getMethodEntity().getNamespace() + "." + oldStatement.getRoot().getMethodEntity().getName() + ".new " +
                                        ((ClassInstanceCreation) oldMethod.getParent().getParent()).getType().toString());
                            rightEntity.setDeclaration(newMethod);
                            rightEntity.setType(EntityType.METHOD);
                            rightEntity.setParent(new InternalNode((CompilationUnit) oldMethod.getRoot(), oldStatement.getLocationInfo().getFilePath(), oldMethod));
                            rightEntity.getParent().setType(EntityType.CLASS);
                            if (newMethod.getParent() instanceof AnonymousClassDeclaration && newMethod.getParent().getParent() instanceof ClassInstanceCreation)
                                rightEntity.setNamespace(newStatement.getRoot().getMethodEntity().getNamespace() + "." + newStatement.getRoot().getMethodEntity().getName() + ".new " +
                                        ((ClassInstanceCreation) newMethod.getParent().getParent()).getType().toString());
                            checkForOperationModifierChanges(leftEntity, rightEntity, refactorings);
                        }
                    }
                }
            }
        }
    }

    private boolean equalsParameters(MethodDeclaration oldMethod, MethodDeclaration newMethod) {
        List<SingleVariableDeclaration> oldParameters = oldMethod.parameters();
        List<SingleVariableDeclaration> newParameters = newMethod.parameters();
        for (int i = 0; i < oldParameters.size(); i++) {
            SingleVariableDeclaration oldParameter = oldParameters.get(i);
            SingleVariableDeclaration newParameter = newParameters.get(i);
            if (!oldParameter.getName().getIdentifier().equals(newParameter.getName().getIdentifier()) ||
                    !oldParameter.getType().toString().equals(newParameter.getType().toString()))
                return false;
        }
        return true;
    }

    private void processVariableDeclarationStatement(StatementNodeTree oldStatement, StatementNodeTree newStatement, DeclarationNodeTree oldEntity,
                                                     DeclarationNodeTree newEntity, List<Refactoring> refactorings) {
        VariableDeclarationStatement statement1 = (VariableDeclarationStatement) oldStatement.getStatement();
        VariableDeclarationStatement statement2 = (VariableDeclarationStatement) newStatement.getStatement();
        List<VariableDeclarationFragment> fragments1 = statement1.fragments();
        List<VariableDeclarationFragment> fragments2 = statement2.fragments();
        for (int i = 0; i < Math.min(fragments1.size(), fragments2.size()); i++) {
            VariableDeclarationFragment fragment1 = fragments1.get(i);
            VariableDeclarationFragment fragment2 = fragments2.get(i);
            if (!fragment1.getName().getIdentifier().equals(fragment2.getName().getIdentifier())) {
                RenameVariableRefactoring refactoring = new RenameVariableRefactoring(fragment1, fragment2, oldEntity, newEntity);
                refactorings.add(refactoring);
            }
        }
    }

    private void processEnhancedForStatement(StatementNodeTree oldStatement, StatementNodeTree newStatement, DeclarationNodeTree oldEntity,
                                             DeclarationNodeTree newEntity, List<Refactoring> refactorings) {
        EnhancedForStatement statement1 = (EnhancedForStatement) oldStatement.getStatement();
        EnhancedForStatement statement2 = (EnhancedForStatement) newStatement.getStatement();
        SingleVariableDeclaration fragment1 = statement1.getParameter();
        SingleVariableDeclaration fragment2 = statement2.getParameter();
        if (!fragment1.getName().getIdentifier().equals(fragment2.getName().getIdentifier())) {
            RenameVariableRefactoring refactoring = new RenameVariableRefactoring(fragment1, fragment2, oldEntity, newEntity);
            refactorings.add(refactoring);
        }
    }

    private void processForStatement(StatementNodeTree oldStatement, StatementNodeTree newStatement, DeclarationNodeTree oldEntity,
                                     DeclarationNodeTree newEntity, List<Refactoring> refactorings) {
        ForStatement statement1 = (ForStatement) oldStatement.getStatement();
        ForStatement statement2 = (ForStatement) newStatement.getStatement();
        List initializers1 = statement1.initializers();
        List initializers2 = statement2.initializers();
        if (initializers1.size() == 1 && initializers2.size() == 1) {
            Object obj1 = initializers1.get(0);
            Object obj2 = initializers2.get(0);
            if (obj1 instanceof VariableDeclarationExpression && obj2 instanceof VariableDeclarationExpression) {
                VariableDeclarationExpression initializer1 = (VariableDeclarationExpression) obj1;
                VariableDeclarationExpression initializer2 = (VariableDeclarationExpression) obj2;
                List<VariableDeclarationFragment> fragments1 = initializer1.fragments();
                List<VariableDeclarationFragment> fragments2 = initializer2.fragments();
                VariableDeclarationFragment fragment1 = fragments1.get(0);
                VariableDeclarationFragment fragment2 = fragments2.get(0);
                if (!fragment1.getName().getIdentifier().equals(fragment2.getName().getIdentifier())) {
                    RenameVariableRefactoring refactoring = new RenameVariableRefactoring(fragment1, fragment2, oldEntity, newEntity);
                    refactorings.add(refactoring);
                }
            }
        }
    }

    private void processCatchClause(StatementNodeTree oldStatement, StatementNodeTree newStatement, DeclarationNodeTree oldEntity,
                                    DeclarationNodeTree newEntity, List<Refactoring> refactorings) {
        CatchClause statement1 = (CatchClause) oldStatement.getStatement();
        CatchClause statement2 = (CatchClause) newStatement.getStatement();
        SingleVariableDeclaration fragment1 = statement1.getException();
        SingleVariableDeclaration fragment2 = statement2.getException();
        if (!fragment1.getName().getIdentifier().equals(fragment2.getName().getIdentifier())) {
            RenameVariableRefactoring refactoring = new RenameVariableRefactoring(fragment1, fragment2, oldEntity, newEntity);
            refactorings.add(refactoring);
        }
    }

    private void processTryStatement(StatementNodeTree oldStatement, StatementNodeTree newStatement, DeclarationNodeTree oldEntity,
                                     DeclarationNodeTree newEntity, List<Refactoring> refactorings) {
        TryStatement statement1 = (TryStatement) oldStatement.getStatement();
        TryStatement statement2 = (TryStatement) newStatement.getStatement();
        List<Object> resources1 = statement1.resources();
        List<Object> resources2 = statement2.resources();
        for (int i = 0; i < Math.min(resources1.size(), resources2.size()); i++) {
            Object resource1 = resources1.get(i);
            Object resource2 = resources2.get(i);
            if (resource1 instanceof VariableDeclarationExpression && resource2 instanceof VariableDeclarationExpression) {
                VariableDeclarationExpression expression1 = (VariableDeclarationExpression) resource1;
                VariableDeclarationExpression expression2 = (VariableDeclarationExpression) resource2;
                List<VariableDeclarationFragment> fragments1 = expression1.fragments();
                List<VariableDeclarationFragment> fragments2 = expression2.fragments();
                for (int j = 0; j < Math.min(fragments1.size(), fragments2.size()); j++) {
                    VariableDeclarationFragment fragment1 = fragments1.get(j);
                    VariableDeclarationFragment fragment2 = fragments2.get(j);
                    if (!fragment1.getName().getIdentifier().equals(fragment2.getName().getIdentifier())) {
                        RenameVariableRefactoring refactoring = new RenameVariableRefactoring(fragment1, fragment2, oldEntity, newEntity);
                        refactorings.add(refactoring);
                    }
                }
            }
        }
    }

    private void processLoopStatement(StatementNodeTree oldStatement, StatementNodeTree newStatement, DeclarationNodeTree oldEntity,
                                      DeclarationNodeTree newEntity, Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                      List<Refactoring> refactorings) {
        if (oldStatement.getType() != newStatement.getType()) {
            ChangeLoopTypeRefactoring refactoring = new ChangeLoopTypeRefactoring(oldStatement, newStatement, oldEntity, newEntity);
            refactorings.add(refactoring);
        }
        StatementNodeTree parent = newStatement.getParent();
        while (parent.getDepth() > 0 &&
                !(parent.getType() == StatementType.FOR_STATEMENT || parent.getType() == StatementType.ENHANCED_FOR_STATEMENT ||
                        parent.getType() == StatementType.WHILE_STATEMENT || parent.getType() == StatementType.DO_STATEMENT)) {
            parent = parent.getParent();
        }
        StatementNodeTree child = retrieveSNTByBFS(oldStatement);
        if (child != null && matchedStatements.contains(Pair.of(child, parent))) {
            LoopInterchangeRefactoring refactoring1 = new LoopInterchangeRefactoring(oldStatement, newStatement, oldEntity, newEntity);
            refactorings.add(refactoring1);
        }
    }

    private void processAnonymousWithLambda(StatementNodeTree oldStatement, StatementNodeTree newStatement, DeclarationNodeTree oldEntity,
                                            DeclarationNodeTree newEntity, List<Refactoring> refactorings) {
        ASTNode statement1 = oldStatement.getStatement();
        ASTNode statement2 = newStatement.getStatement();
        List<AnonymousClassDeclaration> anonymous1 = new ArrayList<>();
        List<LambdaExpression> lambda1 = new ArrayList<>();
        List<MethodReference> references1 = new ArrayList<>();
        List<AnonymousClassDeclaration> anonymous2 = new ArrayList<>();
        List<LambdaExpression> lambda2 = new ArrayList<>();
        List<MethodReference> references2 = new ArrayList<>();
        statement1.accept(new ASTVisitor() {
            @Override
            public boolean visit(AnonymousClassDeclaration node) {
                anonymous1.add(node);
                return true;
            }

            @Override
            public boolean visit(LambdaExpression node) {
                lambda1.add(node);
                return true;
            }

            @Override
            public boolean visit(ExpressionMethodReference node) {
                references1.add(node);
                return true;
            }

            @Override
            public boolean visit(SuperMethodReference node) {
                references1.add(node);
                return true;
            }

            @Override
            public boolean visit(TypeMethodReference node) {
                references1.add(node);
                return true;
            }
        });
        statement2.accept(new ASTVisitor() {
            @Override
            public boolean visit(AnonymousClassDeclaration node) {
                anonymous2.add(node);
                return true;
            }

            @Override
            public boolean visit(LambdaExpression node) {
                lambda2.add(node);
                return true;
            }

            @Override
            public boolean visit(ExpressionMethodReference node) {
                references2.add(node);
                return true;
            }

            @Override
            public boolean visit(SuperMethodReference node) {
                references2.add(node);
                return true;
            }

            @Override
            public boolean visit(TypeMethodReference node) {
                references2.add(node);
                return true;
            }
        });
        if (anonymous1.size() == 1 && lambda1.isEmpty() && references1.isEmpty() &&
                (lambda2.size() + references2.size() == 1) && anonymous2.isEmpty()) {
            ReplaceAnonymousWithLambdaRefactoring refactoring = new ReplaceAnonymousWithLambdaRefactoring(anonymous1.get(0), lambda2.get(0), oldEntity, newEntity);
            refactorings.add(refactoring);
        }
    }

    private void processInvertCondition(StatementNodeTree oldStatement, StatementNodeTree newStatement, DeclarationNodeTree oldEntity,
                                        DeclarationNodeTree newEntity, Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                        List<Refactoring> refactorings) {
        if (!oldStatement.getExpression().equals(newStatement.getExpression()))
            return;
        String expression1 = oldStatement.getExpression();
        String expression2 = newStatement.getExpression();
        boolean invertedFlag = false;
        if (!expression1.equals(expression2) && (expression1.replace("==", "!=").equals(expression2) || expression1.replace("!=", "==").equals(expression2))) {
            InvertConditionRefactoring refactoring = new InvertConditionRefactoring(oldStatement, newStatement, oldEntity, newEntity);
            refactorings.add(refactoring);
            invertedFlag = true;
        }
        if (!expression1.equals(expression2) && (invertedFlag && expression1.replace("if(", "if(!").equals(expression2) || expression1.replace("if(!", "if(").equals(expression2))) {
            InvertConditionRefactoring refactoring = new InvertConditionRefactoring(oldStatement, newStatement, oldEntity, newEntity);
            refactorings.add(refactoring);
            invertedFlag = true;
        }
        if (!expression1.equals(expression2) && (invertedFlag && expression1.replace("if(", "if(!").equals(expression2.replace("if(!(", "if（!")) ||
                expression1.replace("if(!", "if(").equals(expression2.replace("))", ")")))) {
            InvertConditionRefactoring refactoring = new InvertConditionRefactoring(oldStatement, newStatement, oldEntity, newEntity);
            refactorings.add(refactoring);
            invertedFlag = true;
        }
        List<StatementNodeTree> children1 = oldStatement.getChildren();
        List<StatementNodeTree> children2 = newStatement.getChildren();
        if (!invertedFlag) {
            for (StatementNodeTree child1 : children1) {
                if (invertedFlag)
                    break;
                for (StatementNodeTree child2 : children2) {
                    if (matchedStatements.contains(Pair.of(child1, child2)) && !Objects.equals(child1.getBlockExpression(), child2.getBlockExpression()) &&
                            ((child1.getBlockType() == BlockType.IF_BLOCK && child2.getBlockType() == BlockType.ELSE_BLOCK) ||
                                    (child1.getBlockType() == BlockType.ELSE_BLOCK && child2.getBlockType() == BlockType.IF_BLOCK))) {
                        InvertConditionRefactoring refactoring = new InvertConditionRefactoring(oldStatement, newStatement, oldEntity, newEntity);
                        refactorings.add(refactoring);
                        invertedFlag = true;
                        break;
                    }
                }
            }
        }
    }

    private StatementNodeTree retrieveSNTByBFS(StatementNodeTree current) {
        Queue<StatementNodeTree> queue = new LinkedList<>(current.getChildren());
        Set<StatementNodeTree> visited = new HashSet<>();
        while (!queue.isEmpty()) {
            StatementNodeTree node = queue.poll();
            if (node.getType() == StatementType.FOR_STATEMENT || node.getType() == StatementType.ENHANCED_FOR_STATEMENT ||
                    node.getType() == StatementType.WHILE_STATEMENT || node.getType() == StatementType.DO_STATEMENT)
                return node;
            for (StatementNodeTree child : node.getChildren()) {
                if (!visited.contains(child)) {
                    queue.add(child);
                    visited.add(child);
                }
            }
        }
        return null;
    }

    private void detectRefactoringsBetweenMatchedAndAddedStatements(Set<Pair<MethodNode, MethodNode>> methodNodePairs,
                                                                    Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                                                    Set<StatementNodeTree> addedStatements, List<Refactoring> refactorings) {
        for (StatementNodeTree addedStatement : addedStatements) {
            if (addedStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT) {
                checkForExtractVariable(methodNodePairs, matchedStatements, addedStatement, refactorings);
            }
        }

        checkForSplitConditional(methodNodePairs, matchedStatements, addedStatements, refactorings);
    }

    private void checkForExtractVariable(Set<Pair<MethodNode, MethodNode>> methodNodePairs,
                                         Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                         StatementNodeTree addedStatement, List<Refactoring> refactorings) {
        VariableDeclarationStatement variableDeclaration = (VariableDeclarationStatement) addedStatement.getStatement();
        List<VariableDeclarationFragment> fragments = variableDeclaration.fragments();
        DeclarationNodeTree newEntity = addedStatement.getRoot().getMethodEntity();
        for (VariableDeclarationFragment fragment : fragments) {
            DeclarationNodeTree oldEntity = null;
            Map<StatementNodeTree, StatementNodeTree> references = new TreeMap<>(Comparator.comparingInt(StatementNodeTree::getPosition));
            for (Pair<StatementNodeTree, StatementNodeTree> pair : matchedStatements) {
                StatementNodeTree oldStatement = pair.getLeft();
                StatementNodeTree newStatement = pair.getRight();
                if (!methodNodePairs.contains(Pair.of(oldStatement.getRoot(), addedStatement.getRoot())))
                    continue;
                if (newStatement.getPosition() <= addedStatement.getPosition())
                    continue;
                String expression1 = oldStatement.getExpression();
                String expression2 = newStatement.getExpression();
                if (oldEntity == null)
                    oldEntity = oldStatement.getRoot().getMethodEntity();
                if (expression2.contains(fragment.getName().getIdentifier()) && fragment.getInitializer() != null &&
                        (expression1.contains(fragment.getInitializer().toString()) ||
                                DiceFunction.calculateBodyDice(fragment, oldStatement, newStatement) >= DiceFunction.minSimilarity)) {
                    references.put(oldStatement, newStatement);
                }
            }
            if (!references.isEmpty()) {
                ExtractVariableRefactoring refactoring = new ExtractVariableRefactoring(fragment, oldEntity, newEntity, references);
                refactorings.add(refactoring);
            }
        }
    }

    private void checkForSplitConditional(Set<Pair<MethodNode, MethodNode>> methodNodePairs,
                                          Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                          Set<StatementNodeTree> addedStatements, List<Refactoring> refactorings) {
        for (Pair<StatementNodeTree, StatementNodeTree> pair : matchedStatements) {
            StatementNodeTree oldStatement = pair.getLeft();
            StatementNodeTree newStatement = pair.getRight();
            if (oldStatement.getType() == StatementType.IF_STATEMENT && newStatement.getType() == StatementType.IF_STATEMENT) {
                String expression1 = oldStatement.getExpression();
                String expression2 = newStatement.getExpression();
                Set<StatementNodeTree> splitConditionals = new LinkedHashSet<>();
                DeclarationNodeTree oldEntity = null;
                DeclarationNodeTree newEntity = null;
                for (StatementNodeTree addedStatement : addedStatements) {
                    if (!methodNodePairs.contains(Pair.of(oldStatement.getRoot(), addedStatement.getRoot())))
                        continue;
                    String addedExpression = addedStatement.getExpression();
                    if (!expression1.equals(addedExpression) && (expression1.contains(addedExpression.replace("if(", "")) ||
                            expression1.contains(addedExpression.replace("if(", "").replace(")", ""))) &&
                            !(expression2.contains(addedExpression.replace("if(", ""))
                                    || expression2.contains(addedExpression.replace("if(", "").replace(")", ""))
                                    || expression2.contains(addedExpression.replace(")", "")))) {
                        splitConditionals.add(newStatement);
                        splitConditionals.add(addedStatement);
                        if (oldEntity == null)
                            oldEntity = oldStatement.getRoot().getMethodEntity();
                        if (newEntity == null)
                            newEntity = addedStatement.getRoot().getMethodEntity();
                    }
                }
                if (!splitConditionals.isEmpty()) {
                    SplitConditionalRefactoring refactoring = new SplitConditionalRefactoring(oldStatement, splitConditionals, oldEntity, newEntity);
                    refactorings.add(refactoring);
                }
            }
        }
    }

    private void detectRefactoringsBetweenMatchedAndDeletedStatements(Set<Pair<MethodNode, MethodNode>> methodNodePairs,
                                                                      Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                                                      Set<StatementNodeTree> deletedStatements, List<Refactoring> refactorings) {
        for (StatementNodeTree deletedStatement : deletedStatements) {
            if (deletedStatement.getType() == StatementType.EXPRESSION_STATEMENT) {
                checkForMergeDeclarationAndAssignment(methodNodePairs, deletedStatement, matchedStatements, refactorings);
            }
            if (deletedStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT) {
                checkForInlineVariable(methodNodePairs, deletedStatement, matchedStatements, refactorings);
            }
            if (deletedStatement.getType() == StatementType.IF_STATEMENT) {
                checkForReplaceIfWithTernary(methodNodePairs, deletedStatement, matchedStatements, refactorings);
            }
        }

        checkForMergeConditional(methodNodePairs, matchedStatements, deletedStatements, refactorings);
    }

    private void checkForMergeDeclarationAndAssignment(Set<Pair<MethodNode, MethodNode>> methodNodePairs,
                                                       StatementNodeTree deletedStatement, Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                                       List<Refactoring> refactorings) {
        List<Assignment> assignments = new ArrayList<>();
        deletedStatement.getStatement().accept(new ASTVisitor() {
            @Override
            public boolean visit(Assignment node) {
                assignments.add(node);
                return true;
            }
        });
        if (assignments.isEmpty())
            return;
        for (Pair<StatementNodeTree, StatementNodeTree> pair : matchedStatements) {
            StatementNodeTree oldStatement = pair.getLeft();
            StatementNodeTree newStatement = pair.getRight();
            if (!methodNodePairs.contains(Pair.of(deletedStatement.getRoot(), newStatement.getRoot())))
                continue;
            if (oldStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT && newStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT) {
                VariableDeclarationStatement statement1 = (VariableDeclarationStatement) oldStatement.getStatement();
                VariableDeclarationStatement statement2 = (VariableDeclarationStatement) newStatement.getStatement();
                if (statement1.toString().equals(statement2.toString()))
                    continue;
                List<VariableDeclarationFragment> fragments1 = statement1.fragments();
                List<VariableDeclarationFragment> fragments2 = statement2.fragments();
                for (Assignment assignment : assignments) {
                    for (int i = 0; i < Math.min(fragments1.size(), fragments2.size()); i++) {
                        VariableDeclarationFragment fragment1 = fragments1.get(i);
                        VariableDeclarationFragment fragment2 = fragments2.get(i);
                        if (fragment1.getName().getIdentifier().equals(assignment.getLeftHandSide().toString()) &&
                                fragment2.getInitializer() != null &&
                                fragment2.getInitializer().toString().equals(assignment.getRightHandSide().toString())) {
                            DeclarationNodeTree oldEntity = oldStatement.getRoot().getMethodEntity();
                            DeclarationNodeTree newEntity = newStatement.getRoot().getMethodEntity();
                            MergeDeclarationAndAssignmentRefactoring refactoring = new MergeDeclarationAndAssignmentRefactoring(oldStatement, deletedStatement, newStatement, oldEntity, newEntity);
                            refactorings.add(refactoring);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void checkForInlineVariable(Set<Pair<MethodNode, MethodNode>> methodNodePairs, StatementNodeTree deletedStatement,
                                        Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements, List<Refactoring> refactorings) {
        VariableDeclarationStatement variableDeclaration = (VariableDeclarationStatement) deletedStatement.getStatement();
        List<VariableDeclarationFragment> fragments = variableDeclaration.fragments();
        DeclarationNodeTree oldEntity = deletedStatement.getRoot().getMethodEntity();
        for (VariableDeclarationFragment fragment : fragments) {
            DeclarationNodeTree newEntity = null;
            Map<StatementNodeTree, StatementNodeTree> references = new TreeMap<>(Comparator.comparingInt(StatementNodeTree::getPosition));
            for (Pair<StatementNodeTree, StatementNodeTree> pair : matchedStatements) {
                StatementNodeTree oldStatement = pair.getLeft();
                StatementNodeTree newStatement = pair.getRight();
                if (!methodNodePairs.contains(Pair.of(deletedStatement.getRoot(), newStatement.getRoot())))
                    continue;
                List<StatementNodeTree> descendants = deletedStatement.getParent().getDescendants();
                if (!descendants.contains(oldStatement))
                    continue;
                if (oldStatement.getPosition() <= deletedStatement.getPosition())
                    continue;
                String expression1 = oldStatement.getExpression();
                String expression2 = newStatement.getExpression();
                if (newEntity == null)
                    newEntity = newStatement.getRoot().getMethodEntity();
                if (expression1.contains(fragment.getName().getIdentifier()) && fragment.getInitializer() != null &&
                        (expression2.contains(fragment.getInitializer().toString()) ||
                                DiceFunction.calculateBodyDice(fragment, newStatement, oldStatement) >= DiceFunction.minSimilarity)) {
                    references.put(oldStatement, newStatement);
                }
            }
            if (!references.isEmpty()) {
                InlineVariableRefactoring refactoring = new InlineVariableRefactoring(fragment, oldEntity, newEntity, references);
                refactorings.add(refactoring);
            }
        }
    }

    private void checkForReplaceIfWithTernary(Set<Pair<MethodNode, MethodNode>> methodNodePairs,
                                              StatementNodeTree deletedStatement, Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                              List<Refactoring> refactorings) {
        for (Pair<StatementNodeTree, StatementNodeTree> pair : matchedStatements) {
            StatementNodeTree oldStatement = pair.getLeft();
            StatementNodeTree newStatement = pair.getRight();
            if (!methodNodePairs.contains(Pair.of(deletedStatement.getRoot(), newStatement.getRoot())))
                continue;
            DeclarationNodeTree oldEntity = oldStatement.getRoot().getMethodEntity();
            DeclarationNodeTree newEntity = newStatement.getRoot().getMethodEntity();
            if (oldStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT &&
                    newStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT) {
                String expression2 = newStatement.getExpression();
                String expression1 = deletedStatement.getExpression();
                expression1 = expression1.substring(expression1.indexOf("if") + 2);
                if ((expression2.contains(expression1 + " ? ") || (expression2.contains(expression1.replace("(", "").replace(")", "") + " ? ")) ||
                        expression2.contains(expression1.replace("==", "!=") + " ? ") ||
                        expression2.contains(expression1.replace("!=", "==") + " ? ") ||
                        expression2.contains(expression1.replace("(", "(!") + " ? ") ||
                        expression2.contains(expression1.replace("(!", "(") + " ? ")) &&
                        expression2.contains(" : ") && expression2.lastIndexOf(expression1 + " ? ") < expression2.lastIndexOf(" : ")) {
                    ReplaceIfElseWithTernaryRefactoring refactoring = new ReplaceIfElseWithTernaryRefactoring(oldStatement, deletedStatement, newStatement, oldEntity, newEntity);
                    refactorings.add(refactoring);
                }
            }
        }
    }

    private void checkForMergeConditional(Set<Pair<MethodNode, MethodNode>> methodNodePairs,
                                          Set<Pair<StatementNodeTree, StatementNodeTree>> matchedStatements,
                                          Set<StatementNodeTree> deletedStatements, List<Refactoring> refactorings) {
        for (Pair<StatementNodeTree, StatementNodeTree> pair : matchedStatements) {
            StatementNodeTree oldStatement = pair.getLeft();
            StatementNodeTree newStatement = pair.getRight();
            if (oldStatement.getType() == StatementType.IF_STATEMENT && newStatement.getType() == StatementType.IF_STATEMENT) {
                String expression1 = oldStatement.getExpression();
                String expression2 = newStatement.getExpression();
                Set<StatementNodeTree> mergedConditionals = new LinkedHashSet<>();
                DeclarationNodeTree oldEntity = null;
                DeclarationNodeTree newEntity = null;
                for (StatementNodeTree deletedStatement : deletedStatements) {
                    if (!methodNodePairs.contains(Pair.of(deletedStatement.getRoot(), newStatement.getRoot())))
                        continue;
                    String deletedExpression = deletedStatement.getExpression();
                    if (!expression2.equals(deletedExpression) && (expression2.contains(deletedExpression.replace("if(", "")) ||
                            expression2.contains(deletedExpression.replace("if(", "").replace(")", ""))) &&
                            !(expression1.contains(deletedExpression.replace("if(", ""))
                                    || expression1.contains(deletedExpression.replace("if(", "").replace(")", ""))
                                    || expression1.contains(deletedExpression.replace(")", "")))) {
                        mergedConditionals.add(oldStatement);
                        mergedConditionals.add(deletedStatement);
                        if (oldEntity == null)
                            oldEntity = deletedStatement.getRoot().getMethodEntity();
                        if (newEntity == null)
                            newEntity = newStatement.getRoot().getMethodEntity();
                    }
                }
                if (!mergedConditionals.isEmpty()) {
                    MergeConditionalRefactoring refactoring = new MergeConditionalRefactoring(mergedConditionals, newStatement, oldEntity, newEntity);
                    refactorings.add(refactoring);
                }
            }
        }
    }

    private void detectRefactoringsBetweenAddedAndDeletedStatements(Set<Pair<MethodNode, MethodNode>> methodNodePairs,
                                                                    Set<StatementNodeTree> addedStatements, Set<StatementNodeTree> deletedStatements,
                                                                    MatchPair matchPair, List<Refactoring> refactorings) {
        for (StatementNodeTree deletedStatement : deletedStatements) {
            for (StatementNodeTree addedStatement : addedStatements) {
                if (!methodNodePairs.contains(Pair.of(deletedStatement.getRoot(), addedStatement.getRoot())))
                    continue;
                if ((deletedStatement.getType() == StatementType.FOR_STATEMENT || deletedStatement.getType() == StatementType.ENHANCED_FOR_STATEMENT ||
                        deletedStatement.getType() == StatementType.WHILE_STATEMENT || deletedStatement.getType() == StatementType.DO_STATEMENT) &&
                        (addedStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT || addedStatement.getType() == StatementType.EXPRESSION_STATEMENT ||
                                addedStatement.getType() == StatementType.RETURN_STATEMENT)) {
                    if (MethodUtils.isStreamAPI(addedStatement.getStatement()) && DiceFunction.calculateSimilarity(matchPair, deletedStatement, addedStatement) >= DiceFunction.minSimilarity) {
                        DeclarationNodeTree oldEntity = deletedStatement.getRoot().getMethodEntity();
                        DeclarationNodeTree newEntity = addedStatement.getRoot().getMethodEntity();
                        ReplaceLoopWithPipelineRefactoring refactoring = new ReplaceLoopWithPipelineRefactoring(deletedStatement, addedStatement, oldEntity, newEntity);
                        refactorings.add(refactoring);
                    }
                }

                if ((addedStatement.getType() == StatementType.FOR_STATEMENT || addedStatement.getType() == StatementType.ENHANCED_FOR_STATEMENT ||
                        addedStatement.getType() == StatementType.WHILE_STATEMENT || addedStatement.getType() == StatementType.DO_STATEMENT) &&
                        (deletedStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT || deletedStatement.getType() == StatementType.EXPRESSION_STATEMENT ||
                                deletedStatement.getType() == StatementType.RETURN_STATEMENT)) {
                    if (MethodUtils.isStreamAPI(deletedStatement.getStatement()) && DiceFunction.calculateSimilarity(matchPair, deletedStatement, addedStatement) >= DiceFunction.minSimilarity) {
                        DeclarationNodeTree oldEntity = deletedStatement.getRoot().getMethodEntity();
                        DeclarationNodeTree newEntity = addedStatement.getRoot().getMethodEntity();
                        ReplacePipelineWithLoopRefactoring refactoring = new ReplacePipelineWithLoopRefactoring(deletedStatement, addedStatement, oldEntity, newEntity);
                        refactorings.add(refactoring);
                    }
                }
            }
        }
        for (StatementNodeTree deletedStatement : deletedStatements) {
            if (deletedStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT) {
                DeclarationNodeTree oldEntity = null, newEntity = null;
                VariableDeclarationStatement statement = (VariableDeclarationStatement) deletedStatement.getStatement();
                List<VariableDeclarationFragment> fragments = statement.fragments();
                for (VariableDeclarationFragment fragment : fragments) {
                    String name = fragment.getName().getIdentifier();
                    Map<StatementNodeTree, StatementNodeTree> references = new TreeMap<>(Comparator.comparingInt(StatementNodeTree::getPosition));
                    if (fragment.getInitializer() == null) continue;
                    for (StatementNodeTree deleted : deletedStatements) {
                        if (deleted == deletedStatement) continue;
                        for (StatementNodeTree added : addedStatements) {
                            if (matchPair.getMatchedStatements().contains(Pair.of(deleted.getParent(), added.getParent())) &&
                                    deleted.getRoot() == deletedStatement.getRoot() &&
                                    matchPair.getMatchedEntities().contains(Pair.of(deleted.getRoot().getMethodEntity(), added.getRoot().getMethodEntity()))) {
                                if (deleted.getType() == StatementType.EXPRESSION_STATEMENT && added.getType() == StatementType.RETURN_STATEMENT) {
                                    ExpressionStatement expressionStatement = (ExpressionStatement) deleted.getStatement();
                                    ReturnStatement returnStatement = (ReturnStatement) added.getStatement();
                                    if (expressionStatement.getExpression() instanceof Assignment) {
                                        Assignment assignment = (Assignment) expressionStatement.getExpression();
                                        if (assignment.getRightHandSide().toString().equals(returnStatement.getExpression().toString())) {
                                            boolean vdFlag = false;
                                            String removedName = "";
                                            for (StatementNodeTree deletedStmt : deletedStatements) {
                                                if (deletedStmt.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT) {
                                                    VariableDeclarationStatement vds = (VariableDeclarationStatement) deletedStmt.getStatement();
                                                    VariableDeclarationFragment vdf = (VariableDeclarationFragment) vds.fragments().get(0);
                                                    if (vdf.getInitializer() == null) {
                                                        vdFlag = true;
                                                        removedName = vdf.getName().getIdentifier();
                                                    }
                                                }
                                                if (vdFlag && deletedStmt.getType() == StatementType.EXPRESSION_STATEMENT) {
                                                    ExpressionStatement es = (ExpressionStatement) deletedStmt.getStatement();
                                                    if (es.getExpression() instanceof Assignment) {
                                                        Assignment as = (Assignment) es.getExpression();
                                                        if (as.getLeftHandSide().toString().equals(removedName) && as.getRightHandSide().toString().equals(name)) {
                                                            references.put(deletedStmt, added);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (oldEntity == null && newEntity == null) {
                                            oldEntity = deleted.getRoot().getMethodEntity();
                                            newEntity = added.getRoot().getMethodEntity();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!references.isEmpty()) {
                        InlineVariableRefactoring refactoring = new InlineVariableRefactoring(fragment, oldEntity, newEntity, references);
                        refactorings.add(refactoring);
                    }
                }
            }
        }
        for (StatementNodeTree addedStatement : addedStatements) {
            if (addedStatement.getType() == StatementType.VARIABLE_DECLARATION_STATEMENT) {
                DeclarationNodeTree oldEntity = null, newEntity = null;
                VariableDeclarationStatement statement = (VariableDeclarationStatement) addedStatement.getStatement();
                List<VariableDeclarationFragment> fragments = statement.fragments();
                for (VariableDeclarationFragment fragment : fragments) {
                    Map<StatementNodeTree, StatementNodeTree> references = new TreeMap<>(Comparator.comparingInt(StatementNodeTree::getPosition));
                    String name = fragment.getName().getIdentifier();
                    if (fragment.getInitializer() == null) continue;
                    String initializer = fragment.getInitializer().toString();
                    for (StatementNodeTree added : addedStatements) {
                        if (added == addedStatement) continue;
                        for (StatementNodeTree deleted : deletedStatements) {
                            if (matchPair.getMatchedStatements().contains(Pair.of(deleted.getParent(), added.getParent())) &&
                                    added.getRoot() == addedStatement.getRoot() &&
                                    matchPair.getMatchedEntities().contains(Pair.of(deleted.getRoot().getMethodEntity(), added.getRoot().getMethodEntity()))) {
                                if (added.getExpression().replace(name, initializer).equals(deleted.getExpression()) ||
                                        added.getExpression().replace(name, "(" + initializer + ")").equals(deleted.getExpression())) {
                                    references.put(deleted, added);
                                    oldEntity = deleted.getRoot().getMethodEntity();
                                    newEntity = added.getRoot().getMethodEntity();
                                }
                            }
                        }
                    }
                    if (!references.isEmpty()) {
                        ExtractVariableRefactoring refactoring = new ExtractVariableRefactoring(fragment, oldEntity, newEntity, references);
                        refactorings.add(refactoring);
                    }
                }
            }
        }
    }
}
