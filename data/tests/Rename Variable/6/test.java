package org.apache.hadoop.fs.azurebfs.services;

import org.apache.hadoop.fs.azurebfs.services.ITestAbfsInputStreamReadFooter;
import org.junit.Test;

public class ITestAbfsInputStreamReadFooterTest {
    
    ITestAbfsInputStreamReadFooter iTestAbfsInputStreamReadFooter = new ITestAbfsInputStreamReadFooter();

    @Test
    public void testTestOnlyOneServerCallIsMadeWhenTheConfIsTrue() throws Exception {
        iTestAbfsInputStreamReadFooter.testOnlyOneServerCallIsMadeWhenTheConfIsTrue();
    }

    @Test
    public void testTestMultipleServerCallsAreMadeWhenTheConfIsFalse() throws Exception {
        iTestAbfsInputStreamReadFooter.testMultipleServerCallsAreMadeWhenTheConfIsFalse();
    }

    @Test
    public void testTestSeekToBeginAndReadWithConfTrue() throws Exception {
        iTestAbfsInputStreamReadFooter.testSeekToBeginAndReadWithConfTrue();
    }

    @Test
    public void testTestSeekToBeginAndReadWithConfFalse() throws Exception {
        iTestAbfsInputStreamReadFooter.testSeekToBeginAndReadWithConfFalse();
    }

    @Test
    public void testTestSeekToBeforeFooterAndReadWithConfTrue() throws Exception {
        iTestAbfsInputStreamReadFooter.testSeekToBeforeFooterAndReadWithConfTrue();
    }

    @Test
    public void testTestSeekToBeforeFooterAndReadWithConfFalse() throws Exception {
        iTestAbfsInputStreamReadFooter.testSeekToBeforeFooterAndReadWithConfFalse();
    }

    @Test
    public void testTestSeekToFooterAndReadWithConfTrue() throws Exception {
        iTestAbfsInputStreamReadFooter.testSeekToFooterAndReadWithConfTrue();
    }

    @Test
    public void testTestSeekToFooterAndReadWithConfFalse() throws Exception {
        iTestAbfsInputStreamReadFooter.testSeekToFooterAndReadWithConfFalse();
    }

    @Test
    public void testTestSeekToAfterFooterAndReadWithConfTrue() throws Exception {
        iTestAbfsInputStreamReadFooter.testSeekToAfterFooterAndReadWithConfTrue();
    }

    @Test
    public void testTestSeekToToAfterFooterAndReadWithConfFalse() throws Exception {
        iTestAbfsInputStreamReadFooter.testSeekToToAfterFooterAndReadWithConfFalse();
    }

    @Test
    public void testTestSeekToEndAndReadWithConfTrue() throws Exception {
        iTestAbfsInputStreamReadFooter.testSeekToEndAndReadWithConfTrue();
    }

    @Test
    public void testTestSeekToEndAndReadWithConfFalse() throws Exception {
        iTestAbfsInputStreamReadFooter.testSeekToEndAndReadWithConfFalse();
    }

    @Test
    public void testTestPartialReadWithNoData() throws Exception {
        iTestAbfsInputStreamReadFooter.testPartialReadWithNoData();
    }

    @Test
    public void testTestPartialReadWithSomeData() throws Exception {
        iTestAbfsInputStreamReadFooter.testPartialReadWithSomeData();
    }

    @Test
    public void testTestFooterReadBufferSizeConfiguration() throws Exception {
        iTestAbfsInputStreamReadFooter.testFooterReadBufferSizeConfiguration();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme