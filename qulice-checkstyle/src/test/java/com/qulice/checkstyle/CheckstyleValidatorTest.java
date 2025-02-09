/*
 * Copyright (c) 2011-2025 Yegor Bugayenko
 *
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
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collection;
import org.cactoos.list.ListOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CheckstyleValidator} class.
 * @since 0.3
 * @todo #412:30min Split this class into smaller ones and remove PMD
 *  exclude `TooManyMethods`. Good candidates for moving out of this class
 *  are all that use `validateCheckstyle` method.
 * @checkstyle ClassDataAbstractionCoupling (800 lines)
 *  Can also be removed after splitting up this class into smaller ones.
 */
@SuppressWarnings(
    {
        "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals", "PMD.GodClass"
    }
)
final class CheckstyleValidatorTest {

    @BeforeEach
    public void updateRule() {
        CheckstyleTestBase.setRule();
    }

    /**
     * CheckstyleValidator can catch checkstyle violations.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void catchesCheckstyleViolationsInLicense() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = CheckstyleTestBase.getRule().savePackageInfo(
            new File(mock.basedir(), CheckstyleTestBase.DIRECTORY)
        ).withLines("License-1.", "", "License-2.")
            .withEol("\n")
            .file();
        final String content =
            // @checkstyle StringLiteralsConcatenation (4 lines)
            "/" + "*\n * License-3.\n *\n * License-2.\n */\n"
                + "package foo;\n"
                + "public class Foo { }\n";
        final String name = "Foo.java";
        final Environment env = mock.withParam(
            CheckstyleTestBase.LICENSE_PROP,
            CheckstyleTestBase.toUrl(license)
        ).withFile(String.format("src/main/java/foo/%s", name), content);
        final Collection<Violation> results =
            new CheckstyleValidator(env)
                .validate(env.files(name));
        MatcherAssert.assertThat(
            "Header validation is expected",
            results,
            Matchers.hasItem(
                new ViolationMatcher(
                    "Line does not match expected header line of", name
                )
            )
        );
    }

    /**
     * CheckstyleValidator can accept instance method references.
     * @throws Exception In case of error
     */
    @Test
    void acceptsInstanceMethodReferences() throws Exception {
        CheckstyleTestBase.runValidation(
            "InstanceMethodRef.java", true
        );
    }

    /**
     * CheckstyleValidator can report error when parameter object is not
     * documented.
     * @throws Exception In case of error
     */
    @Test
    void reportsErrorWhenParameterObjectIsNotDocumented()
        throws Exception {
        CheckstyleTestBase.validate(
            "ParametrizedClass.java", false,
            "Type Javadoc comment is missing @param <T> tag."
        );
    }

    /**
     * CheckstyleValidator reports an error when package declaration
     * is line wrapped.
     * @throws Exception when error.
     */
    @Test
    void reportsErrorWhenLineWrap()
        throws Exception {
        CheckstyleTestBase.validate(
            "LineWrapPackage.java", false,
            "should not be line-wrapped"
        );
    }

    /**
     * CheckstyleValidator reports an error when indentation is not
     * bigger than previous line by exactly 4.
     * @throws Exception when error.
     */
    @Test
    void reportsErrorWhenIndentationIsIncorrect() throws Exception {
        CheckstyleTestBase.validate(
            "InvalidIndentation.java",
            false,
            "Indentation (14) must be same or less than"
        );
    }

    /**
     * CheckstyleValidator does not report an error when there is no JavaDoc
     * on method in JUnit tests.
     * @throws Exception when error.
     */
    @Test
    void doesNotReportErrorWhenMissingJavadocInTests() throws Exception {
        CheckstyleTestBase.runValidation("MissingJavadocTest.java", true);
    }

    /**
     * CheckstyleValidator reports an error when comment or Javadoc has too
     * long line.
     * @throws Exception when error.
     */
    @Test
    @SuppressWarnings("unchecked")
    void reportsErrorWhenCommentOrJavadocIsTooLong() throws Exception {
        final Collection<Violation> results =
            CheckstyleTestBase.runValidation("TooLongLines.java", false);
        MatcherAssert.assertThat(
            "Two long lines should be found",
            results,
            Matchers.hasItems(
                new ViolationMatcher(
                    "Line is longer than 100 characters (found 104)", ""
                ),
                new ViolationMatcher(
                    "Line is longer than 100 characters (found 103)", ""
                )
            )
        );
    }

    /**
     * CheckstyleValidator can report Apache Commons {@code CharEncoding} class
     * usages.
     * @throws Exception when error.
     */
    @Test
    @SuppressWarnings("unchecked")
    void reportsAllCharEncodingUsages() throws Exception {
        final String message =
            "Use java.nio.charset.StandardCharsets instead";
        final String file = "DoNotUseCharEncoding.java";
        final Collection<Violation> results = CheckstyleTestBase.runValidation(
            file, false
        );
        final String name = "RegexpSinglelineCheck";
        MatcherAssert.assertThat(
            "8 violations should be found",
            results,
            new IsIterableContainingInOrder<>(
                new ListOf<>(
                    new ViolationMatcher(
                        message, file, "6", name
                    ),
                    new ViolationMatcher(
                        message, file, "7", name
                    ),
                    new ViolationMatcher(
                        message, file, "8", name
                    ),
                    new ViolationMatcher(
                        message, file, "9", name
                    ),
                    new ViolationMatcher(
                        message, file, "22", name
                    ),
                    new ViolationMatcher(
                        message, file, "23", name
                    ),
                    new ViolationMatcher(
                        message, file, "24", name
                    ),
                    new ViolationMatcher(
                        message, file, "25", name
                    )
                )
            )
        );
    }

    /**
     * CheckstyleValidator accepts string literal which
     * contains multiline comment.
     * @throws Exception If test failed.
     */
    @Test
    void acceptsValidSingleLineComment() throws Exception {
        CheckstyleTestBase.runValidation(
            "ValidSingleLineCommentCheck.java", true
        );
    }

    /**
     * CheckstyleValidator accepts the valid indentation
     * refused by forceStrictCondition.
     * @throws Exception when error.
     */
    @Test
    void acceptsValidIndentation() throws Exception {
        CheckstyleTestBase.runValidation(
            "ValidIndentation.java", true
        );
    }

    /**
     * CheckstyleValidator reports an error when any method contains more
     * than one return statement.
     * @throws Exception when error.
     */
    @Test
    void reportsErrorOnMoreThanOneReturnStatement() throws Exception {
        CheckstyleTestBase.validate(
            "ReturnCount.java", false,
            "Return count is 2 (max allowed for non-void methods/lambdas is 1)"
        );
    }

    /**
     * CheckstyleValidator can accept default methods with final modifiers.
     * @throws Exception In case of error
     */
    @Test
    void acceptsDefaultMethodsWithFinalModifiers() throws Exception {
        CheckstyleTestBase.runValidation(
            "DefaultMethods.java", true
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
    void acceptsConstantUsedInMethodAnnotation() throws Exception {
        CheckstyleTestBase.runValidation("AnnotationConstant.java", true);
    }

    /**
     * CheckstyleValidator can allow constructor parameters named just like
     * fields.
     * @throws Exception In case of error
     */
    @Test
    void acceptsConstructorParametersNamedJustLikeFields()
        throws Exception {
        final String file = "ConstructorParams.java";
        final Collection<Violation> results = CheckstyleTestBase.runValidation(file, false);
        final String name = "HiddenFieldCheck";
        MatcherAssert.assertThat(
            "Two hidden fields in ctor should be found",
            results,
            Matchers.allOf(
                Matchers.hasItem(
                    new ViolationMatcher(
                        "'number' hides a field.", file, "29", name
                    )
                ),
                Matchers.not(
                    Matchers.hasItem(
                        new ViolationMatcher(
                            "'number' hides a field.", file, "20", name
                        )
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
    @SuppressWarnings("unchecked")
    void allowsOnlyProperlyNamedLocalVariables() throws Exception {
        final String file = "LocalVariableNames.java";
        final Collection<Violation> results = CheckstyleTestBase.runValidation(
            file, false
        );
        MatcherAssert.assertThat(
            "10 total violations should be found",
            results,
            Matchers.hasSize(10)
        );
        MatcherAssert.assertThat(
            "Only invalid variables name should be found",
            results,
            Matchers.allOf(
                Matchers.not(
                    Matchers.hasItems(
                        new ViolationMatcher("aaa", file),
                        new ViolationMatcher("twelveletter", file),
                        new ViolationMatcher("ise", file),
                        new ViolationMatcher("id", file),
                        new ViolationMatcher("parametername", file)
                    )
                ),
                Matchers.hasItems(
                    new ViolationMatcher(
                        "Name 'prolongations' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'very_long_variable_id' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'camelCase' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'it' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'number1' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'ex' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'a' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'ae' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'e' must match pattern", file
                    ),
                    new ViolationMatcher(
                        "Name 'it' must match pattern", file
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
    void allowsOnlyProperlyOrderedAtClauses() throws Exception {
        final String file = "AtClauseOrder.java";
        final Collection<Violation> results = CheckstyleTestBase.runValidation(
            file, false
        );
        final String message = "tags have to appear in the order";
        final String name = "AtclauseOrderCheck";
        MatcherAssert.assertThat(
            "3 tags with wrong order should be found",
            results,
            Matchers.contains(
                new ViolationMatcher(
                    message, file, "14", name
                ),
                new ViolationMatcher(
                    message, file, "21", name
                ),
                new ViolationMatcher(
                    message, file, "48", name
                ),
                new ViolationMatcher(
                    "Class Class should be declared as final.", file, "59", "FinalClassCheck"
                )
            )
        );
    }

    /**
     * CheckstyleValidator will fail if  Windows EOL-s are used.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void passesWindowsEndsOfLineWithoutException() throws Exception {
        final String file = "WindowsEol.java";
        final Collection<Violation> results = CheckstyleTestBase.runValidation(file, false);
        MatcherAssert.assertThat(
            "violation should be reported correctly",
            results,
            Matchers.contains(
                new ViolationMatcher(
                    "Line does not match expected header line of ' */'.",
                    file,
                    "3",
                    "HeaderCheck"
                )
            )
        );
    }

    /**
     * Fail validation with Windows-style formatting of the license and
     * Linux-style formatting of the sources.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void testWindowsEndsOfLineWithLinuxSources() throws Exception {
        final String file = "WindowsEolLinux.java";
        final Collection<Violation> results = CheckstyleTestBase.runValidation(file, false);
        MatcherAssert.assertThat(
            "violation should be reported correctly",
            results,
            Matchers.contains(
                new ViolationMatcher(
                    "Line does not match expected header line of ' * Hello.'.",
                    file,
                    "2",
                    "HeaderCheck"
                )
            )
        );
    }

    /**
     * CheckstyleValidator can allow proper indentation in complex annotations.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void allowsProperIndentationInAnnotations() throws Exception {
        CheckstyleTestBase.runValidation("AnnotationIndentation.java", true);
    }

    /**
     * CheckstyleValidator can deny improper indentation in complex annotations.
     * This is regression test for #411.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void rejectsImproperIndentationInAnnotations() throws Exception {
        CheckstyleTestBase.runValidation("AnnotationIndentationNegative.java", false);
    }

    /**
     * Fail validation with extra semicolon in the end
     * of try-with-resources head.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void testExtraSemicolonInTryWithResources() throws Exception {
        CheckstyleTestBase.validate(
            "ExtraSemicolon.java", false,
            "Extra semicolon in the end of try-with-resources head."
        );
    }

    /**
     * Correctly parses Record type.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void testSupportsRecordTypes() throws Exception {
        CheckstyleTestBase.runValidation("ValidRecord.java", true);
    }

    /**
     * Accepts try-with-resources without extra semicolon
     * at the end of the head.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void acceptsTryWithResourcesWithoutSemicolon() throws Exception {
        CheckstyleTestBase.runValidation("ValidSemicolon.java", true);
    }

    /**
     * CheckstyleValidator cannot demand methods to be static in files with
     * names ending with {@code ITCase}.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void acceptsNonStaticMethodsInIt() throws Exception {
        CheckstyleTestBase.runValidation("ValidIT.java", true);
    }

    /**
     * CheckstyleValidator cannot demand methods to be static in files with
     * names ending with {@code IT}.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void acceptsNonStaticMethodsInItCases() throws Exception {
        CheckstyleTestBase.runValidation("ValidITCase.java", true);
    }

    /**
     * CheckstyleValidator does not produce errors when last thing
     * in file are imports. The only exception that should be thrown is
     * qulice ValidationException.
     * @throws Exception In case of error
     */
    @Test
    void doesNotThrowExceptionIfImportsOnly() throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = CheckstyleTestBase.getRule().savePackageInfo(
            new File(mock.basedir(), CheckstyleTestBase.DIRECTORY)
        ).withLines("License-1.", "", "License-2.")
            .withEol("\n")
            .file();
        final String crlf = "\r\n";
        final String content = Joiner.on(crlf).join(
            "package com.google;",
            crlf,
            "import java.util.*;"
        );
        final String name = "Foo.java";
        final Environment env = mock.withParam(
            CheckstyleTestBase.LICENSE_PROP,
            CheckstyleTestBase.toUrl(license)
        ).withFile(String.format("src/main/java/foo/%s", name), content);
        final Collection<Violation> results =
            new CheckstyleValidator(env).validate(env.files(name));
        MatcherAssert.assertThat(
            "Validation error should exist",
            results,
            Matchers.not(Matchers.<Violation>empty())
        );
    }

    /**
     * CheckstyleValidator can distinguish between valid and invalid
     * catch parameter names.
     * @throws Exception In case of error
     */
    @Test
    @SuppressWarnings({"unchecked", "PMD.AvoidDuplicateLiterals"})
    void distinguishesValidCatchParameterNames() throws Exception {
        final String file = "CatchParameterNames.java";
        final Collection<Violation> results = CheckstyleTestBase.runValidation(
            file, false
        );
        MatcherAssert.assertThat(
            "Should be 3 violations",
            results,
            Matchers.hasSize(3)
        );
        final String name = "CatchParameterNameCheck";
        MatcherAssert.assertThat(
            "All naming violations should be found",
            results,
            Matchers.hasItems(
                new ViolationMatcher(
                    "Name 'ex_invalid_1' must match pattern", file, "26", name
                ),
                new ViolationMatcher(
                    "Name '$xxx' must match pattern", file, "28", name
                ),
                new ViolationMatcher(
                    "Name '_exp' must match pattern", file, "30", name
                )
            )
        );
    }

    /**
     * Test if URLs are valid despite having a line length over 80.
     * @throws Exception In case of error
     */
    @Test
    void doesNotRejectUrlsInLongLines() throws Exception {
        CheckstyleTestBase.runValidation("UrlInLongLine.java", true);
    }

    /**
     * CheckstyleValidator can allow spaces between methods of anonymous
     * classes.
     * @throws Exception In case of error
     */
    @Test
    void allowsSpacesBetweenMethodsOfAnonymousClasses()
        throws Exception {
        CheckstyleTestBase.runValidation("BlankLinesOutsideMethodsPass.java", true);
    }

    /**
     * CheckstyleValidator can reject spaces inside methods, regardless of
     * whether they are inside of an anonymous method or not.
     * @throws Exception In case of error
     */
    @Test
    @SuppressWarnings({"unchecked", "PMD.AvoidDuplicateLiterals"})
    void rejectsSpacesInsideMethods() throws Exception {
        final String file = "BlankLinesInsideMethodsFail.java";
        final Collection<Violation> result = CheckstyleTestBase.runValidation(
            file, false
        );
        final String name = "EmptyLinesCheck";
        final String message = "Empty line inside method";
        MatcherAssert.assertThat(
            "All empty lines should be found",
            result,
            Matchers.hasItems(
                new ViolationMatcher(message, file, "15", name),
                new ViolationMatcher(message, file, "19", name),
                new ViolationMatcher(message, file, "21", name),
                new ViolationMatcher(message, file, "25", name),
                new ViolationMatcher(message, file, "28", name),
                new ViolationMatcher(message, file, "32", name),
                new ViolationMatcher(message, file, "34", name),
                new ViolationMatcher(message, file, "38", name),
                new ViolationMatcher(message, file, "41", name),
                new ViolationMatcher(message, file, "48", name),
                new ViolationMatcher(message, file, "50", name),
                new ViolationMatcher(message, file, "52", name)
            )
        );
    }

    /**
     * CheckstyleValidator can reject uppercase abbreviations in naming
     * outside of final static fields.
     *
     * @throws Exception In case of error
     */
    @Test
    @SuppressWarnings("unchecked")
    void rejectsUppercaseAbbreviations() throws Exception {
        final String file = "InvalidAbbreviationAsWordInNameXML.java";
        final Collection<Violation> results = CheckstyleTestBase.runValidation(
            file, false
        );
        final String name = "AbbreviationAsWordInNameCheck";
        final String message = new Joined(
            " ",
            "Abbreviation in name '%s'",
            "must contain no more than '2' consecutive capital letters."
        ).asString();
        MatcherAssert.assertThat(
            "All long abbreviations should be found",
            results,
            Matchers.hasItems(
                new ViolationMatcher(
                    String.format(
                        message, "InvalidAbbreviationAsWordInNameXML"
                    ),
                    file, "11", name
                ),
                new ViolationMatcher(
                    String.format(message, "InvalidHTML"), file,
                    "15", name
                )
            )
        );
    }

    @Test
    void rejectsHiddenParameters() throws Exception {
        final String file = "HiddenParameter.java";
        final Collection<Violation> results = CheckstyleTestBase.runValidation(
            file, false
        );
        final String name = "HiddenFieldCheck";
        final String message = "'test' hides a field.";
        MatcherAssert.assertThat(
            "Hidden parameter in methods should be found",
            results,
            Matchers.hasItems(
                new ViolationMatcher(
                    message, file, "17", name
                )
            )
        );
    }

    /**
     * CheckstyleValidator can allow IT as an uppercase abbreviation.
     *
     * @throws Exception In case of error
     */
    @Test
    void allowsITUppercaseAbbreviation() throws Exception {
        CheckstyleTestBase.runValidation("ValidAbbreviationAsWordInNameIT.java", true);
    }

    /**
     * CheckstyleValidator can allow final static fields and overrides
     * to have uppercase abbreviations.
     *
     * @throws Exception In case of error
     */
    @Test
    void allowsUppercaseAbbreviationExceptions() throws Exception {
        CheckstyleTestBase.runValidation("ValidAbbreviationAsWordInName.java", true);
    }

    /**
     * CheckstyleValidator can allow final static fields and overrides
     * to have uppercase abbreviations.
     *
     * @throws Exception In case of error
     */
    @Test
    void checkLambdaAndGenericsAtEndOfLine() throws Exception {
        CheckstyleTestBase.runValidation("ValidLambdaAndGenericsAtEndOfLine.java", true);
    }

    /**
     * CheckstyleValidator can reject non diamond operator usage.
     * @throws Exception If error
     */
    @Test
    void rejectsNonDiamondOperatorUsage() throws Exception {
        final String file = "InvalidDiamondsUsage.java";
        final String name = "DiamondOperatorCheck";
        final String message = "Use diamond operator";
        MatcherAssert.assertThat(
            "Two diamond violations should be found",
            CheckstyleTestBase.runValidation(file, false),
            Matchers.hasItems(
                new ViolationMatcher(message, file, "19", name),
                new ViolationMatcher(message, file, "29", name)
            )
        );
    }

    /**
     * CheckstyleValidator can allow diamond operator usage.
     * @throws Exception If error
     */
    @Test
    void allowsDiamondOperatorUsage() throws Exception {
        CheckstyleTestBase.runValidation("ValidDiamondsUsage.java", true);
    }

    /**
     * CheckstyleValidator allows class name instead of diamond in case
     * of return statement.
     * @throws Exception If error
     */
    @Test
    void allowsFullGenericOperatorUsage() throws Exception {
        CheckstyleTestBase.runValidation("DiamondUsageNotNeeded.java", true);
    }

    /**
     * CheckstyleValidator can allow usage of string literals on either sides.
     * E.g. both {@code txt.equals("contents")}
     * and {@code "contents".equals(txt)} are valid.
     * @throws Exception If error
     */
    @Test
    void allowsStringLiteralsOnBothSideInComparisons()
        throws Exception {
        CheckstyleTestBase.runValidation("ValidLiteralComparisonCheck.java", true);
    }
}
