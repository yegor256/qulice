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

import com.qulice.spi.ValidationException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Validate with maven-duplicate-finder-plugin.
 * @since 0.5
 * @todo #1118 ignored dependencies and resources should be placed in different parameters,
 *  and current implementation use ':' symbol as a flag if it is resource or dependency.
 *  Resource can be presented as a regular expression with that symbol, can cause some problem.
 */
public final class DuplicateFinderValidator implements MavenValidator {

    @Override
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        final String prefix = "duplicatefinder";
        if (!env.exclude(prefix, "")) {
            final Properties props = new Properties();
            props.put("failBuildInCaseOfConflict", "true");
            props.put("checkTestClasspath", "false");
            props.put("useResultFile", "false");
            props.put(
                "ignoredResourcePatterns",
                env.excludes(prefix).stream()
                    .filter(s -> !s.contains(":"))
                    .collect(Collectors.toList())
            );
            final Collection<Properties> deps = new LinkedList<>();
            for (final String sdep : env.excludes(prefix)) {
                final String[] parts = sdep.split(":");
                if (parts.length < 2) {
                    continue;
                }
                final Properties main = new Properties();
                final Properties prop = new Properties();
                prop.put("groupId", parts[0]);
                prop.put("artifactId", parts[1]);
                if (parts.length > 2) {
                    prop.put("version", parts[2]);
                }
                main.put("dependency", prop);
                deps.add(main);
            }
            props.put("ignoredDependencies", deps);
            env.executor().execute(
                "org.basepom.maven:duplicate-finder-maven-plugin:2.0.1",
                "check",
                props
            );
        }
    }

}
