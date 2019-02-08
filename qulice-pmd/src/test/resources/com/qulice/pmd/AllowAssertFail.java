package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AllowAssertFail {

    @Test
    public void prohibitPlainJunitAssertionsInTests() throws Exception {
        Matchers.assertThat("errorMessage", "expected", Matchers.is("actual"));
        Assertions.fail("fail test");
    }
}
