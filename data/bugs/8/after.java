package org.hibernate.search.mapper.pojo.mapping.definition.programmatic.impl;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.VectorSimilarity;
import org.hibernate.search.mapper.pojo.bridge.binding.spi.FieldModelContributorContext;
import org.hibernate.search.mapper.pojo.extractor.mapping.programmatic.ContainerExtractorPath;
import org.hibernate.search.mapper.pojo.mapping.building.spi.PojoPropertyMetadataContributor;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.PropertyMappingStep;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.PropertyMappingVectorFieldStep;

class PropertyMappingVectorFieldStepImpl extends AbstractPropertyMappingFieldOptionsStep<PropertyMappingVectorFieldStepImpl>
        implements PropertyMappingVectorFieldStep, PojoPropertyMetadataContributor {

    PropertyMappingVectorFieldStepImpl(PropertyMappingStep parent, int dimension, String relativeFieldName) {
        super( parent, relativeFieldName, FieldModelContributorContext::vectorTypeOptionsStep );
        extractors( ContainerExtractorPath.noExtractors() );
    }

    private PropertyMappingVectorFieldStepImpl configureField(Consumer<FieldModelContributorContext> configuration) {
        fieldModelContributor.add(configuration);
        return this;
    }

    @Override
    public PropertyMappingVectorFieldStep projectable(Projectable projectable) {
        return configureField(c -> c.vectorTypeOptionsStep().projectable(projectable));
    }

    @Override
    public PropertyMappingVectorFieldStep searchable(Searchable searchable) {
        // Assuming this method should be implemented similarly
        return configureField(c -> c.vectorTypeOptionsStep().searchable(searchable));
    }

    @Override
    public PropertyMappingVectorFieldStep vectorSimilarity(VectorSimilarity vectorSimilarity) {
        return configureField(c -> c.vectorTypeOptionsStep().vectorSimilarity(vectorSimilarity));
    }

    @Override
    public PropertyMappingVectorFieldStep beamWidth(int beamWidth) {
        return configureField(c -> c.vectorTypeOptionsStep().beamWidth(beamWidth));
    }

    @Override
    public PropertyMappingVectorFieldStep maxConnections(int maxConnections) {
        return configureField(c -> c.vectorTypeOptionsStep().maxConnections(maxConnections));
    }

    @Override
    public PropertyMappingVectorFieldStep indexNullAs(String indexNullAs) {
        return configureField(c -> c.indexNullAs(indexNullAs));
    }
}