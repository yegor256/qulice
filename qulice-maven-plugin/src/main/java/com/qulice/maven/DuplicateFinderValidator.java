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
package com.qulice.maven;

import com.qulice.maven.transformer.DuplicateFinderExclude;
import com.qulice.maven.transformer.Exclude;
import com.qulice.spi.ValidationException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

/**
 * Validate with maven-duplicate-finder-plugin.
 * @since 0.5
 * @todo #1118 ignored dependencies and resources should be placed in different parameters,
 *  and current implementation use ':' symbol as a flag if it is resource or dependency.
 *  Resource can be presented as a regular expression with that symbol, can cause some problem.
 */
public final class DuplicateFinderValidator implements MavenValidator {
    /**
     * Duplicatefinder constant.
     */
    private static final String DUPLICATEFINDER = "duplicatefinder";

    @Override
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        if (!env.exclude(DuplicateFinderValidator.DUPLICATEFINDER, "")) {
            final Properties props = new Properties();
            props.put("failBuildInCaseOfConflict", "true");
            props.put("checkTestClasspath", "false");
            final Collection<Properties> ignres = new LinkedList<>();
            final Collection<Properties> deps = new LinkedList<>();
            for (final String sdep : env.excludes(DuplicateFinderValidator.DUPLICATEFINDER)) {
                final Exclude<Properties, Properties> exclude =
                    new DuplicateFinderExclude(DuplicateFinderValidator.DUPLICATEFINDER, sdep);
                final Collection<Properties> excl = exclude.dependencies();
                if (!excl.isEmpty()) {
                    deps.addAll(excl);
                }
                final Collection<Properties> res = exclude.resources();
                if (!res.isEmpty()) {
                    ignres.addAll(excl);
                }
            }
            props.put("ignoredDependencies", deps);
            props.put("ignoredResourcePatterns", ignres);
            env.executor().execute(
                "org.basepom.maven:duplicate-finder-maven-plugin:2.0.1",
                "check",
                props
            );
        }
    }
}
