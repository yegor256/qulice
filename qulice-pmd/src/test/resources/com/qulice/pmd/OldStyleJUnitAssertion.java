package com.qulice.pmd;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class OldStyleJUnitAssertion {

    @Test
    public void prohibitOldStyleAssertionsInTests() throws Exception {
        assertEquals("errorMessage", "expected", "actual");
    }
}
