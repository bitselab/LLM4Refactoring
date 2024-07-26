package org.springframework.validation;

import org.springframework.validation.DataBinderConstructTests;
import org.junit.Test;

public class DataBinderConstructTestsTest {

    DataBinderConstructTests dataBinderConstructTests = new DataBinderConstructTests();

    @Test
    public void testDataClassBinding() throws Exception {
        dataBinderConstructTests.dataClassBinding();
    }

    @Test
    public void testDataClassBindingWithOptionalParameter() throws Exception {
        dataBinderConstructTests.dataClassBindingWithOptionalParameter();
    }

    @Test
    public void testDataClassBindingWithMissingParameter() throws Exception {
        dataBinderConstructTests.dataClassBindingWithMissingParameter();
    }

    @Test
    public void testDataClassBindingWithNestedOptionalParameterWithMissingParameter() throws Exception {
        dataBinderConstructTests.dataClassBindingWithNestedOptionalParameterWithMissingParameter();
    }

    @Test
    public void testDataClassBindingWithConversionError() throws Exception {
        dataBinderConstructTests.dataClassBindingWithConversionError();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme