package org.springframework.messaging.core;

import org.springframework.messaging.core.GenericMessagingTemplateTests；
import org.junit.Test;

public class GenericMessagingTemplateTestsTest {

    GenericMessagingTemplateTests genericMessagingTemplateTests;

    @Test
    public void testSetup() throws Exception {
        genericMessagingTemplateTests.setup();
    }

    @Test
    public void testSendWithTimeout() throws Exception {
        genericMessagingTemplateTests.sendWithTimeout();
    }

    @Test
    public void testSendWithTimeoutMutable() throws Exception {
        genericMessagingTemplateTests.sendWithTimeoutMutable();
    }

    @Test
    public void testSendAndReceive() throws Exception {
        genericMessagingTemplateTests.sendAndReceive();
    }

    @Test
    public void testSendAndReceiveTimeout() throws Exception {
        genericMessagingTemplateTests.sendAndReceiveTimeout();
    }

    @Test
    public void testSendAndReceiveVariableTimeout() throws Exception {
        genericMessagingTemplateTests.sendAndReceiveVariableTimeout();
    }

    @Test
    public void testSendAndReceiveVariableTimeoutCustomHeaders() throws Exception {
        genericMessagingTemplateTests.sendAndReceiveVariableTimeoutCustomHeaders();
    }

    @Test
    public void testConvertAndSendWithSimpMessageHeaders() throws Exception {
        genericMessagingTemplateTests.convertAndSendWithSimpMessageHeaders();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme