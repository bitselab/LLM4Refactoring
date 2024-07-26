package org.springframework.test.context.aot;

import org.springframework.test.context.aot.TestContextAotGeneratorTests;
import org.junit.Test;

public class TestContextAotGeneratorTestsTest {

    TestContextAotGeneratorTests testContextAotGeneratorTests = new TestContextAotGeneratorTests();

    @Test
    public void testEndToEndTests() throws Exception {
        testContextAotGeneratorTests.endToEndTests();
    }

    @Test
    public void testProcessAheadOfTimeWithBasicTests() throws Exception {
        testContextAotGeneratorTests.processAheadOfTimeWithBasicTests();
    }

    @Test
    public void testProcessAheadOfTimeWithXmlTests() throws Exception {
        testContextAotGeneratorTests.processAheadOfTimeWithXmlTests();
    }

    @Test
    public void testProcessAheadOfTimeWithWebTests() throws Exception {
        testContextAotGeneratorTests.processAheadOfTimeWithWebTests();
    }

    @Test
    public void testResetFlag() throws Exception {
        testContextAotGeneratorTests.resetFlag();
    }

    @Test
    public void testFailOnErrorEnabledByDefault() throws Exception {
        testContextAotGeneratorTests.failOnErrorEnabledByDefault();
    }

    @Test
    public void testFailOnErrorEnabledViaSpringProperty() throws Exception {
        testContextAotGeneratorTests.failOnErrorEnabledViaSpringProperty("spring.test.aot.processing.failOnError");
    }

    @Test
    public void testFailOnErrorDisabledViaSpringProperty() throws Exception {
        testContextAotGeneratorTests.failOnErrorDisabledViaSpringProperty("spring.test.aot.processing.failOnError");
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme