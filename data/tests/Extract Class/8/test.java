package org.hibernate.search.integrationtest.mapper.pojo.mapping.definition;

import org.hibernate.search.integrationtest.mapper.pojo.mapping.definition.VectorFieldIT;
import org.junit.Test;

public class VectorFieldITTest {
    
    VectorFieldIT vectorFieldIT = new VectorFieldIT();

    @Test
    public void testDefaultAttributes() throws Exception {
        vectorFieldIT.defaultAttributes();
    }

    @Test
    public void testBeamWidth() throws Exception {
        vectorFieldIT.beamWidth();
    }

    @Test
    public void testMaxConnections() throws Exception {
        vectorFieldIT.maxConnections();
    }

    @Test
    public void testName() throws Exception {
        vectorFieldIT.name();
    }

    @Test
    public void testProjectable() throws Exception {
        vectorFieldIT.projectable();
    }

    @Test
    public void testSearchable() throws Exception {
        vectorFieldIT.searchable();
    }

    @Test
    public void testVectorSimilarity() throws Exception {
        vectorFieldIT.vectorSimilarity();
    }

    @Test
    public void testCustomBridge_explicitFieldType() throws Exception {
        vectorFieldIT.customBridge_explicitFieldType();
    }

    @Test
    public void testCustomBridge_withParams_annotationMapping() throws Exception {
        vectorFieldIT.customBridge_withParams_annotationMapping();
    }

    @Test
    public void testCustomBridge_implicitFieldType() throws Exception {
        vectorFieldIT.customBridge_implicitFieldType();
    }

    @Test
    public void testCustomBridge_withParams_programmaticMapping() throws Exception {
        vectorFieldIT.customBridge_withParams_programmaticMapping();
    }

    @Test
    public void testDefaultBridge_invalidFieldType() throws Exception {
        vectorFieldIT.defaultBridge_invalidFieldType();
    }

    @Test
    public void testCustomBridge_implicitFieldType_invalid() throws Exception {
        vectorFieldIT.customBridge_implicitFieldType_invalid();
    }

    @Test
    public void testCustomBridge_explicitFieldType_invalid() throws Exception {
        vectorFieldIT.customBridge_explicitFieldType_invalid();
    }

    @Test
    public void testCustomBridge_implicitFieldType_generic() throws Exception {
        vectorFieldIT.customBridge_implicitFieldType_generic();
    }

    @Test
    public void testCustomBridge_vectorDimensionUnknown() throws Exception {
        vectorFieldIT.customBridge_vectorDimensionUnknown();
    }

    @Test
    public void testValueExtractorsEnabled() throws Exception {
        vectorFieldIT.valueExtractorsEnabled();
    }

    @Test
    public void testValueExtractorsEnabled_defaultExtractorPathNotSupported() throws Exception {
        vectorFieldIT.valueExtractorsEnabled_defaultExtractorPathNotSupported();
    }

    @Test
    public void testCustomBridge_dimensionFromAnnotationTypeInBridge() throws Exception {
        vectorFieldIT.customBridge_dimensionFromAnnotationTypeInBridge();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme