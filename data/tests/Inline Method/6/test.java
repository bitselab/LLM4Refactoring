package org.apache.hadoop.fs.azurebfs;

import org.apache.hadoop.fs.azurebfs.ITestAzureBlobFileSystemAttributes;
import org.junit.Test;

public class ITestAzureBlobFileSystemAttributesTest {
   
    ITestAzureBlobFileSystemAttributes iTestAzureBlobFileSystemAttributes = new ITestAzureBlobFileSystemAttributes();

    @Test
    public void testTestSetGetXAttr() throws Exception {
        iTestAzureBlobFileSystemAttributes.testSetGetXAttr();
    }

    @Test
    public void testTestSetGetXAttrCreateReplace() throws Exception {
        iTestAzureBlobFileSystemAttributes.testSetGetXAttrCreateReplace();
    }

    @Test
    public void testTestSetGetXAttrReplace() throws Exception {
        iTestAzureBlobFileSystemAttributes.testSetGetXAttrReplace();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme