package net.sourceforge.pmd.cpd;

import java.io.IOException;

import net.sourceforge.pmd.lang.document.TextDocument;

/**
 * Tokenizes a source file into tokens consumable by CPD.
 */
public interface Tokenizer {

    /**
     * Tokenize the source code and record tokens using the provided token factory.
     */
    void tokenize(TextDocument document, TokenFactory tokens) throws IOException;

    /**
     * Wraps a call to {@link #tokenize(TextDocument, TokenFactory)} to properly
     * create and close the token factory.
     */
    static void tokenize(Tokenizer tokenizer, TextDocument textDocument, Tokens tokens) throws IOException {
        try (TokenFactory tf = Tokens.factoryForFile(textDocument, tokens)) {
            tokenizer.tokenize(textDocument, tf);
        }
    }
}


import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * These are language properties common to multiple {@link CpdCapableLanguage}s.
 *
 * @see net.sourceforge.pmd.lang.LanguagePropertyBundle
 */
public final class CpdLanguageProperties {
    private CpdLanguageProperties() {
        // utility class
    }

    public static final PropertyDescriptor<Boolean> CPD_IGNORE_LITERAL_SEQUENCES =
            PropertyFactory.booleanProperty("cpdIgnoreLiteralSequences")
                    .defaultValue(false)
                    .desc("Ignore sequences of literals, eg `0, 0, 0, 0`")
                    .build();
    public static final PropertyDescriptor<Boolean> CPD_IGNORE_LITERAL_AND_IDENTIFIER_SEQUENCES =
            PropertyFactory.booleanProperty("cpdIgnoreLiteralAndIdentifierSequences")
                    .defaultValue(false)
                    .desc("Ignore sequences of literals, eg `a, b, 0, 0`")
                    .build();
    public static final PropertyDescriptor<Boolean> CPD_ANONYMIZE_LITERALS =
            PropertyFactory.booleanProperty("cpdAnonymizeLiterals")
                    .defaultValue(false)
                    .desc("Anonymize literals. They are still part of the token stream but all literals appear to have the same value.")
                    .build();
    public static final PropertyDescriptor<Boolean> CPD_ANONYMIZE_IDENTIFIERS =
            PropertyFactory.booleanProperty("cpdAnonymizeIdentifiers")
                    .defaultValue(false)
                    .desc("Anonymize identifiers. They are still part of the token stream but all identifiers appear to have the same value.")
                    .build();
    public static final PropertyDescriptor<Boolean> CPD_IGNORE_IMPORTS =
            PropertyFactory.booleanProperty("cpdIgnoreImports")
                    .defaultValue(true)
                    .desc("Ignore import statements and equivalent (eg using statements in C#).")
                    .build();
    public static final PropertyDescriptor<Boolean> CPD_IGNORE_METADATA =
            PropertyFactory.booleanProperty("cpdIgnoreMetadata")
                    .defaultValue(false)
                    .desc("Ignore metadata such as Java annotations or C# attributes.")
                    .build();

    @Deprecated
    // TODO what to do with this?
    public static final String DEFAULT_SKIP_BLOCKS_PATTERN = "#if 0|#endif";
}