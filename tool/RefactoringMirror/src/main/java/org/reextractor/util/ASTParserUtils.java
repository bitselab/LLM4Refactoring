package org.reextractor.util;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;

import java.util.Map;

public class ASTParserUtils {

    public static ASTParser getASTParser(String[] sourcepathEntries, String[] encodings) {
        return setAttributes(sourcepathEntries, encodings);
    }

    public static ASTParser getASTParser() {
        return setAttributes(null, null);
    }

    public static ASTParser getFastParser() {
        ASTParser parser = ASTParser.newParser(AST.JLS20);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        Map<String, String> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_20, options);
        parser.setCompilerOptions(options);
        parser.setResolveBindings(false);
        parser.setStatementsRecovery(true);
        return parser;
    }

    public static ASTParser getBodyParser() {
        ASTParser parser = ASTParser.newParser(AST.JLS19);
        parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
        Map<String, String> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_11, options);
        parser.setCompilerOptions(options);
        parser.setResolveBindings(false);
        parser.setStatementsRecovery(true);
        return parser;
    }

    private static ASTParser setAttributes(String[] sourcepathEntries, String[] encodings) {
        ASTParser parser = ASTParser.newParser(AST.JLS19);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        Map<String, String> options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        parser.setCompilerOptions(options);
        parser.setCompilerOptions(options);
        parser.setResolveBindings(true);
        parser.setEnvironment(null, sourcepathEntries, encodings, true);
        parser.setBindingsRecovery(true);
        parser.setStatementsRecovery(true);
        parser.setUnitName("unitName");
        return parser;
    }
}
