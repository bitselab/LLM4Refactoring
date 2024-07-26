package org.apache.commons.lang3.concurrent;

import org.apache.commons.io.input.BrokenInputStreamTest;
import org.junit.Test;

public class BrokenInputStreamTestTest {
    
    BrokenInputStreamTest brokenInputStreamTest = new BrokenInputStreamTest();

    @Test
    public void testTestAvailable() throws Exception {
        brokenInputStreamTest.testAvailable(Exception.class);
    }

    @Test
    public void testTestClose() throws Exception {
        brokenInputStreamTest.testClose(Exception.class);
    }

    @Test
    public void testTestInstance() throws Exception {
        brokenInputStreamTest.testInstance();
    }

    @Test
    public void testTestRead() throws Exception {
        brokenInputStreamTest.testRead(Exception.class);
    }

    @Test
    public void testTestReset() throws Exception {
        brokenInputStreamTest.testReset(Exception.class);
    }

    @Test
    public void testTestSkip() throws Exception {
        brokenInputStreamTest.testSkip(Exception.class);
    }

    @Test
    public void testTestTryWithResources() throws Exception {
        brokenInputStreamTest.testTryWithResources();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme