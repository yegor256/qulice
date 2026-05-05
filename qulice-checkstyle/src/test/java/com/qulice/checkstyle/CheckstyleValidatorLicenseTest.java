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

import com.google.common.base.Joiner;
import com.qulice.checkstyle.test.extensions.FileToUrl;
import com.qulice.checkstyle.test.extensions.RunnerConstant;
import com.qulice.checkstyle.test.extensions.ViolationMatcher;
import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for license.
 *
 * @since 0.3
 */
final class CheckstyleValidatorLicenseTest {

    /**
     * Rule for testing.
     */
    private License rule;

    @BeforeEach
    void setRule() {
        this.rule = new License();
    }

    /**
     * CheckstyleValidator can catch checkstyle violations.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void catchesCheckstyleViolationsInLicense() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule.savePackageInfo(
            new File(mock.basedir(), RunnerConstant.DIRECTORY.value())
        ).withLines("License-1.", "", "License-2.")
            .withEol("\n")
            .file();
        final String content =
            // @checkstyle StringLiteralsConcatenation (4 lines)
            "/" + "*\n * License-3.\n *\n * License-2.\n */\n"
                + "package foo;\n"
                + "public class Foo { }\n";
        final String name = "Foo.java";
        final Environment env = mock.withParam(
            RunnerConstant.LICENSE_PROP.value(),
            new FileToUrl(license).toUrl()
        ).withFile(String.format("src/main/java/foo/%s", name), content);
        final Collection<Violation> results =
            new CheckstyleValidator(env)
                .validate(env.files(name));
        MatcherAssert.assertThat(
            "Header validation is expected",
            results,
            Matchers.hasItem(
                new ViolationMatcher(
                    "Line does not match expected header line of", name
                )
            )
        );
    }

    /**
     * CheckstyleValidator does not produce errors when last thing
     * in file are imports. The only exception that should be thrown is
     * qulice ValidationException.
     * @throws Exception In case of error.
     */
    @Test
    void doesNotThrowExceptionIfImportsOnly() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule.savePackageInfo(
            new File(mock.basedir(), RunnerConstant.DIRECTORY.value())
        ).withLines("License-1.", "", "License-2.")
            .withEol("\n")
            .file();
        final String crlf = "\r\n";
        final String content = Joiner.on(crlf).join(
            "package com.google;",
            crlf,
            "import java.util.*;"
        );
        final String name = "Foo.java";
        final Environment env = mock.withParam(
            RunnerConstant.LICENSE_PROP.value(),
            new FileToUrl(license).toUrl()
        ).withFile(String.format("src/main/java/foo/%s", name), content);
        final Collection<Violation> results =
            new CheckstyleValidator(env).validate(env.files(name));
        MatcherAssert.assertThat(
            "Validation error should exist",
            results,
            Matchers.not(Matchers.<Violation>empty())
        );
    }
}
