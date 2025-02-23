/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.jcabi.log.Logger;
import com.qulice.spi.ValidationException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Extension;
import org.apache.maven.model.Plugin;

/**
 * Check that the project has not SNAPSHOT dependencies if its own
 * status is stable.
 *
 * @since 0.3
 */
public final class SnapshotsValidator implements MavenValidator {

    @Override
    public void validate(final MavenEnvironment env)
        throws ValidationException {
        if (!env.exclude("snapshots", "")) {
            final String version = env.project().getVersion();
            if (!SnapshotsValidator.isSnapshot(version)) {
                this.check(env);
            }
        }
    }

    /**
     * Check all plugins and deps.
     * @param env Environment
     * @throws ValidationException If fails
     */
    private void check(final MavenEnvironment env) throws ValidationException {
        int errors = 0;
        for (final Extension ext : env.project().getBuildExtensions()) {
            if (SnapshotsValidator.isSnapshot(ext.getVersion())) {
                Logger.warn(
                    this,
                    "%s build extension is SNAPSHOT",
                    ext
                );
                ++errors;
            }
        }
        for (final Plugin plugin : env.project().getBuildPlugins()) {
            if (SnapshotsValidator.isSnapshot(plugin.getVersion())) {
                Logger.warn(
                    this,
                    "%s build plugin is SNAPSHOT",
                    plugin
                );
                ++errors;
            }
        }
        for (final Dependency dep : env.project().getDependencies()) {
            if (SnapshotsValidator.isSnapshot(dep.getVersion())) {
                Logger.warn(
                    this,
                    "%s dependency is SNAPSHOT",
                    dep
                );
                ++errors;
            }
        }
        if (errors > 0) {
            Logger.warn(
                this,
                "The version of the project is not SNAPSHOT; there shouldn't not be any SNAPSHOT dependencies (%d found)",
                errors
            );
            throw new ValidationException(
                "%d dependencies are in SNAPSHOT state",
                errors
            );
        }
    }

    /**
     * Whether this version is a snapshot?
     * @param version The version
     * @return TRUE if yes
     */
    private static boolean isSnapshot(final String version) {
        return version.endsWith("-SNAPSHOT");
    }

}
