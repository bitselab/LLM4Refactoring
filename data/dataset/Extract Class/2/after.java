package org.apache.commons.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.util.stream.Stream;

/**
 * Factory for parameterized tests of BrokenInputStream, BrokenReader, BrokenOutputStream, and BrokenWriter.
 */
public class BrokenTestFactories {

    public static final class CustomException extends Exception {

        private static final long serialVersionUID = 1L;

    }

    public static Stream<Class<? extends Throwable>> parameters() {
        // @formatter:off
        return Stream.of(
                IOException.class,
                FileNotFoundException.class,
                FileSystemNotFoundException.class,
                RuntimeException.class,
                IllegalArgumentException.class,
                IllegalStateException.class,
                Error.class,
                ExceptionInInitializerError.class,
                CustomException.class
        );
        // @formatter:on
    }
}

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link BrokenInputStream}.
 */
public class BrokenInputStreamTest {

    private static BrokenInputStream createBrokenInputStream(final Throwable exception) {
        if (exception instanceof IOException) {
            return new BrokenInputStream((IOException) exception);
        }
        return new BrokenInputStream(exception);
    }

    @ParameterizedTest
    @MethodSource("org.apache.commons.io.BrokenTestFactories#parameters")
    public void testAvailable(final Class<Exception> clazz) throws Exception {
        final Throwable exception = clazz.newInstance();
        @SuppressWarnings("resource")
        final BrokenInputStream stream = createBrokenInputStream(exception);
        assertEquals(exception, assertThrows(clazz, () -> stream.available()));
    }

    @ParameterizedTest
    @MethodSource("org.apache.commons.io.BrokenTestFactories#parameters")
    public void testClose(final Class<Exception> clazz) throws Exception {
        final Throwable exception = clazz.newInstance();
        @SuppressWarnings("resource")
        final BrokenInputStream stream = createBrokenInputStream(exception);
        assertEquals(exception, assertThrows(clazz, () -> stream.close()));
    }

    @Test
    public void testInstance() {
        assertNotNull(BrokenInputStream.INSTANCE);
    }

    @ParameterizedTest
    @MethodSource("org.apache.commons.io.BrokenTestFactories#parameters")
    public void testRead(final Class<Exception> clazz) throws Exception {
        final Throwable exception = clazz.newInstance();
        @SuppressWarnings("resource")
        final BrokenInputStream stream = createBrokenInputStream(exception);
        assertEquals(exception, assertThrows(clazz, () -> stream.read()));
        assertEquals(exception, assertThrows(clazz, () -> stream.read(new byte[1])));
        assertEquals(exception, assertThrows(clazz, () -> stream.read(new byte[1], 0, 1)));
    }

    @ParameterizedTest
    @MethodSource("org.apache.commons.io.BrokenTestFactories#parameters")
    public void testReset(final Class<Exception> clazz) throws Exception {
        final Throwable exception = clazz.newInstance();
        @SuppressWarnings("resource")
        final BrokenInputStream stream = createBrokenInputStream(exception);
        assertEquals(exception, assertThrows(clazz, () -> stream.reset()));
    }

    @ParameterizedTest
    @MethodSource("org.apache.commons.io.BrokenTestFactories#parameters")
    public void testSkip(final Class<Exception> clazz) throws Exception {
        final Throwable exception = clazz.newInstance();
        @SuppressWarnings("resource")
        final BrokenInputStream stream = createBrokenInputStream(exception);
        assertEquals(exception, assertThrows(clazz, () -> stream.skip(1)));
    }

    @Test
    public void testTryWithResources() {
        final IOException thrown = assertThrows(IOException.class, () -> {
            try (InputStream newStream = new BrokenInputStream()) {
                newStream.read();
            }
        });
        assertEquals("Broken input stream", thrown.getMessage());

        final Throwable[] suppressed = thrown.getSuppressed();
        assertEquals(1, suppressed.length);
        assertEquals(IOException.class, suppressed[0].getClass());
        assertEquals("Broken input stream", suppressed[0].getMessage());
    }

}