package org.apache.lucene.index;

import org.apache.lucene.index.TestTermsHashPerField;
import org.junit.Test;

public class TestTermsHashPerFieldTest {
    
    TestTermsHashPerField testTermsHashPerField = new TestTermsHashPerField();

    @Test
    public void testTestAddAndUpdateTerm() throws Exception {
        testTermsHashPerField.testAddAndUpdateTerm();
    }

    @Test
    public void testTestAddAndUpdateRandom() throws Exception {
        testTermsHashPerField.testAddAndUpdateRandom();
    }

    @Test
    public void testTestWriteBytes() throws Exception {
        testTermsHashPerField.testWriteBytes();
    }

    @Test
    public void testTestAllocKnownSizeSlice() throws Exception {
        testTermsHashPerField.testAllocKnownSizeSlice();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme