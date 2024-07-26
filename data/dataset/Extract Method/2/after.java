package org.apache.commons.io.input;

import static org.apache.commons.io.IOUtils.EOF;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link ClosedInputStream}.
 */
public class ClosedInputStreamTest {

    private void assertEof(final ClosedInputStream cis) {
        assertEquals(EOF, cis.read(), "read()");
    }

    @Test
    public void testRead() throws Exception {
        try (ClosedInputStream cis = new ClosedInputStream()) {
            assertEof(cis);
        }
    }

    @Test
    public void testReadArray() throws Exception {
        try (ClosedInputStream cis = new ClosedInputStream()) {
            assertEquals(EOF, cis.read(new byte[4096]));
            assertEquals(EOF, cis.read(new byte[1]));
            assertEquals(EOF, cis.read(new byte[0]));
        }
    }

    @Test
    public void testReadArrayIndex() throws Exception {
        try (ClosedInputStream cis = new ClosedInputStream()) {
            assertEquals(EOF, cis.read(new byte[4096], 0, 1));
            assertEquals(EOF, cis.read(new byte[1], 0, 1));
            assertEquals(EOF, cis.read(new byte[0], 0, 0));
        }
    }

    @Test
    public void testSingleton() throws Exception {
        try (@SuppressWarnings("deprecation")
             ClosedInputStream cis = ClosedInputStream.CLOSED_INPUT_STREAM) {
            assertEof(cis);
        }
        try (ClosedInputStream cis = ClosedInputStream.INSTANCE) {
            assertEof(cis);
        }
    }

}