/**
 * Copyright (c) 2011-2013, Qulice.com
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
 * Validate with maven-duplicate-finder-plugin.
 * @author Paul Polishchuk (ppol@ua.fm)
 * @version $Id$
 * @since 0.5
 * @todo #152 Maven-duplicate-finder-plugin should support exclusions.
 *  Let's add exclusions of following formats (examples):
 *  - duplicate:about.html
 *  - duplicate:org.eclipse.sisu:org.eclipse.sisu.plexus:0.0.0.M5
 *  - duplicate:org.codehaus.groovy.ast.expr.RegexExpression
 *  See https://github.com/tpc2/qulice/issues/152#issuecomment-39028953
 *  for details
 */
public final class DuplicateFinderValidator implements MavenValidator {

    /**
     * {@inheritDoc}
     * @checkstyle MultipleStringLiterals (20 lines)
     * @checkstyle RedundantThrows (4 lines)
     */
    @Override
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        if (!env.exclude("duplicatefinder", "")) {
            final Properties props = new Properties();
            props.put("failBuildInCaseOfConflict", "true");
            env.executor().execute(
                "com.ning.maven.plugins:maven-duplicate-finder-plugin:1.0.7",
                "check",
                props
            );
        }
    }

}
