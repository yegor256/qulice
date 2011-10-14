/**
 * Copyright (c) 2011, Qulice.com
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

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalysis;
import org.apache.maven.shared.dependency.analyzer.ProjectDependencyAnalyzer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Test case for {@link DependenciesValidator} class.
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class DependenciesValidatorTest {

    /**
     * Temporary folder, set by JUnit framework automatically.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    /**
     * The folder to work in.
     * @see #prepare()
     */
    private File folder;

    /**
     * The environment to work with.
     * @see #prepare()
     */
    private Environment env;

    /**
     * Forward SLF4J to Maven Log.
     * @throws Exception If something is wrong inside
     */
    @BeforeClass
    public static void initLogging() throws Exception {
        final Log log = Mockito.mock(Log.class);
        StaticLoggerBinder.getSingleton().setMavenLog(log);
    }

    /**
     * Prepare the folder and the environment.
     * @throws Exception If something wrong happens inside
     */
    @Before
    public void prepare() throws Exception {
        this.folder = this.temp.newFolder("temp-src");
        final MavenProject project = Mockito.mock(MavenProject.class);
        Mockito.doReturn(new File(this.folder.getPath()))
            .when(project).getBasedir();
        Mockito.doReturn("jar").when(project).getPackaging();
        final Build build = Mockito.mock(Build.class);
        Mockito.doReturn(build).when(project).getBuild();
        Mockito.doReturn(this.folder.getPath()).when(build)
            .getOutputDirectory();
        this.env = new Environment();
        this.env.setProject(project);
        final Context context = Mockito.mock(Context.class);
        this.env.setContext(context);
        final PlexusContainer container = Mockito.mock(PlexusContainer.class);
        Mockito.doReturn(container).when(context).get(Mockito.anyString());
        final ProjectDependencyAnalyzer analyzer =
            Mockito.mock(ProjectDependencyAnalyzer.class);
        Mockito.doReturn(analyzer).when(container)
            .lookup(Mockito.anyString(), Mockito.anyString());
        final ProjectDependencyAnalysis analysis =
            Mockito.mock(ProjectDependencyAnalysis.class);
        Mockito.doReturn(analysis).when(analyzer).analyze(project);
    }

    /**
     * Test without any dependency problems.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void testValidatesWithoutDependencyProblems() throws Exception {
        new DependenciesValidator().validate(this.env);
    }

    /**
     * We should find and identify dependency problems.
     * @throws Exception If something wrong happens inside
     */
    @Test(expected = MojoFailureException.class)
    public void testValidatesWithDependencyProblems() throws Exception {
        final ProjectDependencyAnalysis analysis = this.analysis();
        final Set<Artifact> unused = new HashSet<Artifact>();
        unused.add(Mockito.mock(Artifact.class));
        Mockito.doReturn(unused).when(analysis).getUsedUndeclaredArtifacts();
        final Validator validator = new DependenciesValidator();
        validator.validate(this.env);
    }

    /**
     * Dependencies in runtime scope should be ignored.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void testWithRuntimeScope() throws Exception {
        final ProjectDependencyAnalysis analysis = this.analysis();
        final Set<Artifact> unused = new HashSet<Artifact>();
        final Artifact artifact = Mockito.mock(Artifact.class);
        unused.add(artifact);
        Mockito.doReturn(unused).when(analysis).getUnusedDeclaredArtifacts();
        Mockito.doReturn(Artifact.SCOPE_RUNTIME).when(artifact).getScope();
        final Validator validator = new DependenciesValidator();
        validator.validate(this.env);
    }

    /**
     * Create "analysis" object.
     * @return The object
     * @throws Exception If something wrong happens inside
     */
    private ProjectDependencyAnalysis analysis() throws Exception {
        return
            ((ProjectDependencyAnalyzer)
                ((PlexusContainer)
                    this.env.context().get(PlexusConstants.PLEXUS_KEY)
                ).lookup(ProjectDependencyAnalyzer.ROLE, "default")
            ).analyze(this.env.project());
    }

}
