package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

public class AllowAssertFail {

    @Test
    public void prohibitOldStyleAssertionsInTests() throws Exception {
        Matchers.assertThat("errorMessage", "expected", Matchers.is("actual"));
        Assert.fail("fail test");
    }
}
