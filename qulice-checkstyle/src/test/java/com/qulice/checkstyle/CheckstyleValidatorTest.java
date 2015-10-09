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

import com.google.common.base.Joiner;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import java.io.File;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for {@link CheckstyleValidator} class.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiterals (240 lines)
 */
public final class CheckstyleValidatorTest {

    /**
     * Name of property to set to change location of the license.
     */
    private static final String LICENSE_PROP = "license";

    /**
     * Directory with classes.
     */
    private static final String DIRECTORY = "src/main/java/foo";

    /**
     * License text.
     */
    private static final String LICENSE = "Hello.";

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
        final File license = this.rule.savePackageInfo(
            new File(mock.basedir(), CheckstyleValidatorTest.DIRECTORY)
        ).withLines(new String[] {"License-1.", "", "License-2."})
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
     * CheckstyleValidator can accept instance method references.
     * @throws Exception In case of error
     */
    @Test
    public void acceptsInstanceMethodReferences() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule.savePackageInfo(
            new File(mock.basedir(), CheckstyleValidatorTest.DIRECTORY)
        ).withLines(new String[] {CheckstyleValidatorTest.LICENSE})
            .withEol("\n")
            .file();
        final String content = Joiner.on("\n").join(
            "/**",
            " * Hello.",
            " */",
            "package foo;",
            "/**",
            " * Simple.",
            " * @version $Id $",
            " * @author John Smith (john@example.com)",
            " */",
            "public final class Main {",
            "    /**",
            "     * Start. Check fails in this method.",
            "     */",
            "    private void start() {",
            "        Collections.singletonList(\"1\")",
            "            .forEach(this::doSomething);",
            "    }",
            "    /**",
            "     * Method to be referenced.",
            "     * @param value Value to print",
            "     */",
            "    private void doSomething(final String value) {",
            "        System.out.println(value);",
            "    }",
            "}",
            ""
        );
        final StringWriter writer = new StringWriter();
        org.apache.log4j.Logger.getRootLogger().addAppender(
            new WriterAppender(new SimpleLayout(), writer)
        );
        final Environment env = mock.withParam(
            CheckstyleValidatorTest.LICENSE_PROP,
            this.toURL(license)
        ).withFile("src/main/java/foo/Main.java", content);
        new CheckstyleValidator().validate(env);
        MatcherAssert.assertThat(
            writer.toString(),
            Matchers.containsString("No Checkstyle violations found")
        );
    }

    /**
     * CheckstyleValidator can accept default methods with final modifiers.
     * @throws Exception In case of error
     */
    @Test
    public void acceptsDefaultMethodsWithFinalModifiers() throws Exception {
        this.validateCheckstyle(
            "DefaultMethods.java", true,
            Matchers.containsString("No Checkstyle violations found")
        );
    }

    /**
     * CheckstyleValidator can allow constructor parameters named just like
     * fields.
     * @throws Exception In case of error
     */
    @Test
    public void acceptsConstructorParametersNamedJustLikeFields()
        throws Exception {
        this.validateCheckstyle(
            "ConstructorParams.java", false,
            Matchers.allOf(
                Matchers.containsString(
                    "ConstructorParams.java[30]: 'number' hides a field."
                ),
                Matchers.not(
                    Matchers.containsString(
                        "ConstructorParams.java[21]: 'number' hides a field."
                    )
                )
            )
        );
    }

    /**
     * CheckstyleValidator will fail if  Windows EOL-s are used.
     * @throws Exception If something wrong happens inside
     */
    @Test(expected = ValidationException.class)
    public void passesWindowsEndsOfLineWithoutException() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule.savePackageInfo(
            new File(mock.basedir(), CheckstyleValidatorTest.DIRECTORY)
        ).withLines(new String[] {"Hello.", "", "World."})
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
        final File license = this.rule.savePackageInfo(
            new File(mock.basedir(), CheckstyleValidatorTest.DIRECTORY)
        ).withLines(new String[] {"Welcome.", "", "Friend."})
            .withEol("\r\n")
            .file();
        final String content =
            // @checkstyle MultipleStringLiterals (11 lines)
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

    /**
     * Validates that checkstyle reported given violation.
     * @param file File to check.
     * @param result Expected validation result.
     * @param matcher Matcher to call on checkstyle output.
     * @throws Exception In case of error
     */
    private void validateCheckstyle(final String file, final boolean result,
        final Matcher<String> matcher) throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule.savePackageInfo(
            new File(mock.basedir(), CheckstyleValidatorTest.DIRECTORY)
        ).withLines(new String[] {CheckstyleValidatorTest.LICENSE})
            .withEol("\n").file();
        final StringWriter writer = new StringWriter();
        org.apache.log4j.Logger.getRootLogger().addAppender(
            new WriterAppender(new SimpleLayout(), writer)
        );
        final Environment env = mock.withParam(
            CheckstyleValidatorTest.LICENSE_PROP,
            this.toURL(license)
        )
            .withFile(
                String.format("src/main/java/foo/%s", file),
                IOUtils.toString(
                    this.getClass().getResourceAsStream(file)
                )
            );
        boolean valid = true;
        try {
            new CheckstyleValidator().validate(env);
        } catch (final ValidationException ex) {
            valid = false;
        }
        MatcherAssert.assertThat(valid, Matchers.is(result));
        MatcherAssert.assertThat(writer.toString(), matcher);
    }
}
