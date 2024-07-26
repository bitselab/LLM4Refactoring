package org.elasticsearch.xpack.ql.util;

import org.apache.lucene.geo.GeoEncodingUtils;
import org.apache.lucene.geo.XYEncodingUtils;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.geometry.Geometry;
import org.elasticsearch.geometry.Point;
import org.elasticsearch.geometry.utils.GeometryValidator;
import org.elasticsearch.geometry.utils.WellKnownBinary;
import org.elasticsearch.geometry.utils.WellKnownText;

import java.nio.ByteOrder;

import static org.apache.lucene.geo.GeoEncodingUtils.encodeLatitude;
import static org.apache.lucene.geo.GeoEncodingUtils.encodeLongitude;

public enum SpatialCoordinateTypes {
    GEO {
        public Point longAsPoint(long encoded) {
            return new Point(GeoEncodingUtils.decodeLongitude((int) encoded), GeoEncodingUtils.decodeLatitude((int) (encoded >>> 32)));
        }

        public long pointAsLong(double x, double y) {
            int latitudeEncoded = encodeLatitude(y);
            int longitudeEncoded = encodeLongitude(x);
            return (((long) latitudeEncoded) << 32) | (longitudeEncoded & 0xFFFFFFFFL);
        }
    },
    CARTESIAN {

        private static final int MAX_VAL_ENCODED = XYEncodingUtils.encode((float) XYEncodingUtils.MAX_VAL_INCL);
        private static final int MIN_VAL_ENCODED = XYEncodingUtils.encode((float) XYEncodingUtils.MIN_VAL_INCL);

        public Point longAsPoint(long encoded) {
            final int x = checkCoordinate((int) (encoded >>> 32));
            final int y = checkCoordinate((int) (encoded & 0xFFFFFFFF));
            return new Point(XYEncodingUtils.decode(x), XYEncodingUtils.decode(y));
        }

        private int checkCoordinate(int i) {
            if (i > MAX_VAL_ENCODED || i < MIN_VAL_ENCODED) {
                throw new IllegalArgumentException("Failed to convert invalid encoded value to cartesian point");
            }
            return i;
        }

        public long pointAsLong(double x, double y) {
            final long xi = XYEncodingUtils.encode((float) x);
            final long yi = XYEncodingUtils.encode((float) y);
            return (yi & 0xFFFFFFFFL) | xi << 32;
        }
    };

    public abstract Point longAsPoint(long encoded);

    public abstract long pointAsLong(double x, double y);

    public long wkbAsLong(BytesRef wkb) {
        Geometry geometry = WellKnownBinary.fromWKB(GeometryValidator.NOOP, false, wkb.bytes, wkb.offset, wkb.length);
        if (geometry instanceof Point point) {
            return pointAsLong(point.getX(), point.getY());
        } else {
            throw new IllegalArgumentException("Unsupported geometry: " + geometry.type());
        }
    }

    public BytesRef longAsWkb(long encoded) {
        return pointAsWKB(longAsPoint(encoded));
    }

    public String asWkt(Geometry geometry) {
        return WellKnownText.toWKT(geometry);
    }

    public BytesRef pointAsWKB(Geometry geometry) {
        return new BytesRef(WellKnownBinary.toWKB(geometry, ByteOrder.LITTLE_ENDIAN));
    }

    public BytesRef wktToWkb(String wkt) {
        // TODO: we should be able to transform WKT to WKB without building the geometry
        // we should as well use different validator for cartesian and geo?
        try {
            Geometry geometry = WellKnownText.fromWKT(GeometryValidator.NOOP, false, wkt);
            return new BytesRef(WellKnownBinary.toWKB(geometry, ByteOrder.LITTLE_ENDIAN));
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse WKT: " + e.getMessage(), e);
        }
    }

    public String wkbToWkt(BytesRef wkb) {
        return WellKnownText.fromWKB(wkb.bytes, wkb.offset, wkb.length);
    }
}