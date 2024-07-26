package org.apache.commons.lang3;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.AfterEach;

/**
 * All tests subclass this test.
 */
public class AbstractLangTest {

    /**
     * All tests should leave the {@link ToStringStyle} registry empty.
     */
    @AfterEach
    public void after() {
        validateNullToStringStyleRegistry();
    }

    void validateNullToStringStyleRegistry() {
        assertNull(ToStringStyle.getRegistry(), "Expected null, actual: " + ToStringStyle.getRegistry());
    }

}