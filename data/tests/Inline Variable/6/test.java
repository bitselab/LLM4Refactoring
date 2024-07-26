package org.apache.hadoop.hdfs.server.federation.router;

import org.apache.hadoop.hdfs.server.federation.router.TestRouterAdmin;
import org.junit.Test;

public class TestRouterAdminTest {
    
    TestRouterAdmin testRouterAdmin = new TestRouterAdmin();

    @Test
    public void testGlobalSetUp() throws Exception {
        TestRouterAdmin.globalSetUp();
    }

    @Test
    public void testTearDown() throws Exception {
        TestRouterAdmin.tearDown();
    }

    @Test
    public void testTestSetup() throws Exception {
        testRouterAdmin.testSetup();
    }

    @Test
    public void testTestAddMountTable() throws Exception {
        testRouterAdmin.testAddMountTable();
    }

    @Test
    public void testTestAddDuplicateMountTable() throws Exception {
        testRouterAdmin.testAddDuplicateMountTable();
    }

    @Test
    public void testTestAddReadOnlyMountTable() throws Exception {
        testRouterAdmin.testAddReadOnlyMountTable();
    }

    @Test
    public void testTestAddOrderMountTable() throws Exception {
        testRouterAdmin.testAddOrderMountTable();
    }

    @Test
    public void testTestRemoveMountTable() throws Exception {
        testRouterAdmin.testRemoveMountTable();
    }

    @Test
    public void testTestEditMountTable() throws Exception {
        testRouterAdmin.testEditMountTable();
    }

    @Test
    public void testTestGetMountTable() throws Exception {
        testRouterAdmin.testGetMountTable();
    }

    @Test
    public void testTestGetSingleMountTableEntry() throws Exception {
        testRouterAdmin.testGetSingleMountTableEntry();
    }

    @Test
    public void testTestVerifyFileInDestinations() throws Exception {
        testRouterAdmin.testVerifyFileInDestinations();
    }

    @Test
    public void testTestNameserviceManager() throws Exception {
        testRouterAdmin.testNameserviceManager();
    }

    @Test
    public void testTestNameserviceManagerUnauthorized() throws Exception {
        testRouterAdmin.testNameserviceManagerUnauthorized();
    }

    @Test
    public void testTestNameserviceManagerWithRules() throws Exception {
        testRouterAdmin.testNameserviceManagerWithRules();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme