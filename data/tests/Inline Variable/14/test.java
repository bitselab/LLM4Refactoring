package org.apache.lucene.index;

import org.apache.lucene.index.TestFieldInfos;
import org.junit.Test;

public class TestFieldInfosTest {
    
    TestFieldInfos testFieldInfos = new TestFieldInfos();

    @Test
    public void testTestFieldInfos() throws Exception {
        testFieldInfos.testFieldInfos();
    }

    @Test
    public void testTestFieldAttributes() throws Exception {
        testFieldInfos.testFieldAttributes();
    }

    @Test
    public void testTestFieldAttributesSingleSegment() throws Exception {
        testFieldInfos.testFieldAttributesSingleSegment();
    }

    @Test
    public void testTestMergedFieldInfos_empty() throws Exception {
        testFieldInfos.testMergedFieldInfos_empty();
    }

    @Test
    public void testTestMergedFieldInfos_singleLeaf() throws Exception {
        testFieldInfos.testMergedFieldInfos_singleLeaf();
    }

    @Test
    public void testTestFieldNumbersAutoIncrement() throws Exception {
        testFieldInfos.testFieldNumbersAutoIncrement();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme