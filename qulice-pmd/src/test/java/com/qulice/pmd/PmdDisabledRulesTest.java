/*
 * Copyright (c) 2011-2021, Qulice.com
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

import java.util.Arrays;
import java.util.Collection;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for disabled rules.
 * @since 0.16
 */
public final class PmdDisabledRulesTest {

    @ParameterizedTest
    @MethodSource("parameters")
    public void disablesRules(final String rule) throws Exception {
        new PmdAssert(
            String.format("%s.java", rule),
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString(
                    String.format("(%s)", rule)
                )
            )
        ).validate();
    }

    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static Collection<String[]> parameters() {
        return Arrays.asList(
            new String[][] {
                {"UseConcurrentHashMap"},
                {"DoNotUseThreads"},
                {"AvoidUsingVolatile"},
                {"DefaultPackage"},
                {"ExcessiveImports"},
                {"PositionLiteralsFirstInComparisons"},
                {"MissingSerialVersionUID"},
                {"CallSuperInConstructor"},
            }
        );
    }

}
