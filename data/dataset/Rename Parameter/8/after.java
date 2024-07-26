package org.hibernate.search.backend.lucene.lowlevel.comparator.impl;

import java.io.IOException;

import org.hibernate.search.backend.lucene.lowlevel.docvalues.impl.DoubleMultiValuesToSingleValuesSource;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.LeafFieldComparator;
import org.apache.lucene.search.Pruning;
import org.apache.lucene.search.comparators.DoubleComparator;

public class DoubleValuesSourceComparator extends DoubleComparator {

    private final DoubleMultiValuesToSingleValuesSource source;

    public DoubleValuesSourceComparator(int numHits, String field, Double missingValue, boolean reversed,
                                        Pruning pruning, DoubleMultiValuesToSingleValuesSource source) {
        super( numHits, field, missingValue, reversed, pruning );
        this.source = source;
    }

    @Override
    public LeafFieldComparator getLeafComparator(LeafReaderContext context) throws IOException {
        return new DoubleValuesSourceLeafComparator( context );
    }

    private class DoubleValuesSourceLeafComparator extends DoubleLeafComparator {
        DoubleValuesSourceLeafComparator(LeafReaderContext context) throws IOException {
            super( context );
        }

        @Override
        protected NumericDocValues getNumericDocValues(LeafReaderContext context, String field) throws IOException {
            return source.getValues( context, null ).getRawDoubleValues();
        }
    }

}