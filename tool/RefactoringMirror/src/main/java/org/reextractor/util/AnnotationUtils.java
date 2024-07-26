package org.reextractor.util;

import org.eclipse.jdt.core.dom.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnnotationUtils {

    public static String annotation2String(Annotation annotation) {
        String typeName = annotation.getTypeName().getFullyQualifiedName();
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(typeName);
        if(annotation instanceof SingleMemberAnnotation) {
            SingleMemberAnnotation singleMemberAnnotation = (SingleMemberAnnotation)annotation;
            String value = stringify(singleMemberAnnotation.getValue());
            sb.append("(");
            sb.append(value);
            sb.append(")");
        }
        else if(annotation instanceof NormalAnnotation) {
            NormalAnnotation normalAnnotation = (NormalAnnotation)annotation;
            List<MemberValuePair> pairs = normalAnnotation.values();
            Map<String, String> memberValuePairs = new LinkedHashMap<>();
            for(MemberValuePair pair : pairs) {
                memberValuePairs.put(pair.getName().getIdentifier(), stringify(pair.getValue()));
            }
            sb.append("(");
            int i = 0;
            for (String key : memberValuePairs.keySet()) {
                sb.append(key).append(" = ").append(memberValuePairs.get(key));
                if (i < memberValuePairs.size() - 1)
                    sb.append(", ");
                i++;
            }
            sb.append(")");
        }
        return sb.toString();
    }

    public static String stringify(ASTNode node) {
        ASTFlattener printer = new ASTFlattener();
        node.accept(printer);
        return printer.getResult();
    }
}
