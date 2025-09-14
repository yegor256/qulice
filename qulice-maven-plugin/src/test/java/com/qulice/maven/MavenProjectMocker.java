/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.io.File;
import org.apache.maven.model.Build;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;

/**
 * Mocker of {@link MavenProject}.
 * @since 0.4
 */
public final class MavenProjectMocker {

    /**
     * Mock of project.
     */
    private final MavenProject project = new MavenProject();

    /**
     * In this basedir.
     * @param dir The directory
     * @return This object
     */
    public MavenProjectMocker inBasedir(final File dir) {
        final File parent = new File(dir, "target");
        final Build build = new Build();
        build.setOutputDirectory(parent.getPath());
        this.project.setFile(parent);
        this.project.setBuild(build);
        return this;
    }

    /**
     * Mock it.
     * @return The mock
     * @throws Exception If something wrong happens inside
     */
    public MavenProject mock() throws Exception {
        final Scm scm = new Scm();
        scm.setConnection("scm:svn:...");
        this.project.setPackaging("jar");
        this.project.setScm(scm);
        return this.project;
    }

}
