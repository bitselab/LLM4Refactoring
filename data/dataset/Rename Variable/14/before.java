package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.automaton.CompiledAutomaton;

/** Query that matches String prefixes */
public class SrndPrefixQuery extends SimpleTerm {
    private final CompiledAutomaton compiled;

    public SrndPrefixQuery(String prefix, boolean quoted, char truncator) {
        super(quoted);
        this.prefix = prefix;
        compiled =
                new CompiledAutomaton(PrefixQuery.toAutomaton(new BytesRef(prefix)), true, true, true);
        this.truncator = truncator;
    }

    private final String prefix;

    public String getPrefix() {
        return prefix;
    }

    private final char truncator;

    public char getSuffixOperator() {
        return truncator;
    }

    @Override
    public String toStringUnquoted() {
        return getPrefix();
    }

    @Override
    protected void suffixToString(StringBuilder r) {
        r.append(getSuffixOperator());
    }

    @Override
    public void visitMatchingTerms(IndexReader reader, String fieldName, MatchingTermVisitor mtv)
            throws IOException {
        Terms terms = MultiTerms.getTerms(reader, fieldName);
        if (terms != null) {
            TermsEnum termsEnum = compiled.getTermsEnum(terms);

            BytesRef text;
            while ((text = termsEnum.next()) != null) {
                mtv.visitMatchingTerm(new Term(fieldName, BytesRef.deepCopyOf(text)));
            }
        }
    }
}