package org.apache.hadoop.fs.azurebfs.services;

import org.apache.hadoop.fs.azurebfs.services.TestAbfsRestOperationMockFailures;
import org.junit.Test;

public class TestAbfsRestOperationMockFailuresTest {
    
    TestAbfsRestOperationMockFailures testAbfsRestOperationMockFailures = new TestAbfsRestOperationMockFailures();

    @Test
    public void testTestClientRequestIdForConnectTimeoutRetry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdForConnectTimeoutRetry();
    }

    @Test
    public void testTestClientRequestIdForConnectAndReadTimeoutRetry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdForConnectAndReadTimeoutRetry();
    }

    @Test
    public void testTestClientRequestIdForReadTimeoutRetry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdForReadTimeoutRetry();
    }

    @Test
    public void testTestClientRequestIdForUnknownHostRetry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdForUnknownHostRetry();
    }

    @Test
    public void testTestClientRequestIdForConnectionResetRetry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdForConnectionResetRetry();
    }

    @Test
    public void testTestClientRequestIdForUnknownSocketExRetry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdForUnknownSocketExRetry();
    }

    @Test
    public void testTestClientRequestIdForIOERetry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdForIOERetry();
    }

    @Test
    public void testTestClientRequestIdFor400Retry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdFor400Retry();
    }

    @Test
    public void testTestClientRequestIdFor500Retry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdFor500Retry();
    }

    @Test
    public void testTestClientRequestIdFor503INGRetry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdFor503INGRetry();
    }

    @Test
    public void testTestClientRequestIdFor503egrRetry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdFor503egrRetry();
    }

    @Test
    public void testTestClientRequestIdFor503OPRRetry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdFor503OPRRetry();
    }

    @Test
    public void testTestClientRequestIdFor503OtherRetry() throws Exception {
        testAbfsRestOperationMockFailures.testClientRequestIdFor503OtherRetry();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme