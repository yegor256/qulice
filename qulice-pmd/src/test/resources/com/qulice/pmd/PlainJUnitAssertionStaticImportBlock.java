package com.qulice.pmd;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PlainJUnitAssertionStaticImportBlock {

    @Test
    public void prohibitPlainJunitAssertionsInTests() throws Exception {
        assertEquals("errorMessage", "expected", "actual");
    }
}
