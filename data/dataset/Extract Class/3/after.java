package org.apache.commons.lang3.concurrent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.AbstractLangTest;
import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.Test;

public class BackgroundInitializerTest extends AbstractLangTest {
    /**
     * Helper method for checking whether the initialize() method was correctly
     * called. start() must already have been invoked.
     *
     * @param init the initializer to test
     */
    private void checkInitialize(final AbstractBackgroundInitializerTestImpl init) throws ConcurrentException {
        final Integer result = init.get().getInitializeCalls();
        assertEquals(1, result.intValue(), "Wrong result");
        assertEquals(1, init.getCloseableCounter().getInitializeCalls(), "Wrong number of invocations");
        assertNotNull(init.getFuture(), "No future");
    }

    /**
     * Tests whether initialize() is invoked.
     */
    @Test
    public void testInitialize() throws ConcurrentException {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        init.start();
        checkInitialize(init);
    }

    /**
     * Tries to obtain the executor before start(). It should not have been
     * initialized yet.
     */
    @Test
    public void testGetActiveExecutorBeforeStart() {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        assertNull(init.getActiveExecutor(), "Got an executor");
    }

    /**
     * Tests whether an external executor is correctly detected.
     */
    @Test
    public void testGetActiveExecutorExternal() throws InterruptedException, ConcurrentException {
        final ExecutorService exec = Executors.newSingleThreadExecutor();
        try {
            final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl(
                    exec);
            init.start();
            assertSame(exec, init.getActiveExecutor(), "Wrong executor");
            checkInitialize(init);
        } finally {
            exec.shutdown();
            exec.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    /**
     * Tests getActiveExecutor() for a temporary executor.
     */
    @Test
    public void testGetActiveExecutorTemp() throws ConcurrentException {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        init.start();
        assertNotNull(init.getActiveExecutor(), "No active executor");
        checkInitialize(init);
    }

    /**
     * Tests the execution of the background task if a temporary executor has to
     * be created.
     */
    @Test
    public void testInitializeTempExecutor() throws ConcurrentException {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        assertTrue(init.start(), "Wrong result of start()");
        checkInitialize(init);
        assertTrue(init.getActiveExecutor().isShutdown(), "Executor not shutdown");
    }

    /**
     * Tests whether an external executor can be set using the
     * setExternalExecutor() method.
     */
    @Test
    public void testSetExternalExecutor() throws ConcurrentException {
        final ExecutorService exec = Executors.newCachedThreadPool();
        try {
            final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
            init.setExternalExecutor(exec);
            assertEquals(exec, init.getExternalExecutor(), "Wrong executor service");
            assertTrue(init.start(), "Wrong result of start()");
            assertSame(exec, init.getActiveExecutor(), "Wrong active executor");
            checkInitialize(init);
            assertFalse(exec.isShutdown(), "Executor was shutdown");
        } finally {
            exec.shutdown();
        }
    }

    /**
     * Tests that setting an executor after start() causes an exception.
     *
     * @throws org.apache.commons.lang3.concurrent.ConcurrentException because the test implementation may throw it
     */
    @Test
    public void testSetExternalExecutorAfterStart() throws ConcurrentException, InterruptedException {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        init.start();
        final ExecutorService exec = Executors.newSingleThreadExecutor();
        try {
            assertThrows(IllegalStateException.class, () -> init.setExternalExecutor(exec));
            init.get();
        } finally {
            exec.shutdown();
            exec.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    /**
     * Tests invoking start() multiple times. Only the first invocation should
     * have an effect.
     */
    @Test
    public void testStartMultipleTimes() throws ConcurrentException {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        assertTrue(init.start(), "Wrong result for start()");
        for (int i = 0; i < 10; i++) {
            assertFalse(init.start(), "Could start again");
        }
        checkInitialize(init);
    }

    /**
     * Tests calling get() before start(). This should cause an exception.
     */
    @Test
    public void testGetBeforeStart() {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        assertThrows(IllegalStateException.class, init::get);
    }

    /**
     * Tests the get() method if background processing causes a runtime
     * exception.
     */
    @Test
    public void testGetRuntimeException() {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        final RuntimeException rex = new RuntimeException();
        init.ex = rex;
        init.start();
        final Exception ex = assertThrows(Exception.class, init::get);
        assertEquals(rex, ex, "Runtime exception not thrown");
    }

    /**
     * Tests the get() method if background processing causes a checked
     * exception.
     */
    @Test
    public void testGetCheckedException() {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        final Exception ex = new Exception();
        init.ex = ex;
        init.start();
        final ConcurrentException cex = assertThrows(ConcurrentException.class, init::get);
        assertEquals(ex, cex.getCause(), "Exception not thrown");
    }

    /**
     * Tests the get() method if waiting for the initialization is interrupted.
     *
     * @throws InterruptedException because we're making use of Java's concurrent API
     */
    @Test
    public void testGetInterruptedException() throws InterruptedException {
        final ExecutorService exec = Executors.newSingleThreadExecutor();
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl(
                exec);
        final CountDownLatch latch1 = new CountDownLatch(1);
        init.shouldSleep = true;
        init.start();
        final AtomicReference<InterruptedException> iex = new AtomicReference<>();
        final Thread getThread = new Thread() {
            @Override
            public void run() {
                try {
                    init.get();
                } catch (final ConcurrentException cex) {
                    if (cex.getCause() instanceof InterruptedException) {
                        iex.set((InterruptedException) cex.getCause());
                    }
                } finally {
                    assertTrue(isInterrupted(), "Thread not interrupted");
                    latch1.countDown();
                }
            }
        };
        getThread.start();
        getThread.interrupt();
        latch1.await();
        exec.shutdownNow();
        exec.awaitTermination(1, TimeUnit.SECONDS);
        assertNotNull(iex.get(), "No interrupted exception");
    }

    /**
     * Tests isStarted() before start() was called.
     */
    @Test
    public void testIsStartedFalse() {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        assertFalse(init.isStarted(), "Already started");
    }

    /**
     * Tests isStarted() after start().
     */
    @Test
    public void testIsStartedTrue() {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        init.start();
        assertTrue(init.isStarted(), "Not started");
    }

    /**
     * Tests isStarted() after the background task has finished.
     */
    @Test
    public void testIsStartedAfterGet() throws ConcurrentException {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        init.start();
        checkInitialize(init);
        assertTrue(init.isStarted(), "Not started");
    }

    /**
     * Tests isInitialized() before and after the background task has finished.
     */
    @Test
    public void testIsInitialized() throws ConcurrentException {
        final AbstractBackgroundInitializerTestImpl init = getBackgroundInitializerTestImpl();
        init.enableLatch();
        init.start();
        assertTrue(init.isStarted(), "Not started"); //Started and Initialized should return opposite values
        assertFalse(init.isInitialized(), "Initalized before releasing latch");
        init.releaseLatch();
        init.get(); //to ensure the initialize thread has completed.
        assertTrue(init.isInitialized(), "Not initalized after releasing latch");
    }

    protected AbstractBackgroundInitializerTestImpl getBackgroundInitializerTestImpl() {
        return new MethodBackgroundInitializerTestImpl();
    }

    protected AbstractBackgroundInitializerTestImpl getBackgroundInitializerTestImpl(final ExecutorService exec) {
        return new MethodBackgroundInitializerTestImpl(exec);
    }

    /**
     * A concrete implementation of BackgroundInitializer. It also overloads
     * some methods that simplify testing.
     */
    protected static class AbstractBackgroundInitializerTestImpl extends
            BackgroundInitializer<CloseableCounter> {
        /** An exception to be thrown by initialize(). */
        Exception ex;

        /** A flag whether the background task should sleep a while. */
        boolean shouldSleep;

        /** A latch tests can use to control when initialize completes. */
        final CountDownLatch latch = new CountDownLatch(1);
        boolean waitForLatch = false;

        /** An object containing the state we are testing */
        CloseableCounter counter = new CloseableCounter();

        AbstractBackgroundInitializerTestImpl() {
        }

        AbstractBackgroundInitializerTestImpl(final ExecutorService exec) {
            super(exec);
        }

        public void enableLatch() {
            waitForLatch = true;
        }

        public void releaseLatch() {
            latch.countDown();
        }

        public CloseableCounter getCloseableCounter() {
            return counter;
        }

        /**
         * Records this invocation. Optionally throws an exception or sleeps a
         * while.
         *
         * @throws Exception in case of an error
         */
        protected CloseableCounter initializeInternal() throws Exception {
            if (ex != null) {
                throw ex;
            }
            if (shouldSleep) {
                ThreadUtils.sleep(Duration.ofMinutes(1));
            }
            if (waitForLatch) {
                latch.await();
            }
            return counter.increment();
        }
    }

    protected static class CloseableCounter {
        /** The number of invocations of initialize(). */
        AtomicInteger initializeCalls = new AtomicInteger();

        public CloseableCounter increment() {
            initializeCalls.incrementAndGet();
            return this;
        }

        public int getInitializeCalls() {
            return initializeCalls.get();
        }
    }
}