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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Test case for {@link CheckstyleValidator} class.
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class CheckstyleValidatorTest {

    /**
     * Name of property to set to change location of the license.
     */
    private static final String LICENSE_PROP = "license";

    /**
     * Temporary folder, set by JUnit framework automatically.
     * @checkstyle VisibilityModifier (3 lines)
     */
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    /**
     * The folder where to test.
     * @see #prepare()
     */
    private File folder;

    /**
     * The environment.
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
     * Prepare the folder and environment for testing.
     * @throws Exception If something wrong happens inside
     */
    @Before
    public void prepare() throws Exception {
        this.folder = this.temp.newFolder("temp-src");
        final MavenProject project = Mockito.mock(MavenProject.class);
        Mockito.doReturn(new File(this.folder.getPath()))
            .when(project).getBasedir();
        final Build build = Mockito.mock(Build.class);
        Mockito.doReturn(build).when(project).getBuild();
        final List<String> paths = new ArrayList<String>();
        paths.add(this.folder.getPath());
        Mockito.doReturn(paths).when(project).getTestClasspathElements();
        Mockito.doReturn(paths).when(project).getRuntimeClasspathElements();
        Mockito.doReturn(this.folder.getPath())
            .when(build).getOutputDirectory();
        Mockito.doReturn(this.folder.getPath())
            .when(build).getTestOutputDirectory();
        this.env = new Environment();
        this.env.setProject(project);
    }

    /**
     * Validate set of files with error inside.
     * @throws Exception If something wrong happens inside
     */
    @Test(expected = MojoFailureException.class)
    public void testValidatesSetOfFiles() throws Exception {
        final Properties config = new Properties();
        final File license = this.temp.newFile("license.txt");
        FileUtils.writeStringToFile(license, "license\n");
        config.setProperty(this.LICENSE_PROP, "file:" + license.getPath());
        final Log log = Mockito.mock(Log.class);
        final Validator validator = new CheckstyleValidator();
        final File java = new File(this.folder, "src/main/java/Main.java");
        java.getParentFile().mkdirs();
        FileUtils.writeStringToFile(java, "public class Main { }");
        validator.validate(this.env);
    }

    /**
     * Immidate the license inside the classpath (validator has to find it).
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void testImmitatesLicenseInClasspath() throws Exception {
        final File license = new File(this.folder, "my-license.txt");
        FileUtils.writeStringToFile(license, "some non-important text\n");
        final Properties config = new Properties();
        config.setProperty(this.LICENSE_PROP, license.getName());
        final Log log = Mockito.mock(Log.class);
        final Validator validator = new CheckstyleValidator();
        validator.validate(this.env);
    }

}
