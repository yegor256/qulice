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
package com.qulice.checkstyle;

import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

/**
 * Test case for {@link CheckstyleValidator} class.
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (300 lines)
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
     * The file where to save java code.
     * @see #prepare()
     */
    private File java;

    /**
     * The environment.
     * @see #prepare()
     */
    private Environment env;

    /**
     * Prepare the folder and environment for testing.
     * @throws Exception If something wrong happens inside
     */
    @Before
    public void prepare() throws Exception {
        final File folder = this.temp.newFolder("temp-src");
        this.env = Mockito.mock(Environment.class);
        Mockito.doReturn(folder).when(this.env).basedir();
        Mockito.doReturn(folder).when(this.env).tempdir();
        this.java = new File(folder, "src/main/java/com/qulice/foo/Main.java");
        this.java.getParentFile().mkdirs();
    }

    /**
     * Validate set of files with error inside.
     * @throws Exception If something wrong happens inside
     */
    @Test(expected = ValidationException.class)
    public void testValidatesSetOfFiles() throws Exception {
        final File license = this.build("The license.", "\n");
        Mockito.doReturn(this.toURL(license)).when(this.env)
            .param(Mockito.eq(this.LICENSE_PROP), Mockito.any(String.class));
        final Validator validator = new CheckstyleValidator();
        FileUtils.writeStringToFile(
            this.java,
            // @checkstyle RegexpSingleline (1 line)
            "/**\n * The license.\n *\n * The license.\n */\n"
            + "package com.qulice.foo;\n"
            + "public class Main { }\n"
        );
        validator.validate(this.env);
    }

    /**
     * Immidate the license inside the classpath (validator has to find it).
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void testImmitatesLicenseInClasspath() throws Exception {
        // not implemented yet
    }

    /**
     * Validate with Windows-style formatting.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void testWindowsEndsOfLine() throws Exception {
        final File license = this.build("Line 1.", "\r\n");
        Mockito.doReturn(this.toURL(license)).when(this.env)
            .param(Mockito.eq(this.LICENSE_PROP), Mockito.any(String.class));
        final Validator validator = new CheckstyleValidator();
        FileUtils.writeStringToFile(
            this.java,
            "/**\r\n"
            + " * Line 1.\r\n"
            + " *\r\n"
            + " * Line 1.\r\n"
            + " */\r\n"
            + "package com.qulice.foo;\r\n"
            + "/**\r\n"
            + " * Simple class.\r\n"
            + " * @version $Id $\r\n"
            + " * @author John Doe (john@qulice.com)\r\n"
            + " */\r\n"
            + "public class Main { }\r\n"
        );
        validator.validate(this.env);
    }

    /**
     * Validate with Windows-style formatting of the license and Linux-style
     * formatting of the sources.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void testWindowsEndsOfLineWithLinuxSources() throws Exception {
        final File license = this.build("Line 2.", "\r\n");
        Mockito.doReturn(this.toURL(license)).when(this.env)
            .param(Mockito.eq(this.LICENSE_PROP), Mockito.any(String.class));
        final Validator validator = new CheckstyleValidator();
        FileUtils.writeStringToFile(
            this.java,
            "/**\n"
            + " * Line 2.\n"
            + " *\n"
            + " * Line 2.\n"
            + " */\n"
            + "package com.qulice.foo;\n"
            + "/**\n"
            + " * Just a simple class.\n"
            + " * @version $Id $\n"
            + " * @author Alex Doe (alex@qulice.com)\n"
            + " */\n"
            + "public class Main { }\n"
        );
        validator.validate(this.env);
    }

    /**
     * Convert file name to URL.
     * @param file The file
     * @return The URL
     */
    private String toURL(final File file) {
        return String.format("file:%s", file);
    }

    /**
     * Create license and package-info.java close to the java file.
     * @param line The line to put into the license
     * @param eol What to use as end-of-line character
     * @return The location of LICENSE.txt
     * @throws Exception If something wrong happens inside
     */
    private File build(final String line, final String eol) throws Exception {
        final File license = this.temp.newFile("LICENSE.txt");
        FileUtils.writeStringToFile(
            license,
            String.format("%s%s%s%s", line, eol, eol, line)
        );
        final File info = new File(
            this.java.getParentFile(), "package-info.java"
        );
        final String header = String.format(
            // @checkstyle RegexpSingleline (1 line)
            "/**%s * %s%s *%s * %s%s */",
            eol, line, eol, eol, line, eol, eol
        );
        FileUtils.writeStringToFile(
            info,
            String.format(
                // @checkstyle LineLength (2 lines)
                // @checkstyle RegexpSingleline (1 line)
                "%s%s/**%s * Hm...%s@version $Id $%s * @author John Doe (j@qulice.com)%s */%spackage com.qulice.foo;%s",
                header, eol, eol, eol, eol, eol, eol, eol
            )
        );
        return license;
    }

}
