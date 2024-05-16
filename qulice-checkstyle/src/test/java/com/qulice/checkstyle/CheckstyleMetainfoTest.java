/*
 * Copyright (c) 2011-2024 Qulice.com
 *
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
package com.qulice.checkstyle;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Test case for file metainfo validation.
 * @since 0.3
 */
@SuppressWarnings(
    {
        "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals", "PMD.GodClass"
    }
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
final class CheckstyleMetainfoTest {

    /**
     * Local Common (JavaHelper pattern) with useful utils to test checkstyle.
     */
    private Common common;

    @BeforeAll
    public void setcommon() {
        this.common = new Common();
    }

    @BeforeEach
    public void setRule() {
        this.common.updateRule();
    }

    /**
     * CheckstyleValidator can deny binary contents in java file.
     * This is test for #1264.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void rejectsJavaFileWithBinaryContent() throws Exception {
        this.common.runValidation("JavaFileWithBinaryContent.java", false);
    }
}
