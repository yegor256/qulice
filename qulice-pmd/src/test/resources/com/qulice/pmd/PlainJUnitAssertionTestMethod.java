package com.qulice.pmd;

import org.junit.Assert;
import org.junit.Test;

public class PlainJUnitAssertionTestMethod {

    @Test
    public void prohibitPlainJunitAssertionsInTests() throws Exception {
        Assert.assertEquals("errorMessage", "expected", "actual");
    }
}
