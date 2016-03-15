/**
 * Copyright (c) 2011-2016, Qulice.com
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

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

/**
 * PMD Validator assertions.
 * @author Krzysztof Krason (Krzysztof.Krason@gmail.com)
 * @version $Id$
 * @since 0.16
 */
final class PmdAssert {
    /**
     * File to validate.
     */
    private final transient String file;

    /**
     * Expected build status, true means success.
     */
    private final transient Matcher<Boolean> result;

    /**
     * Matcher that needs to match.
     */
    private final transient Matcher<String> matcher;

    /**
     * Constructor.
     * @param file File to validate.
     * @param result Expected build status.
     * @param matcher Matcher that needs to match.
     */
    PmdAssert(final String file, final Matcher<Boolean> result,
        final Matcher<String> matcher) {
        this.file = file;
        this.result = result;
        this.matcher = matcher;
    }

    /**
     * Validated given file against PMD.
     * @throws Exception In case of error.
     */
    public void validate() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final StringWriter writer = new StringWriter();
        final WriterAppender appender =
            new WriterAppender(new SimpleLayout(), writer);
        try {
            Logger.getRootLogger().addAppender(appender);
            final Environment env = mock.withFile(
                String.format("src/main/java/foo/%s", this.file),
                IOUtils.toString(
                    this.getClass().getResourceAsStream(this.file)
                )
            );
            boolean valid = true;
            try {
                new PmdValidator().validate(env);
            } catch (final ValidationException ex) {
                valid = false;
            }
            writer.flush();
            MatcherAssert.assertThat(valid, this.result);
            MatcherAssert.assertThat(writer.toString(), this.matcher);
        } finally {
            Logger.getRootLogger().removeAppender(appender);
        }
    }
}
