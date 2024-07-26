package org.apache.hadoop.yarn.client.api.impl;

import org.apache.hadoop.yarn.client.api.impl.TestNMClient;
import org.junit.Test;

public class TestNMClientTest {
    
    TestNMClient testNMClient = new TestNMClient();

    @Test
    public void testTearDown() throws Exception {
        testNMClient.tearDown();
    }

    @Test
    public void testTestNMClientNoCleanupOnStop() throws Exception {
        testNMClient.testNMClientNoCleanupOnStop();
    }

    @Test
    public void testTestNMClient() throws Exception {
        testNMClient.testNMClient();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme