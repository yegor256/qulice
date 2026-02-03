/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
