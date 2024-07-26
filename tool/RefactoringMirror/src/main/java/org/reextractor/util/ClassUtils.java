package org.reextractor.util;

import org.remapper.dto.DeclarationNodeTree;
import org.remapper.util.StringUtils;

public class ClassUtils {

    public static String typeDeclaration2String(DeclarationNodeTree typeDeclaration) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotEmpty(typeDeclaration.getNamespace())) {
            sb.append(typeDeclaration.getNamespace()).append(".").append(typeDeclaration.getName());
        } else {
            sb.append(typeDeclaration.getName());
        }
        return sb.toString();
    }
}
