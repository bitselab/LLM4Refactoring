package org.junit.jupiter.engine.extension;

import org.junit.jupiter.engine.extension.AutoCloseTests;
import org.junit.Test;

public class AutoCloseSpyTest {

    AutoCloseTests.AutoCloseSpy autoCloseSpy = new AutoCloseSpy("");

    @Test
    public void testRun() throws Exception {
        autoCloseSpy.run();
    }

    @Test
    public void testClose() throws Exception {
        autoCloseSpy.close();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme