package com.github.javaparser.printer;

import com.github.javaparser.printer.XmlPrinterTest;
import org.junit.Test;

public class XmlPrinterTestTest {
   
    XmlPrinterTest xmlPrinterTest = new XmlPrinterTest();

    @Test
    public void testSetupDocumentBuilder() throws Exception {
        XmlPrinterTest.setupDocumentBuilder();
    }

    @Test
    public void testSetupTransformerFactory() throws Exception {
        XmlPrinterTest.setupTransformerFactory();
    }

    @Test
    public void testTestWithType() throws Exception {
        xmlPrinterTest.testWithType();
    }

    @Test
    public void testTestWithoutType() throws Exception {
        xmlPrinterTest.testWithoutType();
    }

    @Test
    public void testTestList() throws Exception {
        xmlPrinterTest.testList();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme