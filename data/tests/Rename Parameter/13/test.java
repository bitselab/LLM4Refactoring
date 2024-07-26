package org.junit.jupiter.engine.extension;

import org.junit.jupiter.engine.extension.AutoCloseTests；
import org.junit.Test;

public class AutoCloseTestsTest {
    
    AutoCloseTests autoCloseTests = new AutoCloseTests();

    @Test
    public void testBlankCloseMethodName() throws Exception {
        autoCloseTests.blankCloseMethodName();
    }

    @Test
    public void testPrimitiveTypeCannotBeClosed() throws Exception {
        autoCloseTests.primitiveTypeCannotBeClosed();
    }

    @Test
    public void testArrayCannotBeClosed() throws Exception {
        autoCloseTests.arrayCannotBeClosed();
    }

    @Test
    public void testNullCannotBeClosed() throws Exception {
        autoCloseTests.nullCannotBeClosed(null);
    }

    @Test
    public void testNoCloseMethod() throws Exception {
        autoCloseTests.noCloseMethod();
    }

    @Test
    public void testNoShutdownMethod() throws Exception {
        autoCloseTests.noShutdownMethod();
    }

    @Test
    public void testSpyPermitsOnlyASingleAction() throws Exception {
        autoCloseTests.spyPermitsOnlyASingleAction();
    }

    @Test
    public void testFieldsAreProperlyClosedWithInstancePerMethodTestClass() throws Exception {
        autoCloseTests.fieldsAreProperlyClosedWithInstancePerMethodTestClass();
    }

    @Test
    public void testFieldsAreProperlyClosedWithInstancePerClassTestClass() throws Exception {
        autoCloseTests.fieldsAreProperlyClosedWithInstancePerClassTestClass();
    }

    @Test
    public void testFieldsAreProperlyClosedWithNestedTestClassesWithInstancePerMethod() throws Exception {
        autoCloseTests.fieldsAreProperlyClosedWithNestedTestClassesWithInstancePerMethod();
    }

    @Test
    public void testFieldsAreProperlyClosedWithNestedTestClassesWithInstancePerClass() throws Exception {
        autoCloseTests.fieldsAreProperlyClosedWithNestedTestClassesWithInstancePerClass();
    }

    @Test
    public void testFieldsAreProperlyClosedWithinTestClassHierarchy() throws Exception {
        autoCloseTests.fieldsAreProperlyClosedWithinTestClassHierarchy();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme