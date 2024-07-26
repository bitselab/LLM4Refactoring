package org.reextractor.util;

import org.eclipse.jdt.core.dom.*;

public class VariableUtils {

    public static String variable2String(VariableDeclaration variableDeclaration) {
        StringBuilder sb = new StringBuilder();
        sb.append(variableDeclaration.getName().getIdentifier());
        sb.append(" : ");
        if (variableDeclaration instanceof SingleVariableDeclaration) {
            sb.append(((SingleVariableDeclaration) variableDeclaration).getType().toString());
            if (((SingleVariableDeclaration) variableDeclaration).isVarargs())
                sb.append("...");
        } else {
            ASTNode parent = variableDeclaration.getParent();
            if (parent instanceof VariableDeclarationStatement) {
                sb.append(((VariableDeclarationStatement) parent).getType().toString());
            } else if (parent instanceof VariableDeclarationExpression) {
                sb.append(((VariableDeclarationExpression) parent).getType().toString());
            } else if (parent instanceof LambdaExpression) {
                sb.append("null");
            } else {
                sb.append("null");
            }
        }
        return sb.toString();
    }
}
