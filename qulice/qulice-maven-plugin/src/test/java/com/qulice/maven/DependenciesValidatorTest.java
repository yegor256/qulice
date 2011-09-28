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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.io.FileUtils;
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
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public class DependenciesValidatorTest {

    /**
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private File folder;

    private Environment env;

    @Before
    public void prepare() throws Exception {
        this.folder = this.temp.newFolder("temp-src");
        final MavenProject project = mock(MavenProject.class);
        doReturn(new File(this.folder.getPath())).when(project).getBasedir();
        doReturn("jar").when(project).getPackaging();
        final Build build = mock(Build.class);
        doReturn(build).when(project).getBuild();
        doReturn(this.folder.getPath()).when(build).getOutputDirectory();
        this.env = new Environment();
        this.env.setProject(project);
        this.env.setLog(mock(Log.class));
        final Context context = mock(Context.class);
        this.env.setContext(context);
        final PlexusContainer container = mock(PlexusContainer.class);
        doReturn(container).when(context).get(anyString());
        final ProjectDependencyAnalyzer analyzer =
            mock(ProjectDependencyAnalyzer.class);
        doReturn(analyzer).when(container).lookup(anyString(), anyString());
        final ProjectDependencyAnalysis analysis =
            mock(ProjectDependencyAnalysis.class);
        doReturn(analysis).when(analyzer).analyze(project);
    }

    @Test
    public void testValidatesWithoutDependencyProblems() throws Exception {
        new DependenciesValidator().validate(this.env);
    }

    @Ignore
    @Test(expected = MojoFailureException.class)
    public void testValidatesWithDependencyProblems() throws Exception {
        final ProjectDependencyAnalysis analysis =
            ((ProjectDependencyAnalyzer) ((PlexusContainer)
            this.env.context().get(PlexusConstants.PLEXUS_KEY))
            .lookup(ProjectDependencyAnalyzer.ROLE, "default"))
            .analyze(this.env.project());
        final Set<Artifact> unused = new HashSet<Artifact>();
        unused.add(mock(Artifact.class));
        doReturn(unused).when(analysis).getUsedUndeclaredArtifacts();
        final Validator validator = new DependenciesValidator();
        validator.validate(this.env);
    }

    @Test
    public void testWithRuntimeScope() throws Exception {
        final ProjectDependencyAnalysis analysis =
            ((ProjectDependencyAnalyzer) ((PlexusContainer)
            this.env.context().get(PlexusConstants.PLEXUS_KEY))
            .lookup(ProjectDependencyAnalyzer.ROLE, "default"))
            .analyze(this.env.project());
        final Set<Artifact> unused = new HashSet<Artifact>();
        final Artifact artifact = mock(Artifact.class);
        unused.add(artifact);
        doReturn(unused).when(analysis).getUnusedDeclaredArtifacts();
        doReturn(Artifact.SCOPE_RUNTIME).when(artifact).getScope();
        final Validator validator = new DependenciesValidator();
        validator.validate(this.env);
    }

}
