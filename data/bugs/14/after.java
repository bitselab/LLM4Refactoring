package org.apache.solr.util.circuitbreaker;

import java.lang.invoke.MethodHandles;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single CircuitBreaker that registers both a Memory and a CPU CircuitBreaker. This is only for
 * backward compatibility with the 9.x versions prior to 9.4.
 *
 * @deprecated Use individual Circuit Breakers instead
 */
@Deprecated(since = "9.4")
public class CircuitBreakerManager extends CircuitBreaker {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private boolean cpuEnabled;
    private boolean memEnabled;
    private int memThreshold = 100;
    private int cpuThreshold = 100;
    private MemoryCircuitBreaker memCB;
    private CPUCircuitBreaker cpuCB;

    private final CircuitBreakerRegistry circuitBreakerRegistry = new CircuitBreakerRegistry();

    public CircuitBreakerManager() {
        super();
    }

    @Override
    public boolean isTripped() {
        return (memEnabled && memCB.isTripped()) || (cpuEnabled && cpuCB.isTripped()) || circuitBreakerRegistry.checkAnyTripped();
    }

    @Override
    public String getDebugInfo() {
        StringBuilder sb = new StringBuilder();
        if (memEnabled) {
            sb.append(memCB.getDebugInfo()).append("\n");
        }
        if (cpuEnabled) {
            sb.append(cpuCB.getDebugInfo());
        }
        sb.append(circuitBreakerRegistry.getDebugInfo());
        return sb.toString();
    }

    @Override
    public String getErrorMessage() {
        StringBuilder sb = new StringBuilder();
        if (memEnabled) {
            sb.append(memCB.getErrorMessage());
        }
        if (memEnabled && cpuEnabled) {
            sb.append("\n");
        }
        if (cpuEnabled) {
            sb.append(cpuCB.getErrorMessage());
        }
        sb.append(circuitBreakerRegistry.toErrorMessage());
        return sb.toString();
    }

    @Override
    public void init(NamedList<?> args) {
        super.init(args);
        log.warn("CircuitBreakerManager is deprecated. Use individual Circuit Breakers instead");
        if (memEnabled) {
            memCB = new MemoryCircuitBreaker();
            memCB.setThreshold(memThreshold);
        }
        if (cpuEnabled) {
            cpuCB = new CPUCircuitBreaker();
            cpuCB.setThreshold(cpuThreshold);
        }
    }

    // The methods below will be called by super class during init
    public void setMemEnabled(String enabled) {
        this.memEnabled = Boolean.getBoolean(enabled);
    }

    public void setMemThreshold(int threshold) {
        this.memThreshold = threshold;
    }

    public void setMemThreshold(String threshold) {
        this.memThreshold = Integer.parseInt(threshold);
    }

    public void setCpuEnabled(String enabled) {
        this.cpuEnabled = Boolean.getBoolean(enabled);
    }

    public void setCpuThreshold(int threshold) {
        this.cpuThreshold = threshold;
    }

    public void setCpuThreshold(String threshold) {
        this.cpuThreshold = Integer.parseInt(threshold);
    }
}

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of all registered circuit breaker instances for various request types. Responsible
 * for a holistic view of whether a circuit breaker has tripped or not.
 *
 * @lucene.experimental
 * @since 9.4
 */
public class CircuitBreakerRegistry {

    private final List<CircuitBreaker> circuitBreakerList = new ArrayList<>();

    public CircuitBreakerRegistry() {}

    public void register(CircuitBreaker circuitBreaker) {
        circuitBreakerList.add(circuitBreaker);
    }

    @VisibleForTesting
    public void deregisterAll() {
        circuitBreakerList.clear();
    }
    /**
     * Check and return circuit breakers that have triggered
     *
     * @return CircuitBreakers which have triggered, null otherwise.
     */
    public List<CircuitBreaker> checkTripped() {
        List<CircuitBreaker> triggeredCircuitBreakers = null;

        for (CircuitBreaker circuitBreaker : circuitBreakerList) {
            if (circuitBreaker.isTripped()) {
                if (triggeredCircuitBreakers == null) {
                    triggeredCircuitBreakers = new ArrayList<>();
                }

                triggeredCircuitBreakers.add(circuitBreaker);
            }
        }

        return triggeredCircuitBreakers;
    }

    /**
     * Returns true if *any* circuit breaker has triggered, false if none have triggered.
     *
     * <p>NOTE: This method short circuits the checking of circuit breakers -- the method will return
     * as soon as it finds a circuit breaker that has triggered.
     */
    public boolean checkAnyTripped() {
        for (CircuitBreaker circuitBreaker : circuitBreakerList) {
            if (circuitBreaker.isTripped()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Construct the final error message to be printed when circuit breakers trip.
     *
     * @param circuitBreakerList Input list for circuit breakers.
     * @return Constructed error message.
     */
    public static String toErrorMessage(List<CircuitBreaker> circuitBreakerList) {
        StringBuilder sb = new StringBuilder();

        for (CircuitBreaker circuitBreaker : circuitBreakerList) {
            sb.append(circuitBreaker.getErrorMessage());
            sb.append("\n");
        }

        return sb.toString();
    }

    public boolean isEnabled() {
        return !circuitBreakerList.isEmpty();
    }

    @VisibleForTesting
    public List<CircuitBreaker> getRegisteredCircuitBreakers() {
        return circuitBreakerList;
    }
}