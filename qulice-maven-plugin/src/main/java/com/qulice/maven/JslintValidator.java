/**
 * Copyright (c) 2011-2014, Qulice.com
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
package com.qulice.maven;

import com.qulice.spi.ValidationException;
import java.util.Properties;

/**
 * Validate with jslint-maven-plugin.
 *
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 */
public final class JslintValidator implements MavenValidator {

    /**
     * {@inheritDoc}
     * @checkstyle RedundantThrows (4 lines)
     */
    @Override
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        final Properties config = new Properties();
        config.setProperty("sourceJsFolder", "${basedir}/src/main");
        final String jslint = "jslint";
        if (!env.exclude(jslint, "")) {
            try {
                env.executor().execute(
                    "org.codehaus.mojo:jslint-maven-plugin:1.0.1",
                    jslint,
                    config
                );
            } catch (final IllegalStateException ex) {
                // @checkstyle MethodBodyCommentsCheck (1 line)
                // JsLint throws error if it can't find the sourceJsFolder
                if (!(ex.getMessage().contains("basedir")
                    && ex.getMessage().contains("does not exist"))) {
                    throw ex;
                }
            }
        }
    }

}
