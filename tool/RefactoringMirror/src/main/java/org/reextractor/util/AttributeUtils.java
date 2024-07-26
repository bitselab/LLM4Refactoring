package org.reextractor.util;

import org.eclipse.jdt.core.dom.*;
import org.remapper.dto.DeclarationNodeTree;
import org.remapper.dto.EntityType;

public class AttributeUtils {

    public static String attribute2QualifiedString(DeclarationNodeTree attribute) {
        StringBuilder sb = new StringBuilder();
        if (attribute.getType() == EntityType.FIELD) {
            FieldDeclaration fieldDeclaration = (FieldDeclaration) attribute.getDeclaration();
            VariableDeclarationFragment fragment = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
            sb.append(fragment.getName().getIdentifier());
            sb.append(" : ");
            sb.append(fieldDeclaration.getType().toString());
        } else if (attribute.getType() == EntityType.ENUM_CONSTANT) {
            EnumConstantDeclaration enumConstantDeclaration = (EnumConstantDeclaration) attribute.getDeclaration();
            sb.append(enumConstantDeclaration.getName().getIdentifier());
            sb.append(" : ");
            sb.append(attribute.getParent().getName());
        }
        return sb.toString();
    }

    public static String attribute2String(DeclarationNodeTree attribute) {
        StringBuilder sb = new StringBuilder();
        if (attribute.getType() == EntityType.FIELD) {
            FieldDeclaration fieldDeclaration = (FieldDeclaration) attribute.getDeclaration();
            VariableDeclarationFragment fragment = (VariableDeclarationFragment) fieldDeclaration.fragments().get(0);
            boolean isInterfaceMethod = false;
            if (fieldDeclaration.getParent() instanceof TypeDeclaration) {
                TypeDeclaration parent = (TypeDeclaration) fieldDeclaration.getParent();
                isInterfaceMethod = parent.isInterface();
            }
            int attributeModifiers = fieldDeclaration.getModifiers();
            if ((attributeModifiers & Modifier.PUBLIC) != 0)
                sb.append("public").append(" ");
            else if ((attributeModifiers & Modifier.PROTECTED) != 0)
                sb.append("protected").append(" ");
            else if ((attributeModifiers & Modifier.PRIVATE) != 0)
                sb.append("private").append(" ");
            else if (isInterfaceMethod)
                sb.append("public").append(" ");
            else
                sb.append("package").append(" ");
            if ((attributeModifiers & Modifier.ABSTRACT) != 0)
                sb.append("abstract").append(" ");
            sb.append(fragment.getName().getIdentifier());
            sb.append(" : ");
            sb.append(fieldDeclaration.getType().toString());
        } else if (attribute.getType() == EntityType.ENUM_CONSTANT) {
            EnumConstantDeclaration enumConstantDeclaration = (EnumConstantDeclaration) attribute.getDeclaration();
            sb.append(enumConstantDeclaration.getName().getIdentifier());
            sb.append(" : ");
            sb.append(attribute.getParent().getName());
        }
        return sb.toString();
    }
}
