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
 * @checkstyle MultipleStringLiterals (300 lines)
 * @todo #412:30min Split this class into smaller ones and remove PMD
 *  exclude `TooManyMethods`. Good candidates for moving out of this class
 *  are all that use `validateCheckstyle` method.
 */
@SuppressWarnings("PMD.TooManyMethods")
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
     * Message that there are no violations.
     */
    private static final String NO_VIOLATIONS =
        "No Checkstyle violations found";

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
        this.validateCheckstyle(
                "InstanceMethodRef.java", true,
                Matchers.containsString(CheckstyleValidatorTest.NO_VIOLATIONS)
        );
    }

    /**
     * CheckstyleValidator can report error when parameter object is not
     * documented.
     * @throws Exception In case of error
     */
    @Test
    public void reportsErrorWhenParameterObjectIsNotDocumented()
        throws Exception {
        this.validateCheckstyle(
            "ParametrizedClass.java", false,
            Matchers.containsString(
                "Type Javadoc comment is missing an @param <T> tag."
            )
        );
    }

    /**
     * CheckstyleValidator reports an error when package decalaration
     * is line wrapped.
     * @throws Exception when error.
     */
    @Test
    public void reportsErrorWhenLineWrap()
        throws Exception {
        this.validateCheckstyle(
            "LineWrapPackage.java", false,
            Matchers.containsString("should not be line-wrapped")
        );
    }

    /**
     * CheckstyleValidator reports an error when indentation is not strict.
     * @throws Exception when error.
     */
    @Test
    public void reportsErrorWhenIndentationIsNotStrict() throws Exception {
        this.validateCheckstyle(
            "StrictIndentation.java",
            false,
            Matchers.containsString(
                "incorrect indentation level 14, expected level should be 12"
            )
        );
    }

    /**
     * CheckstyleValidator reports an error when any method contains more
     * than one return statement.
     * @throws Exception when error.
     */
    @Test
    public void reportsErrorOnMoreThanOneReturnStatement() throws Exception {
        this.validateCheckstyle(
            "ReturnCount.java", false,
            Matchers.containsString("Return count is 2 (max allowed is 1)")
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
            Matchers.containsString(CheckstyleValidatorTest.NO_VIOLATIONS)
        );
    }

    /**
     * CheckstyleValidator can accept author which has only a single name.
     * This is to support case where authro wants to use a nick instead of
     * first and last name.
     * @throws Exception In case of error
     */
    @Test
    public void acceptsSingleNameAuthor() throws Exception {
        this.validateCheckstyle(
            "AuthorTag.java", true,
            Matchers.containsString(CheckstyleValidatorTest.NO_VIOLATIONS)
        );
    }

    /**
     * CheckstyleValidator can accept constant used in method annotation.
     * @throws Exception In case of error
     * @todo #447:30min Right now ConstantUsageCheck takes into account
     *  usage of constants inside method annotations, add handling of constants
     *  used in field and class annotations.
     */
    @Test
    public void acceptsConstantUsedInMethodAnnotation() throws Exception {
        this.validateCheckstyle(
            "AnnotationConstant.java", true,
            Matchers.containsString(CheckstyleValidatorTest.NO_VIOLATIONS)
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
    @Test
    public void passesWindowsEndsOfLineWithoutException() throws Exception {
        this.validateCheckstyle(
            "WindowsEol.java", false,
            Matchers.containsString("LICENSE found")
        );
    }

    /**
     * Fail validation with Windows-style formatting of the license and
     * Linux-style formatting of the sources.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void testWindowsEndsOfLineWithLinuxSources() throws Exception {
        this.validateCheckstyle(
            "WindowsEolLinux.java", false,
            Matchers.containsString("LICENSE found")
        );
    }

    /**
     * Fail validation with extra semicolon in the end
     * of try-with-resources head.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void testExtraSemicolonInTryWithResources() throws Exception {
        this.validateCheckstyle(
            "ExtraSemicolon.java", false,
            Matchers.containsString("Only one statement per line allowed.")
        );
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
