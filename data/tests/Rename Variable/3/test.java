package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.builder.DiffResultTest;
import org.junit.Test;

public class DiffResultTestTest {
    
    DiffResultTest diffResultTest = new DiffResultTest();

    @Test
    public void testTestDefaultStyle() throws Exception {
        diffResultTest.testDefaultStyle();
    }

    @Test
    public void testTestIterator() throws Exception {
        diffResultTest.testIterator();
    }

    @Test
    public void testTestLeftAndRightGetters() throws Exception {
        diffResultTest.testLeftAndRightGetters();
    }

    @Test
    public void testTestListIsNonModifiable() throws Exception {
        diffResultTest.testListIsNonModifiable();
    }

    @Test
    public void testTestNoDifferencesString() throws Exception {
        diffResultTest.testNoDifferencesString();
    }

    @Test
    public void testTestNullLhs() throws Exception {
        diffResultTest.testNullLhs();
    }

    @Test
    public void testTestNullList() throws Exception {
        diffResultTest.testNullList();
    }

    @Test
    public void testTestNullRhs() throws Exception {
        diffResultTest.testNullRhs();
    }

    @Test
    public void testTestToStringOutput() throws Exception {
        diffResultTest.testToStringOutput();
    }

    @Test
    public void testTestToStringFormat() throws Exception {
        diffResultTest.testToStringFormat();
    }

    @Test
    public void testTestToStringSpecifyStyleOutput() throws Exception {
        diffResultTest.testToStringSpecifyStyleOutput();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme