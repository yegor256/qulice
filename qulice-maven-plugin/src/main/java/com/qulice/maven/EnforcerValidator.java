/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.ValidationException;
import java.util.Properties;

/**
 * Validate with maven-enforcer-plugin.
 *
 * @since 0.3
 */
public final class EnforcerValidator implements MavenValidator {

    @Override
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        if (!env.exclude("enforcer", "")) {
            final Properties props = new Properties();
            final Properties rules = new Properties();
            props.put("rules", rules);
            final Properties maven = new Properties();
            rules.put("requireMavenVersion", maven);
            maven.put("version", "3.0");
            final Properties java = new Properties();
            rules.put("requireJavaVersion", java);
            java.put("version", "1.7");
            env.executor().execute(
                "org.apache.maven.plugins:maven-enforcer-plugin:3.1.0",
                "enforce",
                props
            );
        }
    }

}
