package org.apache.commons.io.input;

import org.apache.commons.io.input.ClosedInputStreamTest;
import org.junit.Test;

public class ClosedInputStreamTestTest {
    
    ClosedInputStreamTest closedInputStreamTest = new ClosedInputStreamTest();

    @Test
    public void testTestRead() throws Exception {
        closedInputStreamTest.testRead();
    }

    @Test
    public void testTestReadArray() throws Exception {
        closedInputStreamTest.testReadArray();
    }

    @Test
    public void testTestReadArrayIndex() throws Exception {
        closedInputStreamTest.testReadArrayIndex();
    }

    @Test
    public void testTestSingleton() throws Exception {
        closedInputStreamTest.testSingleton();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme