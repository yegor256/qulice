/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.jcabi.log.Logger;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MavenPluginManager;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Abstract mojo.
 *
 * @since 0.3
 */
public abstract class AbstractQuliceMojo extends AbstractMojo
    implements Contextualizable {

    /**
     * Environment to pass to validators.
     */
    private final DefaultMavenEnvironment environment =
        new DefaultMavenEnvironment();

    /**
     * Maven project, to be injected by Maven itself.
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    /**
     * Maven session, to be injected by Maven itself.
     */
    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession sess;

    /**
     * Maven plugin manager, to be injected by Maven itself.
     */
    @Component
    private MavenPluginManager manager;

    /**
     * Shall we skip execution?
     */
    @Parameter(property = "qulice.skip", defaultValue = "false")
    private boolean skip;

    /**
     * List of regular expressions to exclude.
     */
    @Parameter(property = "qulice.excludes")
    private final Collection<String> excludes = new LinkedList<>();

    /**
     * List of xpath queries to validate pom.xml.
     * @checkstyle IndentationCheck (5 lines)
     */
    @Parameter(
        property = "qulice.asserts",
        required = false
    )
    private final Collection<String> asserts = new LinkedList<>();

    /**
     * The source encoding.
     *
     * @parameter expression="${project.build.sourceEncoding}" required="true"
     */
    @Parameter(property = "encoding", defaultValue = "${project.build.sourceEncoding}")
    private String charset;

    /**
     * Set Maven Project (used mostly for unit testing).
     * @param proj The project to set
     */
    public final void setProject(final MavenProject proj) {
        this.project = proj;
    }

    /**
     * Set skip option (mostly for unit testing).
     * @param skp The "skip" option
     */
    public final void setSkip(final boolean skp) {
        this.skip = skp;
    }

    /**
     * Set asserts option.
     * @param newAsserts Asserts to use.
     */
    public final void setAsserts(final Collection<String> newAsserts) {
        this.asserts.clear();
        this.asserts.addAll(newAsserts);
    }

    /**
     * Set excludes.
     * @param exprs Expressions
     */
    public final void setExcludes(final Collection<String> exprs) {
        this.excludes.clear();
        this.excludes.addAll(exprs);
    }

    /**
     * Set source code encoding.
     * @param encoding Source code encoding
     */
    public void setEncoding(final String encoding) {
        this.charset = encoding;
    }

    @Override
    public final void contextualize(final Context ctx) {
        this.environment.setContext(ctx);
    }

    @Override
    public final void execute() throws MojoFailureException {
        StaticLoggerBinder.getSingleton().setMavenLog(this.getLog());
        if (this.skip) {
            this.getLog().info("Execution skipped");
            return;
        }
        this.environment.setProject(this.project);
        this.environment.setMojoExecutor(
            new MojoExecutor(this.manager, this.sess)
        );
        this.environment.setExcludes(this.excludes);
        this.environment.setAssertion(this.asserts);
        this.environment.setEncoding(this.charset);
        final long start = System.nanoTime();
        this.doExecute();
        Logger.info(
            this,
            "Qulice quality check completed in %[nano]s",
            System.nanoTime() - start
        );
    }

    /**
     * Current maven session.
     * @return Current maven session
     */
    public final MavenSession session() {
        return this.sess;
    }

    /**
     * Do the real execution.
     * @throws MojoFailureException If some failure inside
     * @checkstyle NonStaticMethod (2 lines)
     */
    protected abstract void doExecute() throws MojoFailureException;

    /**
     * Get the environment.
     * @return The environment
     */
    protected final MavenEnvironment env() {
        return this.environment;
    }

}
