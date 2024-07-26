package org.reextractor.util;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.internal.core.dom.NaiveASTFlattener;
import org.remapper.dto.DeclarationNodeTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class MethodUtils {

    public static boolean isGetter(MethodDeclaration methodDeclaration) {
        Block body = methodDeclaration.getBody();
        if (body != null) {
            List<Statement> statements = body.statements();
            List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
            if (statements.size() == 1) {
                Statement statement = statements.get(0);
                if (statement.toString().startsWith("return ")) {
                    ASTNode parent = methodDeclaration.getParent();
                    if (parent instanceof TypeDeclaration) {
                        TypeDeclaration typeDeclaration = (TypeDeclaration) parent;
                        FieldDeclaration[] fields = typeDeclaration.getFields();
                        for (FieldDeclaration fieldDeclaration : fields) {
                            List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
                            for (VariableDeclarationFragment fragment : fragments) {
                                if (statement.toString().equals("return " + fragment.getName().getIdentifier() + ";\n") && (parameters.size() == 0)) {
                                    return true;
                                } else if (statement.toString().equals("return " + fragment.getName().getIdentifier() + ".keySet()" + ";\n") && (parameters.size() == 0)) {
                                    return true;
                                } else if (statement.toString().equals("return " + fragment.getName().getIdentifier() + ".values()" + ";\n") && (parameters.size() == 0)) {
                                    return true;
                                }
                            }
                        }
                    }
                    String name = methodDeclaration.getName().getIdentifier();
                    Type returnType = methodDeclaration.getReturnType2();
                    if ((name.startsWith("is") || name.startsWith("has")) && (parameters.size() == 0) &&
                            returnType != null && returnType.toString().equals("boolean")) {
                        return true;
                    }
                    if (statement.toString().equals("return null;\n")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isSetter(MethodDeclaration methodDeclaration) {
        Block body = methodDeclaration.getBody();
        List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
        if (body != null && parameters.size() == 1) {
            List<Statement> statements = body.statements();
            if (statements.size() == 1) {
                Statement statement = statements.get(0);
                ASTNode parent = methodDeclaration.getParent();
                if (parent instanceof TypeDeclaration) {
                    TypeDeclaration typeDeclaration = (TypeDeclaration) parent;
                    FieldDeclaration[] fields = typeDeclaration.getFields();
                    for (FieldDeclaration fieldDeclaration : fields) {
                        List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
                        for (VariableDeclarationFragment fragment : fragments) {
                            if (statement.toString().equals(fragment.getName().getIdentifier() + "=" + parameters.get(0).getName().getIdentifier() + ";\n")) {
                                return true;
                            } else if (statement.toString().equals("this." + fragment.getName().getIdentifier() + "=" + parameters.get(0).getName().getIdentifier() + ";\n")) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static String method2String(DeclarationNodeTree method) {
        MethodDeclaration methodDeclaration = (MethodDeclaration) method.getDeclaration();
        StringBuilder sb = new StringBuilder();
        int methodModifiers = methodDeclaration.getModifiers();
        boolean isInterfaceMethod = false;
        if (methodDeclaration.getParent() instanceof TypeDeclaration) {
            TypeDeclaration parent = (TypeDeclaration) methodDeclaration.getParent();
            isInterfaceMethod = parent.isInterface();
        }
        if ((methodModifiers & Modifier.PUBLIC) != 0)
            sb.append("public").append(" ");
        else if ((methodModifiers & Modifier.PROTECTED) != 0)
            sb.append("protected").append(" ");
        else if ((methodModifiers & Modifier.PRIVATE) != 0)
            sb.append("private").append(" ");
        else if (isInterfaceMethod)
            sb.append("public").append(" ");
        else
            sb.append("package").append(" ");
        if ((methodModifiers & Modifier.ABSTRACT) != 0)
            sb.append("abstract").append(" ");
        sb.append(methodDeclaration.getName().getIdentifier());
        sb.append("(");
        List<SingleVariableDeclaration> parameters = methodDeclaration.parameters();
        List<String> list = new ArrayList<>();
        for (SingleVariableDeclaration parameter : parameters) {
            if (parameter.isVarargs()) {
                list.add(parameter.getName().getFullyQualifiedName() + " " + parameter.getType().toString() + "...");
            } else {
                list.add(parameter.getName().getFullyQualifiedName() + " " + parameter.getType().toString());
            }
        }
        sb.append(String.join(", ", list));
        sb.append(")");
        if (methodDeclaration.getReturnType2() != null) {
            sb.append(" : ");
            sb.append(methodDeclaration.getReturnType2().toString());
        }
        return sb.toString();
    }

    public static String getAnonymousCodePath(AnonymousClassDeclaration anonymous) {
        CompilationUnit cu = (CompilationUnit) anonymous.getRoot();
        PackageDeclaration packageDeclaration = cu.getPackage();
        TypeDeclaration typeDeclaration = (TypeDeclaration) cu.types().get(0);
        String name = "";
        ASTNode parent = anonymous.getParent();
        while (parent != null) {
            if (parent instanceof MethodDeclaration) {
                String methodName = (packageDeclaration == null ? "" : packageDeclaration.getName().getFullyQualifiedName() + ".") +
                        typeDeclaration.getName().getFullyQualifiedName() + "." + ((MethodDeclaration) parent).getName().getIdentifier();
                if (name.isEmpty()) {
                    name = methodName;
                } else {
                    name = methodName + "." + name;
                }
            } else if (parent instanceof VariableDeclarationFragment &&
                    (parent.getParent() instanceof FieldDeclaration ||
                            parent.getParent() instanceof VariableDeclarationStatement)) {
                String fieldName = ((VariableDeclarationFragment) parent).getName().getIdentifier();
                if (name.isEmpty()) {
                    name = fieldName;
                } else {
                    name = fieldName + "." + name;
                }
            } else if (parent instanceof MethodInvocation) {
                String invocationName = ((MethodInvocation) parent).getName().getIdentifier();
                if (name.isEmpty()) {
                    name = invocationName;
                } else {
                    name = invocationName + "." + name;
                }
            } else if (parent instanceof SuperMethodInvocation) {
                String invocationName = ((SuperMethodInvocation) parent).getName().getIdentifier();
                if (name.isEmpty()) {
                    name = invocationName;
                } else {
                    name = invocationName + "." + name;
                }
            } else if (parent instanceof ClassInstanceCreation) {
                String invocationName = stringify(((ClassInstanceCreation) parent).getType());
                if (name.isEmpty()) {
                    name = "new " + invocationName;
                } else {
                    name = "new " + invocationName + "." + name;
                }
            }
            parent = parent.getParent();
        }
        return name.toString();
    }

    private static String stringify(ASTNode node) {
        ASTFlattener printer = new ASTFlattener();
        node.accept(printer);
        return printer.getResult();
    }

    public static String getLambdaString(LambdaExpression lambda) {
        StringBuilder sb = new StringBuilder();
        boolean hasParentheses = lambda.hasParentheses();
        List<VariableDeclaration> params = lambda.parameters();
        ASTNode body = lambda.getBody();
        if (hasParentheses) {
            sb.append("(");
        }
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName().getIdentifier());
            if (i < params.size() - 1)
                sb.append(", ");
        }
        if (hasParentheses) {
            sb.append(")");
        }
        if (params.size() > 0 || hasParentheses) {
            sb.append(" -> ");
        }
        StringBuilder lambdaString = new StringBuilder();
        if (body instanceof Block) {
            lambdaString.append("{");
            List<Statement> statements = ((Block) body).statements();
            for (Statement statement : statements) {
                lambdaString.append(statement.toString());
            }
        } else {
            lambdaString.append(body.toString());
        }
        String string = lambdaString.toString();
        sb.append(string.contains("\n") ? string.substring(0, string.indexOf("\n")) : string);
        return sb.toString();
    }

    public static boolean isStreamAPI(ASTNode statement) {
        if (statement.toString().contains(" -> ") || statement.toString().contains("::")) {
            List<Expression> list = new ArrayList<>();
            statement.accept(new ASTVisitor() {
                @Override
                public boolean visit(MethodInvocation node) {
                    if (streamAPIName(node.getName().getFullyQualifiedName()))
                        list.add(node);
                    return true;
                }

                @Override
                public boolean visit(SuperMethodInvocation node) {
                    if (streamAPIName(node.getName().getFullyQualifiedName()))
                        list.add(node);
                    return true;
                }

                @Override
                public boolean visit(ExpressionMethodReference node) {
                    if (streamAPIName(node.getName().getFullyQualifiedName()))
                        list.add(node);
                    return true;
                }

                @Override
                public boolean visit(SuperMethodReference node) {
                    if (streamAPIName(node.getName().getFullyQualifiedName()))
                        list.add(node);
                    return true;
                }

                @Override
                public boolean visit(TypeMethodReference node) {
                    if (streamAPIName(node.getName().getFullyQualifiedName()))
                        list.add(node);
                    return true;
                }
            });
            if (list.size() > 0)
                return true;
        }
        return false;
    }

    private static boolean streamAPIName(String name) {
        return name.equals("stream") || name.equals("filter") || name.equals("forEach") || name.equals("collect") || name.equals("map") || name.equals("removeIf");
    }

    public static boolean isNewFunction(ASTNode oldFragment, ASTNode newFragment) {
        if (oldFragment instanceof MethodDeclaration && newFragment instanceof MethodDeclaration) {
            if (((MethodDeclaration) oldFragment).getBody() == null || ((MethodDeclaration) newFragment).getBody() == null)
                return false;
            String[] oldMethodLines = ((MethodDeclaration) oldFragment).getBody().toString().split("\n");
            String[] newMethodLines = ((MethodDeclaration) newFragment).getBody().toString().split("\n");
            int index = 0;
            for (String newMethodLine : newMethodLines) {
                if (Objects.equals(oldMethodLines[index], newMethodLine))
                    index++;
                if (index == oldMethodLines.length)
                    return true;
            }
            return index == oldMethodLines.length;
        }
        return false;
    }
}

class ASTFlattener extends NaiveASTFlattener {
    @Override
    public boolean visit(InfixExpression node) {
        node.getLeftOperand().accept(this);
        this.buffer.append(' ');  // for cases like x= i - -1; or x= i++ + ++i;
        this.buffer.append(node.getOperator().toString());
        this.buffer.append(' ');
        node.getRightOperand().accept(this);
        final List extendedOperands = node.extendedOperands();
        if (extendedOperands.size() != 0) {
            for (Iterator it = extendedOperands.iterator(); it.hasNext(); ) {
                this.buffer.append(' ');
                this.buffer.append(node.getOperator().toString()).append(' ');
                Expression e = (Expression) it.next();
                e.accept(this);
            }
        }
        return false;
    }
}