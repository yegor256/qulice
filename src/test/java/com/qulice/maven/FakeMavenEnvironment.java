/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.context.Context;

/**
 * FakeMavenEnvironment.
 * A mock to MavenEnvironment.
 * @since 0.24.1
 */
final class FakeMavenEnvironment implements MavenEnvironment {

    /**
     * Project.
     */
    private final MavenProject proj;

    /**
     * Context.
     */
    private final Context ctx;

    /**
     * Asserts.
     */
    private final Collection<String> assrts;

    FakeMavenEnvironment(
        final MavenProject prj,
        final Context ctx,
        final Collection<String> asserts
    ) {
        this.proj = prj;
        this.ctx = ctx;
        this.assrts = asserts;
    }

    @Override
    public MavenProject project() {
        return this.proj;
    }

    @Override
    public Properties properties() {
        throw new UnsupportedOperationException("#properties()");
    }

    @Override
    public Context context() {
        return this.ctx;
    }

    @Override
    public Properties config() {
        throw new UnsupportedOperationException("#config()");
    }

    @Override
    public MojoExecutor executor() {
        throw new UnsupportedOperationException("#executor()");
    }

    @Override
    public Collection<String> asserts() {
        return this.assrts;
    }

    @Override
    public File basedir() {
        return this.proj.getBasedir();
    }

    @Override
    public File tempdir() {
        return new File("/tmp");
    }

    @Override
    public File outdir() {
        return this.proj.getBasedir();
    }

    @Override
    public Collection<File> testdirs() {
        return this.proj.getTestCompileSourceRoots().stream()
            .map(File::new)
            .toList();
    }

    @Override
    public String param(final String name, final String value) {
        return "";
    }

    @Override
    public ClassLoader classloader() {
        return ClassLoader.getSystemClassLoader();
    }

    @Override
    public Collection<String> classpath() {
        return Collections.emptyList();
    }

    @Override
    public Collection<File> files(final String pattern) {
        return Collections.emptyList();
    }

    @Override
    public boolean exclude(final String check, final String name) {
        return true;
    }

    @Override
    public Collection<String> excludes(final String checker) {
        return Collections.emptyList();
    }

    @Override
    public Charset encoding() {
        return StandardCharsets.UTF_8;
    }
}
