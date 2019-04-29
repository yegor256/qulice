/*
 * Copyright (c) 2011-2019, Qulice.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.pmd;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link com.qulice.pmd.rules.UseStringIsEmptyRule}.
 * @since 0.18
 * @todo #1000:30min UseStringIsEmptyRuleTest is not comparing all possible
 *  targets. Implement the tests for the remaining targets (see
 *  complete list in UseStringIsEmptyRule#getComparisonTargets). Conditions
 *  that must be tested are checking string length when the String is the
 *  result of a method (this.method().length()) and when the String is a class
 *  field (this.somestring.length()). Use the same name and file pattern used
 *  in the test methods, appending 'This' or 'Method' suffix to indicate
 *  which access type the test relates to (field or method), renaming the
 *  tests if needed.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class UseStringIsEmptyRuleTest {

    /**
     * Error message used to inform about using public static method.
     */
    private static final String ERR_MESSAGE =
        "Use String.isEmpty() when checking for empty string";

    /**
     * UseStringIsEmptyRule can detect when String.length() < 1,
     * when the String is a local variable.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthLessThanOne() throws Exception {
        new PmdAssert(
            "StringLengthLessThanOne.java", new IsEqual<>(false),
            new StringContains(
                UseStringIsEmptyRuleTest.ERR_MESSAGE
            )
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when String.length() > 0,
     * when the String is a local variable.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthGreaterThanZero() throws Exception {
        new PmdAssert(
            "StringLengthGreaterThanZero.java", new IsEqual<>(false),
            new StringContains(
                UseStringIsEmptyRuleTest.ERR_MESSAGE
            )
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when String.length() is compared to 0.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthEqualsZero() throws Exception {
        new PmdAssert(
            "StringLengthEqualsZero.java", new IsEqual<>(false),
            new StringContains(
                UseStringIsEmptyRuleTest.ERR_MESSAGE
            )
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when String.length() != 1,
     * when the String is a local variable.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthNotEqualsZero() throws Exception {
        new PmdAssert(
            "StringLengthNotEqualsZero.java", new IsEqual<>(false),
            new StringContains(
                UseStringIsEmptyRuleTest.ERR_MESSAGE
            )
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when String.length() >= 0,
     * when the String is returned by a method.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthGreaterOrEqualZero() throws Exception {
        new PmdAssert(
            "StringLengthGreaterOrEqualZero.java", new IsEqual<>(false),
            new StringContains(
                UseStringIsEmptyRuleTest.ERR_MESSAGE
            )
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when String.length() >= 1,
     * when the String is returned by a method.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthGreaterOrEqualOne() throws Exception {
        new PmdAssert(
            "StringLengthGreaterOrEqualOne.java", new IsEqual<>(false),
            new StringContains(
                UseStringIsEmptyRuleTest.ERR_MESSAGE
            )
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when String.length() <= 0,
     * when the String is a local variable.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthLessOrEqualZero() throws Exception {
        new PmdAssert(
            "StringLengthLessOrEqualZero.java", new IsEqual<>(false),
            new StringContains(
                UseStringIsEmptyRuleTest.ERR_MESSAGE
            )
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when this.String.length() is compared to
     * 0.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthEqualsZeroThis() throws Exception {
        new PmdAssert(
            "StringLengthEqualsZeroThis.java", new IsEqual<>(false),
            new StringContains(UseStringIsEmptyRuleTest.ERR_MESSAGE)
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when this.String.length() >= 1.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthGreaterOrEqualOneThis() throws Exception {
        new PmdAssert(
            "StringLengthGreaterOrEqualOneThis.java", new IsEqual<>(false),
            new StringContains(UseStringIsEmptyRuleTest.ERR_MESSAGE)
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when this.String.length() < 1.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthLessThanOneThis() throws Exception {
        new PmdAssert(
            "StringLengthLessThanOneThis.java", new IsEqual<>(false),
            new StringContains(UseStringIsEmptyRuleTest.ERR_MESSAGE)
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when this.String.length() is compared to
     * 0, when String is returned by a method.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthEqualsZeroMethod() throws Exception {
        new PmdAssert(
            "StringLengthEqualsZeroMethod.java", new IsEqual<>(false),
            new StringContains(UseStringIsEmptyRuleTest.ERR_MESSAGE)
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when this.String.length() >= 1, when
     * String is returned by a method.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthGreaterOrEqualOneMethod() throws Exception {
        new PmdAssert(
            "StringLengthGreaterOrEqualOneMethod.java", new IsEqual<>(false),
            new StringContains(UseStringIsEmptyRuleTest.ERR_MESSAGE)
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when this.String.length() < 1, when
     * String is returned by a method.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthLessThanOneMethod() throws Exception {
        new PmdAssert(
            "StringLengthLessThanOneMethod.java", new IsEqual<>(false),
            new StringContains(UseStringIsEmptyRuleTest.ERR_MESSAGE)
        ).validate();
    }
}
