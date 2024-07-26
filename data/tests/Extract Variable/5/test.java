package org.apache.flink.runtime.executiongraph.failover;

import org.apache.flink.runtime.executiongraph.failover.ExponentialDelayRestartBackoffTimeStrategyTest;
import org.junit.Test;

public class ExponentialDelayRestartBackoffTimeStrategyTestTest {
    
    ExponentialDelayRestartBackoffTimeStrategyTest exponentialDelayRestartBackoffTimeStrategyTest;

    @Test
    public void testTestMaxAttempts() throws Exception {
        exponentialDelayRestartBackoffTimeStrategyTest.testMaxAttempts();
    }

    @Test
    public void testTestNotCallNotifyFailure() throws Exception {
        exponentialDelayRestartBackoffTimeStrategyTest.testNotCallNotifyFailure();
    }

    @Test
    public void testTestInitialBackoff() throws Exception {
        exponentialDelayRestartBackoffTimeStrategyTest.testInitialBackoff();
    }

    @Test
    public void testTestMaxBackoff() throws Exception {
        exponentialDelayRestartBackoffTimeStrategyTest.testMaxBackoff();
    }

    @Test
    public void testTestResetBackoff() throws Exception {
        exponentialDelayRestartBackoffTimeStrategyTest.testResetBackoff();
    }

    @Test
    public void testTestBackoffMultiplier() throws Exception {
        exponentialDelayRestartBackoffTimeStrategyTest.testBackoffMultiplier();
    }

    @Test
    public void testTestJitter() throws Exception {
        exponentialDelayRestartBackoffTimeStrategyTest.testJitter();
    }

    @Test
    public void testTestJitterNoHigherThanMax() throws Exception {
        exponentialDelayRestartBackoffTimeStrategyTest.testJitterNoHigherThanMax();
    }

    @Test
    public void testTestMultipleSettings() throws Exception {
        exponentialDelayRestartBackoffTimeStrategyTest.testMultipleSettings();
    }

    @Test
    public void testTestMergeMultipleExceptionsIntoOneAttempt() throws Exception {
        exponentialDelayRestartBackoffTimeStrategyTest.testMergeMultipleExceptionsIntoOneAttempt();
    }

    @Test
    public void testTestMergingExceptionsWorksWithResetting() throws Exception {
        exponentialDelayRestartBackoffTimeStrategyTest.testMergingExceptionsWorksWithResetting();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme