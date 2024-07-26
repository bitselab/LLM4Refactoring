package org.eclipse.jetty.deploy.providers;

import org.eclipse.jetty.deploy.providers.WebAppProviderTest;
import org.junit.Test;

public class WebAppProviderTestTest {
    
    WebAppProviderTest webAppProviderTest = new WebAppProviderTest();

    @Test
    public void testTeardownEnvironment() throws Exception {
        webAppProviderTest.teardownEnvironment();
    }

    @Test
    public void testTestStartupContext() throws Exception {
        webAppProviderTest.testStartupContext();
    }

    @Test
    public void testTestStartupSymlinkContext() throws Exception {
        webAppProviderTest.testStartupSymlinkContext();
    }

    @Test
    public void testTestWebappSymlinkDir() throws Exception {
        webAppProviderTest.testWebappSymlinkDir();
    }

    @Test
    public void testTestBaseDirSymlink() throws Exception {
        webAppProviderTest.testBaseDirSymlink();
    }

    @Test
    public void testTestDelayedDeploy() throws Exception {
        webAppProviderTest.testDelayedDeploy();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme