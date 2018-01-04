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
package com.qulice.maven;

import com.qulice.spi.ValidationException;
import java.util.Properties;

/**
 * Validates with Cobertura.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.3
 */
public final class CoberturaValidator implements MavenValidator {

    // @checkstyle MultipleStringLiterals (30 lines)
    @Override
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        if (!env.exclude("cobertura", "")) {
            final Properties props = new Properties();
            props.put("quiet", "false");
            final Properties check = new Properties();
            props.put("check", check);
            check.put("haltOnFailure", "true");
            check.put("lineRate", "60");
            check.put("branchRate", "60");
            check.put("packageLineRate", "70");
            check.put("packageBranchRate", "70");
            check.put("totalLineRate", "80");
            check.put("totalBranchRate", "80");
            env.executor().execute(
                "org.codehaus.mojo:cobertura-maven-plugin:2.5.1",
                "check",
                props
            );
        }
    }

}
