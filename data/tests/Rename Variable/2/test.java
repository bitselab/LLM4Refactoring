package org.apache.commons.io.input;

import org.apache.commons.io.input.NullInputStreamTest;
import org.junit.Test;

public class NullInputStreamTestTest {
    
    NullInputStreamTest nullInputStreamTest = new NullInputStreamTest();

    @Test
    public void testTestEOFException() throws Exception {
        nullInputStreamTest.testEOFException();
    }

    @Test
    public void testTestMarkAndReset() throws Exception {
        nullInputStreamTest.testMarkAndReset();
    }

    @Test
    public void testTestMarkNotSupported() throws Exception {
        nullInputStreamTest.testMarkNotSupported();
    }

    @Test
    public void testTestRead() throws Exception {
        nullInputStreamTest.testRead();
    }

    @Test
    public void testTestReadByteArray() throws Exception {
        nullInputStreamTest.testReadByteArray();
    }

    @Test
    public void testTestSkip() throws Exception {
        nullInputStreamTest.testSkip();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme