/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
