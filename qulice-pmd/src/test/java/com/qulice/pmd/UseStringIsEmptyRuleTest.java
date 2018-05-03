/**
 * Copyright (c) 2011-2018, Qulice.com
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

import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link UseStringIsEmptyRule}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.18
 */
public final class UseStringIsEmptyRuleTest {

    /**
     * Error message used to inform about using public static method.
     */
    private static final String ERR_MESSAGE =
        "Use String.isEmpty() when checking for empty string";

    /**
     * UseStringIsEmptyRule can detect when String.length() is compared to 0.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthEqualsZero() throws Exception {
        new PmdAssert(
            "StringLengthEqualsZero.java", Matchers.is(false),
            Matchers.containsString(
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
            "StringFromMethodLength.java", Matchers.is(false),
            Matchers.containsString(
                UseStringIsEmptyRuleTest.ERR_MESSAGE
            )
        ).validate();
    }

    /**
     * UseStringIsEmptyRule can detect when String.length() < 1,
     * when the String is a local variable.
     * @throws Exception If something goes wrong
     */
    @Test
    public void detectsLengthLessThanOne() throws Exception {
        new PmdAssert(
            "LocalStringLength.java", Matchers.is(false),
            Matchers.containsString(
                UseStringIsEmptyRuleTest.ERR_MESSAGE
            )
        ).validate();
    }

}
