package org.mockitousage.spies;

import org.mockitousage.spies.PartialMockingWithSpiesTest;
import org.junit.Test;

public class PartialMockingWithSpiesTestTest {
    
    PartialMockingWithSpiesTest partialMockingWithSpiesTest = new PartialMockingWithSpiesTest();

    @Test
    public void testPleaseMakeStackTracesClean() throws Exception {
        partialMockingWithSpiesTest.pleaseMakeStackTracesClean();
    }

    @Test
    public void testShouldCallRealMethdsEvenDelegatedToOtherSelfMethod() throws Exception {
        partialMockingWithSpiesTest.shouldCallRealMethdsEvenDelegatedToOtherSelfMethod();
    }

    @Test
    public void testShouldAllowStubbingOfMethodsThatDelegateToOtherMethods() throws Exception {
        partialMockingWithSpiesTest.shouldAllowStubbingOfMethodsThatDelegateToOtherMethods();
    }

    @Test
    public void testShouldAllowStubbingWithThrowablesMethodsThatDelegateToOtherMethods() throws Exception {
        partialMockingWithSpiesTest.shouldAllowStubbingWithThrowablesMethodsThatDelegateToOtherMethods();
    }

    @Test
    public void testShouldStackTraceGetFilteredOnUserExceptions() throws Exception {
        partialMockingWithSpiesTest.shouldStackTraceGetFilteredOnUserExceptions();
    }

    @Test
    public void testShouldStackTraceGetFilteredOnUserExceptionsReflection() throws Exception {
        partialMockingWithSpiesTest.shouldStackTraceGetFilteredOnUserExceptionsReflection();
    }

    @Test
    public void testShouldStackTraceGetFilteredOnUserExceptionsReflectionForJava21AndHigher() throws Exception {
        partialMockingWithSpiesTest.shouldStackTraceGetFilteredOnUserExceptionsReflectionForJava21AndHigher();
    }

    @Test
    public void testVerifyTheStackTrace() throws Exception {
        partialMockingWithSpiesTest.verifyTheStackTrace();
    }

    @Test
    public void testShouldVerify() throws Exception {
        partialMockingWithSpiesTest.shouldVerify();
    }

    @Test
    public void testShouldStub() throws Exception {
        partialMockingWithSpiesTest.shouldStub();
    }

    @Test
    public void testShouldDealWithPrivateFieldsOfSubclasses() throws Exception {
        partialMockingWithSpiesTest.shouldDealWithPrivateFieldsOfSubclasses();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme