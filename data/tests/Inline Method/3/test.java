package org.apache.commons.lang3;

import org.apache.commons.lang3.AbstractLangTest;
import org.junit.Test;

public class AbstractLangTestTest {
    
    AbstractLangTest abstractLangTest = new AbstractLangTest();

    @Test
    public void testAfter() throws Exception {
        abstractLangTest.after();
    }

    @Test
    public void testValidateNullToStringStyleRegistry() throws Exception {
        abstractLangTest.validateNullToStringStyleRegistry();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme