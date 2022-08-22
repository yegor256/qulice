package com.qulice.pmd;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AllowAssertFail {

    @Test
    void prohibitPlainJunitAssertionsInTests() throws Exception {
        Matchers.assertThat("errorMessage", "expected", Matchers.is("actual"));
        Assertions.fail("fail test");
    }
}
