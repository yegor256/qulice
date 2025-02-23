/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Properties;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.context.Context;

/**
 * Environment, passed from MOJO to validators.
 *
 * @since 0.3
 */
@SuppressWarnings("PMD.TooManyMethods")
interface MavenEnvironment extends Environment {

    /**
     * Get project.
     * @return The project
     */
    MavenProject project();

    /**
     * Get properties.
     * @return The properties
     */
    Properties properties();

    /**
     * Get context.
     * @return The context
     */
    Context context();

    /**
     * Get plugin configuration properties.
     * @return The props
     */
    Properties config();

    /**
     * Get MOJO executor.
     * @return The executor
     */
    MojoExecutor executor();

    /**
     * Get xpath queries for pom.xml validation.
     * @return The asserts
     */
    Collection<String> asserts();

    /**
     * Wrapper of maven environment.
     *
     * @since 0.1
     */
    final class Wrap implements MavenEnvironment {
        /**
         * Parent environment.
         */
        private final Environment env;

        /**
         * Parent maven environment.
         */
        private final MavenEnvironment menv;

        /**
         * Public ctor.
         * @param penv Parent env
         * @param pmenv Parent maven env
         */
        Wrap(final Environment penv,
            final MavenEnvironment pmenv) {
            this.env = penv;
            this.menv = pmenv;
        }

        @Override
        public File basedir() {
            return this.env.basedir();
        }

        @Override
        public File tempdir() {
            return this.env.tempdir();
        }

        @Override
        public File outdir() {
            return this.env.outdir();
        }

        @Override
        public String param(final String name, final String value) {
            return this.env.param(name, value);
        }

        @Override
        public ClassLoader classloader() {
            return this.env.classloader();
        }

        @Override
        public Collection<String> classpath() {
            return this.env.classpath();
        }

        @Override
        public MavenProject project() {
            return this.menv.project();
        }

        @Override
        public Properties properties() {
            return this.menv.properties();
        }

        @Override
        public Context context() {
            return this.menv.context();
        }

        @Override
        public Properties config() {
            return this.menv.config();
        }

        @Override
        public MojoExecutor executor() {
            return this.menv.executor();
        }

        @Override
        public Collection<String> asserts() {
            return this.menv.asserts();
        }

        @Override
        public Collection<File> files(final String pattern) {
            return this.env.files(pattern);
        }

        @Override
        public boolean exclude(final String check, final String name) {
            return this.env.exclude(check, name);
        }

        @Override
        public Collection<String> excludes(final String checker) {
            return this.env.excludes(checker);
        }

        @Override
        public Charset encoding() {
            return this.env.encoding();
        }
    }
}
