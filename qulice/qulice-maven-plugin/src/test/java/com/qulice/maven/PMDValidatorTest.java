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
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public class PMDValidatorTest {

    /**
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private File folder;

    private MavenProject project;

    @Before
    public void prepareValidator() throws Exception {
        this.folder = this.temp.newFolder("temp-src");
        this.project = mock(MavenProject.class);
        doReturn(new File(this.folder.getPath())).when(project).getBasedir();
        final Build build = mock(Build.class);
        doReturn(build).when(project).getBuild();
        final List<String> paths = new ArrayList<String>();
        paths.add(this.folder.getPath());
        doReturn(paths).when(project).getTestClasspathElements();
        doReturn(paths).when(project).getRuntimeClasspathElements();
        doReturn(this.folder.getPath()).when(build).getOutputDirectory();
        doReturn(this.folder.getPath()).when(build).getTestOutputDirectory();
    }

    @Ignore
    @Test(expected = MojoFailureException.class)
    public void testValidatesSetOfFiles() throws Exception {
        final Properties config = new Properties();
        final Log log = mock(Log.class);
        final Validator validator = new PMDValidator(this.project, log, config);
        final File java = new File(this.folder, "Main.java");
        FileUtils.writeStringToFile(java, "class Main { int x = 0; }");
        validator.validate();
    }

}
