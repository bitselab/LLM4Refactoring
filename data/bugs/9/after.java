package org.hibernate.search.backend.lucene.lowlevel.codec.impl;

import static org.apache.lucene.util.hnsw.HnswGraphBuilder.DEFAULT_BEAM_WIDTH;
import static org.apache.lucene.util.hnsw.HnswGraphBuilder.DEFAULT_MAX_CONN;

import java.io.IOException;
import java.util.Objects;

import org.apache.lucene.codecs.KnnVectorsFormat;
import org.apache.lucene.codecs.KnnVectorsReader;
import org.apache.lucene.codecs.KnnVectorsWriter;
import org.apache.lucene.codecs.lucene95.Lucene95HnswVectorsFormat;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

public class HibernateSearchKnnVectorsFormat extends KnnVectorsFormat {
    public static final int DEFAULT_MAX_DIMENSIONS = KnnVectorsFormat.DEFAULT_MAX_DIMENSIONS;
    private static final KnnVectorsFormat DEFAULT_KNN_VECTORS_FORMAT = new HibernateSearchKnnVectorsFormat();

    public static KnnVectorsFormat defaultFormat() {
        return DEFAULT_KNN_VECTORS_FORMAT;
    }

    private final KnnVectorsFormat delegate;
    private final int maxConnections;

    private final int beamWidth;

    public HibernateSearchKnnVectorsFormat() {
        this( DEFAULT_MAX_CONN, DEFAULT_BEAM_WIDTH );
    }

    public HibernateSearchKnnVectorsFormat(int maxConnection, int beamWidth) {
        super( HibernateSearchKnnVectorsFormat.class.getSimpleName() );
        this.delegate = new Lucene95HnswVectorsFormat( maxConnection, beamWidth );
        this.maxConnections = maxConnection;
        this.beamWidth = beamWidth;
    }

    @Override
    public KnnVectorsWriter fieldsWriter(SegmentWriteState state) throws IOException {
        return delegate.fieldsWriter( state );
    }

    @Override
    public KnnVectorsReader fieldsReader(SegmentReadState state) throws IOException {
        return delegate.fieldsReader( state );
    }

    @Override
    public int getMaxDimensions(String fieldName) {
        // TODO : vector : we can make this configurable, apparently there are models that produce larger vectors than this default allows.
        return DEFAULT_MAX_DIMENSIONS;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        HibernateSearchKnnVectorsFormat that = (HibernateSearchKnnVectorsFormat) o;
        return maxConnections == that.maxConnections && beamWidth == that.beamWidth;
    }

    @Override
    public int hashCode() {
        return Objects.hash( maxConnections, beamWidth );
    }

    @Override
    public String toString() {
        return "HibernateSearchKnnVectorsFormat{" +
                "maxConnections=" + maxConnections +
                ", beamWidth=" + beamWidth +
                '}';
    }
}