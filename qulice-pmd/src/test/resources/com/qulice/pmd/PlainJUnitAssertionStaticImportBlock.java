package com.qulice.pmd;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;


public class PlainJUnitAssertionStaticImportBlock {

    @Test
    public void prohibitPlainJunitAssertionsInTests() throws Exception {
        assertEquals("errorMessage", "expected", "actual");
    }
}
