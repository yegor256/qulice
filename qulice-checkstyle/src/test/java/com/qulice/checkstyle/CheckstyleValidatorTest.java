/**
 * Copyright (c) 2011-2016, Qulice.com
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
import com.jcabi.aspects.Tv;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
 * @since 0.3
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
     * CheckstyleValidator reports an error when indentation is not
     * bigger than previous line by exactly 4.
     * @throws Exception when error.
     */
    @Test
    public void reportsErrorWhenIndentationIsIncorrect() throws Exception {
        this.validateCheckstyle(
            "InvalidIndentation.java",
            false,
            Matchers.containsString(
                "Indentation (14) must be same or less than"
            )
        );
    }

    /**
     * CheckstyleValidator reports an error when comment or Javadoc has too
     * long line.
     * @throws Exception when error.
     */
    @Test
    public void reportsErrorWhenCommentOrJavadocIsTooLong() throws Exception {
        this.validateCheckstyle(
            "TooLongLines.java",
            false,
            Matchers.stringContainsInOrder(
                Arrays.asList(
                    "TooLongLines.java[8]",
                    "Line is longer than 80 characters (found 82)",
                    "TooLongLines.java[14]",
                    "Line is longer than 80 characters (found 85)"
                )
            )
        );
    }

    /**
     * CheckstyleValidator accepts the valid indentation
     * refused by forceStrictCondition.
     * @throws Exception when error.
     */
    @Test
    public void acceptsValidIndentation() throws Exception {
        this.validateCheckstyle(
            "ValidIndentation.java",
            true,
            Matchers.containsString(CheckstyleValidatorTest.NO_VIOLATIONS)
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
                    "ConstructorParams.java[31]: 'number' hides a field."
                ),
                Matchers.not(
                    Matchers.containsString(
                        "ConstructorParams.java[22]: 'number' hides a field."
                    )
                )
            )
        );
    }

    /**
     * CheckstyleValidator allows local variables and catch parameters with
     * names matching {@code ^[a-z]{3,12}$} pattern.
     * Additionally, catch parameters can use name {@code ex}.
     * @throws Exception In case of error
     */
    @Test
    public void allowsOnlyProperlyNamedLocalVariables() throws Exception {
        final String result = this.runValidation(
            "LocalVariableNames.java", false
        );
        MatcherAssert.assertThat(
            StringUtils.countMatches(result, "LocalVariableNames.java"),
            Matchers.is(Tv.TEN)
        );
        MatcherAssert.assertThat(
            result,
            Matchers.allOf(
                Matchers.not(
                    Matchers.stringContainsInOrder(
                        Arrays.asList(
                            "aaa", "twelveletter", "ise", "id", "parametername"
                        )
                    )
                ),
                Matchers.stringContainsInOrder(
                    Arrays.asList(
                        "Name 'prolongations' must match pattern",
                        "Name 'very_long_variable_id' must match pattern",
                        "Name 'camelCase' must match pattern",
                        "Name 'it' must match pattern",
                        "Name 'number1' must match pattern",
                        "Name 'ex' must match pattern",
                        "Name 'a' must match pattern",
                        "Name 'ae' must match pattern",
                        "Name 'e' must match pattern",
                        "Name 'it' must match pattern"
                    )
                )
            )
        );
    }

    /**
     * CheckstyleValidator can allow only properly ordered Javadoc at-clauses.
     * @throws Exception In case of error
     */
    @Test
    public void allowsOnlyProperlyOrderedAtClauses() throws Exception {
        final String result = this.runValidation(
            "AtClauseOrder.java", false
        );
        MatcherAssert.assertThat(
            StringUtils.countMatches(result, "AtClauseOrder.java"),
            Matchers.is(Tv.FOUR)
        );
        MatcherAssert.assertThat(
            result,
            Matchers.stringContainsInOrder(
                Arrays.asList(
                    "[23]: At-clauses have to appear in the order ",
                    "[50]: At-clauses have to appear in the order ",
                    "[60]: At-clauses have to appear in the order ",
                    "[61]: At-clauses have to appear in the order "
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
            Matchers.containsString("LICENSE found:")
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
            Matchers.containsString(
                "Extra semicolon in the end of try-with-resources head."
            )
        );
    }

    /**
     * Accepts try-with-resources without extra semicolon
     * at the end of the head.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void acceptsTryWithResourcesWithoutSemicolon() throws Exception {
        this.validateCheckstyle(
            "ValidSemicolon.java", true,
            Matchers.containsString(CheckstyleValidatorTest.NO_VIOLATIONS)
        );
    }

    /**
     * CheckstyleValidator cannot demand methods to be static in files with
     * names ending with {@code ITCase}.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void acceptsNonStaticMethodsInIt() throws Exception {
        this.validateCheckstyle(
            "ValidIT.java", true,
            Matchers.containsString(CheckstyleValidatorTest.NO_VIOLATIONS)
        );
    }

    /**
     * CheckstyleValidator cannot demand methods to be static in files with
     * names ending with {@code IT}.
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void acceptsNonStaticMethodsInItCases() throws Exception {
        this.validateCheckstyle(
            "ValidITCase.java", true,
            Matchers.containsString(CheckstyleValidatorTest.NO_VIOLATIONS)
        );
    }

    /**
     * CheckstyleValidator does not produce errors when last thing
     * in file are imports. The only exception that should be thrown is
     * qulice ValidationException.
     * @throws Exception In case of error
     */
    @Test(expected = ValidationException.class)
    public void doesNotThrowExceptionIfImportsOnly() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule.savePackageInfo(
            new File(mock.basedir(), CheckstyleValidatorTest.DIRECTORY)
        ).withLines(new String[] {"License-1.", "", "License-2."})
            .withEol("\n")
            .file();
        final String crlf = "\r\n";
        final String content = Joiner.on(crlf).join(
            "package com.google;",
            crlf,
            "import java.util.*;"
        );
        final Environment env = mock.withParam(
            CheckstyleValidatorTest.LICENSE_PROP,
            this.toURL(license)
        ).withFile("src/main/java/foo/Foo.java", content);
        new CheckstyleValidator().validate(env);
    }

    /**
     * CheckstyleValidator can distinguish between valid and invalid
     * catch parameter names.
     * @throws Exception In case of error
     */
    @Test
    public void distinguishesValidCatchParameterNames() throws Exception {
        final String result = this.runValidation(
            "CatchParameterNames.java", false
        );
        MatcherAssert.assertThat(
            StringUtils.countMatches(result, "CatchParameterNames"),
            Matchers.is(Tv.THREE)
        );
        MatcherAssert.assertThat(
            result,
            Matchers.stringContainsInOrder(
                Arrays.asList(
                    "[27]: Name 'ex_invalid_1' must match pattern",
                    "[29]: Name '$xxx' must match pattern",
                    "[31]: Name '_exp' must match pattern"
                )
            )
        );
    }

    /**
     * Test if URLs are valid despite having a line length over 80.
     * @throws Exception In case of error
     */
    @Test
    public void doesNotRejectUrlsInLongLines() throws Exception {
        this.validateCheckstyle(
            "UrlInLongLine.java", true,
            Matchers.containsString(CheckstyleValidatorTest.NO_VIOLATIONS)
        );
    }

    /**
     * CheckstyleValidator can allow spaces between methods of anonymous
     * classes.
     * @throws Exception In case of error
     */
    @Test
    public void allowsSpacesBetwenMethodsOfAnonymousClasses() throws Exception {
        this.validateCheckstyle(
            "BlankLinesOutsideMethodsPass.java", true,
            Matchers.containsString(CheckstyleValidatorTest.NO_VIOLATIONS)
        );
    }

    /**
     * CheckstyleValidator can reject spaces inside methods, regardless of
     * whether they are inside of an anonymous method or not.
     * @throws Exception In case of error
     */
    @Test
    public void rejectsSpacesInsideMethods() throws Exception {
        final String result = this.runValidation(
            "BlankLinesInsideMethodsFail.java", false
        );
        MatcherAssert.assertThat(
            result,
            Matchers.stringContainsInOrder(
                Arrays.asList(
                    "[17]: Empty line inside method (EmptyLinesCheck)",
                    "[21]: Empty line inside method (EmptyLinesCheck)",
                    "[23]: Empty line inside method (EmptyLinesCheck)",
                    "[27]: Empty line inside method (EmptyLinesCheck)",
                    "[30]: Empty line inside method (EmptyLinesCheck)",
                    "[34]: Empty line inside method (EmptyLinesCheck)",
                    "[36]: Empty line inside method (EmptyLinesCheck)",
                    "[40]: Empty line inside method (EmptyLinesCheck)",
                    "[43]: Empty line inside method (EmptyLinesCheck)",
                    "[50]: Empty line inside method (EmptyLinesCheck)",
                    "[52]: Empty line inside method (EmptyLinesCheck)",
                    "[54]: Empty line inside method (EmptyLinesCheck)"
                )
            )
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
        MatcherAssert.assertThat(this.runValidation(file, result), matcher);
    }

    /**
     * Returns string with Checkstyle validation results.
     * @param file File to check.
     * @param result Expected validation result.
     * @return String containing validation results in textual form.
     * @throws IOException In case of error
     */
    private String runValidation(final String file, final boolean result)
        throws IOException {
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
        return writer.toString();
    }
}
