/**
 * Copyright (c) 2011-2015, Qulice.com
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
import java.io.File;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link CheckstyleValidator} class.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (120 lines)
 */
public final class CheckstyleValidatorTest {

    /**
     * Name of property to set to change location of the license.
     */
    private static final String LICENSE_PROP = "license";

    /**
     * License rule.
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @Rule
    public final transient LicenseRule rule = new LicenseRule();

    /**
     * CheckstyleValidator can catch checkstyle violations.
     * @throws Exception If something wrong happens inside
     */
    @Test(expected = ValidationException.class)
    public void catchesCheckstyleViolationsInLicense() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule
            .savePackageInfo(new File(mock.basedir(), "src/main/java/foo"))
            .withLines(new String[] {"License-1.", "", "License-2."})
            .withEol("\n")
            .file();
        final String content =
            // @checkstyle StringLiteralsConcatenation (4 lines)
            // @checkstyle RegexpSingleline (1 line)
            "/**\n * License-1.\n *\n * License-2.\n */\n"
            + "package foo;\n"
            + "public class Foo { }\n";
        final Environment env = mock.withParam(
            CheckstyleValidatorTest.LICENSE_PROP,
            this.toURL(license)
        ).withFile("src/main/java/foo/Foo.java", content);
        new CheckstyleValidator().validate(env);
    }

    /**
     * CheckstyleValidator will fail if  Windows EOL-s are used.
     * @throws Exception If something wrong happens inside
     */
    @Test(expected = ValidationException.class)
    public void passesWindowsEndsOfLineWithoutException() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule
            .savePackageInfo(new File(mock.basedir(), "src/main/java/foo"))
            .withLines(new String[] {"Hello.", "", "World."})
            .withEol("\r\n")
            .file();
        final String content =
            // @checkstyle StringLiteralsConcatenation (12 lines)
            "/**\r\n"
            + " * Hello.\r\n"
            + " *\r\n"
            + " * World.\r\n"
            + " */\r\n"
            + "package foo;\r\n"
            + "/**\r\n"
            + " * Simple class.\r\n"
            + " * @version $Id $\r\n"
            + " * @author John Doe (john@qulice.com)\r\n"
            + " */\r\n"
            + "public class Main { }\r\n";
        final Environment env = mock.withParam(
            CheckstyleValidatorTest.LICENSE_PROP,
            this.toURL(license)
        ).withFile("src/main/java/foo/Main.java", content);
        new CheckstyleValidator().validate(env);
    }

    /**
     * Fail validation with Windows-style formatting of the license and
     * Linux-style formatting of the sources.
     * @throws Exception If something wrong happens inside
     */
    @Test(expected = ValidationException.class)
    public void testWindowsEndsOfLineWithLinuxSources() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule
            .savePackageInfo(new File(mock.basedir(), "src/main/java/foo"))
            .withLines(new String[] {"Welcome.", "", "Friend."})
            .withEol("\r\n")
            .file();
        final String content =
            "/**\n"
            + " * Welcome.\n"
            + " *\n"
            + " * Friend.\n"
            + " */\n"
            + "package foo;\n"
            + "/**\n"
            + " * Just a simple class.\n"
            + " * @version $Id $\n"
            + " * @author Alex Doe (alex@qulice.com)\n"
            + " */\n"
            + "public class Bar { }" + System.getProperty("line.separator");
        final Environment env = mock
            .withFile("src/main/java/foo/Bar.java", content)
            .withParam(
                CheckstyleValidatorTest.LICENSE_PROP,
                this.toURL(license)
            );
        new CheckstyleValidator().validate(env);
    }

    /**
     * Convert file name to URL.
     * @param file The file
     * @return The URL
     */
    private String toURL(final File file) {
        return String.format("file:%s", file);
    }

}
