/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.io.File;
import org.apache.maven.model.Build;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.mockito.Mockito;

/**
 * Mocker of {@link MavenProject}.
 * @since 0.4
 */
public final class MavenProjectMocker {

    /**
     * Mock of project.
     */
    private final MavenProject project =
        Mockito.mock(MavenProject.class);

    /**
     * In this basedir.
     * @param dir The directory
     * @return This object
     */
    public MavenProjectMocker inBasedir(final File dir) {
        Mockito.doReturn(dir).when(this.project).getBasedir();
        final Build build = Mockito.mock(Build.class);
        Mockito.doReturn(build).when(this.project).getBuild();
        Mockito.doReturn(new File(dir, "target").getPath())
            .when(build).getOutputDirectory();
        return this;
    }

    /**
     * Mock it.
     * @return The mock
     * @throws Exception If something wrong happens inside
     */
    public MavenProject mock() throws Exception {
        Mockito.doReturn("jar").when(this.project).getPackaging();
        final Scm scm = new Scm();
        scm.setConnection("scm:svn:...");
        Mockito.doReturn(scm).when(this.project).getScm();
        return this.project;
    }

}
