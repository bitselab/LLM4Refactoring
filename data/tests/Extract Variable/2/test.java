package org.apache.commons.io.input;

import org.apache.commons.io.input.BoundedInputStreamTest;
import org.junit.Test;

public class BoundedInputStreamTestTest {
    
    BoundedInputStreamTest boundedInputStreamTest = new BoundedInputStreamTest();

    @Test
    public void testTestOnMaxLength() throws Exception {
        boundedInputStreamTest.testOnMaxLength();
    }

    @Test
    public void testTestReadArray() throws Exception {
        boundedInputStreamTest.testReadArray();
    }

    @Test
    public void testTestReadSingle() throws Exception {
        boundedInputStreamTest.testReadSingle();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme