package org.apache.solr.util;

import org.apache.solr.util.TestUtils;
import org.junit.Test;

public class TestUtilsTest {
    
    TestUtils testUtils = new TestUtils();

    @Test
    public void testTestJoin() throws Exception {
        testUtils.testJoin();
    }

    @Test
    public void testTestEscapeTextWithSeparator() throws Exception {
        testUtils.testEscapeTextWithSeparator();
    }

    @Test
    public void testTestSplitEscaping() throws Exception {
        testUtils.testSplitEscaping();
    }

    @Test
    public void testTestToLower() throws Exception {
        testUtils.testToLower();
    }

    @Test
    public void testTestNamedLists() throws Exception {
        testUtils.testNamedLists();
    }

    @Test
    public void testTestNumberUtils() throws Exception {
        testUtils.testNumberUtils();
    }

    @Test
    public void testTestNoggitFlags() throws Exception {
        testUtils.testNoggitFlags();
    }

    @Test
    public void testTestBinaryCommands() throws Exception {
        testUtils.testBinaryCommands();
    }

    @Test
    public void testTestSetObjectByPath() throws Exception {
        testUtils.testSetObjectByPath();
    }

    @Test
    public void testTestUtilsJSPath() throws Exception {
        testUtils.testUtilsJSPath();
    }

    @Test
    public void testTestMapWriterIdx() throws Exception {
        testUtils.testMapWriterIdx();
    }

    @Test
    public void testTestMergeJson() throws Exception {
        testUtils.testMergeJson();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme