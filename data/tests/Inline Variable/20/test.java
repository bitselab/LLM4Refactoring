package org.springframework.r2dbc.core;

import org.springframework.r2dbc.core.R2dbcBeanPropertyRowMapperTests;
import org.junit.Test;

public class R2dbcBeanPropertyRowMapperTestsTest {
    
    R2dbcBeanPropertyRowMapperTests r2dbcBeanPropertyRowMapperTests = new R2dbcBeanPropertyRowMapperTests();

    @Test
    public void testMappingUnknownReadableRejected() throws Exception {
        r2dbcBeanPropertyRowMapperTests.mappingUnknownReadableRejected();
    }

    @Test
    public void testMappingOutParametersAccepted() throws Exception {
        r2dbcBeanPropertyRowMapperTests.mappingOutParametersAccepted();
    }

    @Test
    public void testMappingRowSimpleObject() throws Exception {
        r2dbcBeanPropertyRowMapperTests.mappingRowSimpleObject();
    }

    @Test
    public void testMappingRowMissingAttributeAccepted() throws Exception {
        r2dbcBeanPropertyRowMapperTests.mappingRowMissingAttributeAccepted();
    }

    @Test
    public void testMappingRowWithDifferentName() throws Exception {
        r2dbcBeanPropertyRowMapperTests.mappingRowWithDifferentName();
    }

    @Test
    public void testRowTypeAndMappingTypeMisaligned() throws Exception {
        r2dbcBeanPropertyRowMapperTests.rowTypeAndMappingTypeMisaligned();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme