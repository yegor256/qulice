package com.qulice.pmd;

import org.junit.Assert;
import org.junit.Test;

public class OldStyleJUnitAssertionTestMethod {

    @Test
    public void prohibitOldStyleAssertionsInTests() throws Exception {
        Assert.assertEquals("errorMessage", "expected", "actual");
    }
}
