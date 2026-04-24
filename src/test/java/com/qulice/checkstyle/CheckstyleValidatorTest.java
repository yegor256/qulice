/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.google.common.base.Joiner;
import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
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
@SuppressWarnings({"PMD.TooManyMethods", "PMD.UnitTestShouldIncludeAssert"})
final class CheckstyleValidatorTest {

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
     * Rule for testing.
     */
    private License rule;

    @BeforeEach
    void setRule() {
        this.rule = new License();
    }

    @Test
    void acceptsInstanceMethodReferences() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("InstanceMethodRef.java", true)
        );
    }

    /**
     * CheckstyleValidator can report error when parameter object is not
     * documented.
     * @throws Exception In case of error
     */
    @Test
    void reportsErrorWhenParameterObjectIsNotDocumented() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.validate(
                "ParametrizedClass.java", false,
                "Type Javadoc comment is missing @param <T> tag."
            )
        );
    }

    /**
     * CheckstyleValidator reports an error when package declaration
     * is line wrapped.
     * @throws Exception when error.
     */
    @Test
    void reportsErrorWhenLineWrap() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.validate("LineWrapPackage.java", false, "should not be line-wrapped")
        );
    }

    /**
     * CheckstyleValidator reports an error when indentation is not
     * bigger than previous line by exactly 4.
     * @throws Exception when error.
     */
    @Test
    void reportsErrorWhenIndentationIsIncorrect() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.validate(
            "InvalidIndentation.java", false,
            "Indentation (14) must be same or less than"
        )
        );
    }

    @Test
    void doesNotReportErrorWhenMissingJavadocInTests() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("MissingJavadocTest.java", true)
        );
    }

    /**
     * CheckstyleValidator reports an error when comment or Javadoc has too
     * long line.
     * @throws Exception when error.
     */
    @Test
    @SuppressWarnings("unchecked")
    void reportsErrorWhenCommentOrJavadocIsTooLong() throws Exception {
        MatcherAssert.assertThat(
            "Two long lines should be found",
            this.runValidation("TooLongLines.java", false),
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
     * CheckstyleValidator does not report a LineLength violation when
     * the long line is preceded by a {@code @checkstyle LineLengthCheck (N lines)}
     * comment.
     * See https://github.com/yegor256/qulice/issues/1360.
     * @throws Exception when error.
     */
    @Test
    void suppressesLineLengthInCommentsViaNearbyComment() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("SuppressLineLengthInComment.java", true)
        );
    }

    /**
     * CheckstyleValidator does not report a ConditionalRegexpMultilineCheck
     * violation when the offending code is preceded by a
     * {@code @checkstyle ConditionalRegexpMultilineCheck (N lines)} comment.
     * See https://github.com/yegor256/qulice/issues/1328.
     * @throws Exception when error.
     */
    @Test
    void suppressesConditionalRegexpMultilineInComment() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("SuppressConditionalRegexpMultilineInComment.java", true)
        );
    }

    @Test
    void acceptsValidSingleLineComment() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidSingleLineCommentCheck.java", true)
        );
    }

    @Test
    void acceptsValidIndentation() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidIndentation.java", true)
        );
    }

    /**
     * CheckstyleValidator reports an error when any method contains more
     * than one return statement.
     * @throws Exception when error.
     */
    @Test
    void reportsErrorOnMoreThanOneReturnStatement() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.validate(
                "ReturnCount.java", false,
                "Return count is 2 (max allowed for non-void methods/lambdas is 1)"
            )
        );
    }

    /**
     * Regression test for https://github.com/yegor256/qulice/issues/547:
     * a validated file must not pick up violations from a parallel run.
     * @throws Exception when error.
     */
    @Test
    void doesNotLeakViolationsBetweenTests() throws Exception {
        MatcherAssert.assertThat(
            "ReturnCount.java must not yield violations from ParametrizedClass.java",
            this.runValidation("ReturnCount.java", false),
            Matchers.not(
                Matchers.hasItem(
                    new ViolationMatcher(
                        "Type Javadoc comment is missing @param <T> tag.",
                        "ReturnCount.java"
                    )
                )
            )
        );
    }

    @Test
    void acceptsDefaultMethodsWithFinalModifiers() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("DefaultMethods.java", true)
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
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("AnnotationConstant.java", true)
        );
    }

    /**
     * CheckstyleValidator can allow constructor parameters named just like
     * fields.
     * @throws Exception In case of error
     */
    @Test
    void acceptsConstructorParametersNamedJustLikeFields() throws Exception {
        final String file = "ConstructorParams.java";
        final String name = "HiddenFieldCheck";
        MatcherAssert.assertThat(
            "Two hidden fields in ctor should be found",
            this.runValidation(file, false),
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
        MatcherAssert.assertThat(
            "Only invalid variables name should be found",
            this.runValidation(file, false),
            Matchers.<Iterable<Violation>>allOf(
                Matchers.iterableWithSize(10),
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
        final String message = "tags have to appear in the order";
        final String name = "AtclauseOrderCheck";
        MatcherAssert.assertThat(
            "3 tags with wrong order should be found",
            this.runValidation(file, false),
            Matchers.contains(
                new ViolationMatcher(
                    "Javadoc comment at column 3 has parse error.",
                    file,
                    "13",
                    "MissingDeprecatedCheck"
                ),
                new ViolationMatcher(
                    "Javadoc comment at column 3 has parse error.",
                    file,
                    "13",
                    name
                ),
                new ViolationMatcher(
                    message, file, "22", name
                ),
                new ViolationMatcher(
                    message, file, "49", name
                ),
                new ViolationMatcher(
                    "Class Class should be declared as final.",
                    file,
                    "60",
                    "FinalClassCheck"
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
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("AnnotationIndentation.java", true)
        );
    }

    /**
     * CheckstyleValidator can deny improper indentation in complex annotations.
     * This is regression test for #411.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void rejectsImproperIndentationInAnnotations() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("AnnotationIndentationNegative.java", false)
        );
    }

    /**
     * Fail validation with extra semicolon in the end
     * of try-with-resources head.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void testExtraSemicolonInTryWithResources() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.validate(
                "ExtraSemicolon.java", false,
                "Extra semicolon in the end of try-with-resources head."
            )
        );
    }

    @Test
    void testSupportsRecordTypes() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidRecord.java", true)
        );
    }

    /**
     * Accepts try-with-resources without extra semicolon
     * at the end of the head.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void acceptsTryWithResourcesWithoutSemicolon() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidSemicolon.java", true)
        );
    }

    /**
     * CheckstyleValidator rejects stray semicolons placed after the
     * closing brace of a class, method or constructor declaration.
     * Regression test for https://github.com/yegor256/qulice/issues/718.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void rejectsUnnecessarySemicolonsAfterDeclarations() throws Exception {
        final String file = "ExtraSemicolonInDeclaration.java";
        final String name = "ExtraSemicolonCheck";
        final String message = "Unnecessary semicolon";
        MatcherAssert.assertThat(
            "Stray semicolons after constructor, method and class must be reported",
            this.runValidation(file, false),
            Matchers.hasItems(
                new ViolationMatcher(message, file, "20", name),
                new ViolationMatcher(message, file, "28", name),
                new ViolationMatcher(message, file, "29", name)
            )
        );
    }

    /**
     * CheckstyleValidator accepts enum constants following the
     * upper-case, underscore-separated naming convention.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void acceptsProperlyNamedEnumValues() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidEnumValues.java", true)
        );
    }

    /**
     * CheckstyleValidator reports enum constants that do not
     * follow the upper-case, underscore-separated naming convention.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void rejectsImproperlyNamedEnumValues() throws Exception {
        final String file = "InvalidEnumValues.java";
        final String name = "EnumValueNameCheck";
        MatcherAssert.assertThat(
            "All three enum value naming violations should be reported",
            this.runValidation(file, false),
            Matchers.hasItems(
                new ViolationMatcher(
                    "Enum value anyName must match pattern",
                    file, "17", name
                ),
                new ViolationMatcher(
                    "Enum value MixedCase must match pattern",
                    file, "22", name
                ),
                new ViolationMatcher(
                    "Enum value lowercase must match pattern",
                    file, "27", name
                )
            )
        );
    }

    /**
     * CheckstyleValidator cannot demand methods to be static in files with
     * names ending with {@code ITCase}.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void acceptsNonStaticMethodsInIt() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidIT.java", true)
        );
    }

    /**
     * CheckstyleValidator cannot demand methods to be static in files with
     * names ending with {@code IT}.
     * @throws Exception If something wrong happens inside
     */
    @Test
    void acceptsNonStaticMethodsInItCases() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidITCase.java", true)
        );
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
        final String crlf = String.valueOf('\r').concat(String.valueOf('\n'));
        final String name = "Foo.java";
        final Environment env = mock.withParam(
            CheckstyleValidatorTest.LICENSE_PROP,
            this.toUrl(
                this.rule.savePackageInfo(
                    new File(mock.basedir(), CheckstyleValidatorTest.DIRECTORY)
                ).withLines("License-1.", "", "License-2.")
                    .withEol(String.valueOf('\n'))
                    .file()
            )
        ).withFile(
            String.format("src/main/java/foo/%s", name),
            Joiner.on(crlf).join(
                "package com.google;",
                crlf,
                "import java.util.*;"
            )
        );
        MatcherAssert.assertThat(
            "Validation error should exist",
            new CheckstyleValidator(env).validate(env.files(name)),
            Matchers.not(Matchers.<Violation>empty())
        );
    }

    /**
     * CheckstyleValidator can distinguish between valid and invalid
     * catch parameter names.
     * @throws Exception In case of error
     */
    @Test
    @SuppressWarnings("unchecked")
    void distinguishesValidCatchParameterNames() throws Exception {
        final String file = "CatchParameterNames.java";
        final String name = "CatchParameterNameCheck";
        MatcherAssert.assertThat(
            "All naming violations should be found",
            this.runValidation(file, false),
            Matchers.<Iterable<Violation>>allOf(
                Matchers.iterableWithSize(3),
                Matchers.hasItems(
                    new ViolationMatcher(
                        "Name 'ex_invalid_1' must match pattern", file, "27", name
                    ),
                    new ViolationMatcher(
                        "Name '$xxx' must match pattern", file, "29", name
                    ),
                    new ViolationMatcher(
                        "Name '_exp' must match pattern", file, "31", name
                    )
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
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("UrlInLongLine.java", true)
        );
    }

    /**
     * CheckstyleValidator can allow spaces between methods of anonymous
     * classes.
     * @throws Exception In case of error
     */
    @Test
    void allowsSpacesBetweenMethodsOfAnonymousClasses() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("BlankLinesOutsideMethodsPass.java", true)
        );
    }

    /**
     * CheckstyleValidator can reject spaces inside methods, regardless of
     * whether they are inside of an anonymous method or not.
     * @throws Exception In case of error
     */
    @Test
    @SuppressWarnings("unchecked")
    void rejectsSpacesInsideMethods() throws Exception {
        final String file = "BlankLinesInsideMethodsFail.java";
        final String name = "EmptyLinesCheck";
        final String message = "Empty line inside method";
        MatcherAssert.assertThat(
            "All empty lines should be found",
            this.runValidation(file, false),
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
        final String name = "AbbreviationAsWordInNameCheck";
        final String message = new Joined(
            " ",
            "Abbreviation in name '%s'",
            "must contain no more than '2' consecutive capital letters."
        ).asString();
        MatcherAssert.assertThat(
            "All long abbreviations should be found",
            this.runValidation(file, false),
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
        MatcherAssert.assertThat(
            "Hidden parameter in methods should be found",
            this.runValidation(file, false),
            Matchers.hasItems(
                new ViolationMatcher(
                    "'test' hides a field.", file, "18", "HiddenFieldCheck"
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
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidAbbreviationAsWordInNameIT.java", true)
        );
    }

    /**
     * CheckstyleValidator can allow final static fields and overrides
     * to have uppercase abbreviations.
     *
     * @throws Exception In case of error
     */
    @Test
    void allowsUppercaseAbbreviationExceptions() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidAbbreviationAsWordInName.java", true)
        );
    }

    /**
     * CheckstyleValidator can allow final static fields and overrides
     * to have uppercase abbreviations.
     *
     * @throws Exception In case of error
     */
    @Test
    void checkLambdaAndGenericsAtEndOfLine() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidLambdaAndGenericsAtEndOfLine.java", true)
        );
    }

    /**
     * CheckstyleValidator can reject a comma placed at the beginning of
     * a line, since it must stay attached to the preceding token.
     * @throws Exception If error
     */
    @Test
    void rejectsLeadingComma() throws Exception {
        final String file = "InvalidLeadingComma.java";
        MatcherAssert.assertThat(
            "leading comma must be reported",
            this.runValidation(file, false),
            Matchers.hasItem(
                new ViolationMatcher(
                    "',' is preceded with whitespace",
                    file, "22", "NoWhitespaceBeforeCheck"
                )
            )
        );
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
            this.runValidation(file, false),
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
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidDiamondsUsage.java", true)
        );
    }

    /**
     * CheckstyleValidator allows class name instead of diamond in case
     * of return statement.
     * @throws Exception If error
     */
    @Test
    void allowsFullGenericOperatorUsage() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("DiamondUsageNotNeeded.java", true)
        );
    }

    /**
     * CheckstyleValidator can allow usage of string literals on either sides.
     * E.g. both {@code txt.equals("contents")}
     * and {@code "contents".equals(txt)} are valid.
     * @throws Exception If error
     */
    @Test
    void allowsStringLiteralsOnBothSideInComparisons() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidLiteralComparisonCheck.java", true)
        );
    }

    /**
     * {@link MultilineJavadocTagsCheck} mustn't throw an internal exception,
     * if it meets a block comment instead of javadoc.
     * @throws Exception If an internal exception occurs
     */
    @Test
    void rejectsInvalidMethodDocWithoutInternalException() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("InvalidMethodDoc.java", false)
        );
    }

    @Test
    void rejectsFileLength() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("FileLengthCheck.java", true)
        );
    }

    @Test
    void rejectsLongLambdaBody() throws Exception {
        final String file = "LongLambdaInBody.java";
        MatcherAssert.assertThat(
            "Long lambda body must be reported",
            this.runValidation(file, false),
            Matchers.hasItem(
                new ViolationMatcher(
                    "Lambda body length is 25 lines (max allowed is 20).",
                    file, "19", "LambdaBodyLengthCheck"
                )
            )
        );
    }

    /**
     * CheckstyleValidator reports type parameter descriptions that do not
     * start with a capital letter. See
     * https://github.com/yegor256/qulice/issues/705.
     * @throws Exception when error.
     */
    @Test
    @SuppressWarnings("unchecked")
    void rejectsLowercaseTypeParamDescription() throws Exception {
        final String file = "InvalidTypeParamDescription.java";
        final String message =
            "@param tag description should start with capital letter";
        final String name = "RegexpSinglelineCheck";
        MatcherAssert.assertThat(
            "Both class and method type parameter descriptions must be reported",
            this.runValidation(file, false),
            Matchers.hasItems(
                new ViolationMatcher(message, file, "9", name),
                new ViolationMatcher(message, file, "17", name)
            )
        );
    }

    /**
     * CheckstyleValidator accepts type parameter descriptions that start
     * with a capital letter. See
     * https://github.com/yegor256/qulice/issues/705.
     * @throws Exception when error.
     */
    @Test
    void allowsUppercaseTypeParamDescription() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidTypeParamDescription.java", true)
        );
    }

    /**
     * CheckstyleValidator reports a fluent call that opens a multi-line
     * block while sitting alone on a line. See
     * https://github.com/yegor256/qulice/issues/670.
     * @throws Exception when error.
     */
    @Test
    void rejectsFluentCallAloneOnLineOpeningMultiLineBlock() throws Exception {
        final String file = "InvalidFluentCallFormatting.java";
        MatcherAssert.assertThat(
            "Fluent call opening a multi-line block alone on a line must be reported",
            this.runValidation(file, false),
            Matchers.hasItem(
                new ViolationMatcher(
                    "A fluent call opening a multi-line block must be attached to the previous line",
                    file, "20", "RegexpSinglelineCheck"
                )
            )
        );
    }

    /**
     * CheckstyleValidator accepts fluent calls that open a multi-line
     * block only when attached to the previous line. See
     * https://github.com/yegor256/qulice/issues/670.
     * @throws Exception when error.
     */
    @Test
    void acceptsFluentCallAttachedToPreviousLine() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidFluentCallFormatting.java", true)
        );
    }

    /**
     * CheckstyleValidator accepts the equality operator at the start of
     * a wrapped line, as required by OperatorWrap in 'nl' mode. See
     * https://github.com/yegor256/qulice/issues/790.
     * @throws Exception when error.
     */
    @Test
    void acceptsEqualityOperatorAtStartOfWrappedLine() throws Exception {
        Assertions.assertDoesNotThrow(
            () -> this.runValidation("ValidEqualityOperatorWrap.java", true)
        );
    }

    /**
     * CheckstyleValidator reports an error when the colon in an enhanced
     * for statement is not surrounded by whitespace. See
     * https://github.com/yegor256/qulice/issues/721.
     * @throws Exception when error.
     */
    @Test
    void rejectsMissingSpaceAroundColonInEnhancedFor() throws Exception {
        this.validate(
            "InvalidEnhancedForColon.java", false,
            "':' is not preceded with whitespace"
        );
    }

    /**
     * CheckstyleValidator accepts the colon in an enhanced for statement
     * when it is surrounded by whitespace. See
     * https://github.com/yegor256/qulice/issues/721.
     * @throws Exception when error.
     */
    @Test
    void acceptsSpacesAroundColonInEnhancedFor() throws Exception {
        this.runValidation("ValidEnhancedForColon.java", true);
    }

    /**
     * Convert file name to URL.
     * @param file The file
     * @return The URL
     */
    private String toUrl(final File file) {
        return String.format("file:%s", file);
    }

    /**
     * Validates that checkstyle reported given violation.
     * @param file File to check
     * @param result Expected validation result
     * @param message Message to match
     * @throws Exception In case of error
     */
    private void validate(final String file, final boolean result,
        final String message) throws Exception {
        MatcherAssert.assertThat(
            "validation should yield expected message",
            this.runValidation(file, result),
            Matchers.hasItem(new ViolationMatcher(message, file))
        );
    }

    /**
     * Returns string with Checkstyle validation results.
     * @param file File to check
     * @param passes Whether validation is expected to pass
     * @return String containing validation results in textual form
     * @throws IOException In case of error
     */
    private Collection<Violation> runValidation(final String file,
        final boolean passes) throws IOException {
        final Environment.Mock mock = new Environment.Mock();
        final Environment env = mock.withParam(
            CheckstyleValidatorTest.LICENSE_PROP,
            this.toUrl(
                this.rule.savePackageInfo(
                    new File(mock.basedir(), CheckstyleValidatorTest.DIRECTORY)
                ).withLines(CheckstyleValidatorTest.LICENSE)
                    .withEol(String.valueOf('\n')).file()
            )
        ).withFile(
            String.format("src/main/java/foo/%s", file),
            new IoCheckedText(
                new TextOf(
                    new ResourceOf(
                        new FormattedText("com/qulice/checkstyle/%s", file)
                    )
                )
            ).asString()
        );
        final Collection<Violation> results =
            new CheckstyleValidator(env).validate(env.files(file));
        MatcherAssert.assertThat(
            "validation result should match expected state",
            results.isEmpty(),
            Matchers.is(passes)
        );
        return results;
    }
}
