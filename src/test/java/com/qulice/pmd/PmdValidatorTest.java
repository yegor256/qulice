/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

import com.google.common.base.Joiner;
import com.jcabi.matchers.RegexMatchers;
import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

/**
 * Test case for {@link PmdValidator} class.
 *
 * @since 0.3
 */
@SuppressWarnings("PMD.TooManyMethods")
final class PmdValidatorTest {

    /**
     * Error message for forbidding access to static fields other than with a
     * static way.
     *
     * @checkstyle LineLengthCheck (40 lines)
     */
    private static final String STATIC_ACCESS =
        "%s\\[\\d+-\\d+\\]: Static fields should be accessed in a static way \\[CLASS_NAME.FIELD_NAME\\]\\.";

    /**
     * Error message for forbidding access to static members via instance
     * reference using 'this' keyword.
     */
    private static final String STATIC_VIA_THIS =
        "%s\\[\\d+-\\d+\\]: Static members should be accessed in a static way \\[CLASS_NAME.FIELD_NAME\\], not via instance reference.";

    /**
     * Error message for forbidding instructions inside a constructor other than
     * field initialization or call to other constructors.
     */
    private static final String CODE_IN_CON =
        "%s\\[\\d+-\\d+\\]: Only field initialization or call to other constructors in a constructor";

    /**
     * Pattern for non-constructor field initialization.
     */
    private static final String NO_CON_INIT =
        "%s\\[\\d+-\\d+\\]: Avoid doing field initialization outside constructor.";

    /**
     * Pattern multiple constructors field initialization.
     */
    private static final String MULT_CON_INIT =
        "%s\\[\\d+-\\d+\\]: Avoid field initialization in several constructors.";

    /**
     * Template for string inside brackets.
     */
    private static final String BRACKETS = "(%s)";

    /**
     * Error message used to inform about using public static method.
     */
    private static final String STATIC_METHODS =
        "Public static methods are prohibited";

    /**
     * Error text for Files.createFile.
     */
    private static final String FILES_CREATE_ERR =
        "Files.createFile should not be used in tests, replace them with @Rule TemporaryFolder";

    /**
     * PmdValidator can find violations in Java file(s).
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void findsProblemsInJavaFiles() throws Exception {
        final String file = "src/main/java/Main.java";
        final Environment env = new Environment.Mock()
            .withFile(file, "class Main { int x = 0; }");
        MatcherAssert.assertThat(
            "Violations should be found",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.not(Matchers.<Violation>empty())
        );
    }

    /**
     * PmdValidator can understand method references.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void understandsMethodReferences() throws Exception {
        new PmdAssert(
            "UnderstandsMethodReferences.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("(UnusedPrivateMethod)")
            )
        ).assertOk();
    }

    /**
     * PmdValidator does not think that constant is unused when it is used just
     * from the inner class.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void doesNotComplainAboutConstantsInInnerClasses() throws Exception {
        final String file = "src/main/java/foo/Foo.java";
        final Environment env = new Environment.Mock().withFile(
            file,
            Joiner.on('\n').join(
                "package foo;",
                "interface Foo {",
                "  final class Bar implements Foo {",
                "    private static final Pattern TEST =",
                "      Pattern.compile(\"hey\");",
                "    public String doSomething() {",
                "      return Foo.Bar.TEST.toString();",
                "    }",
                "  }",
                "}"
            )
        );
        MatcherAssert.assertThat(
            "Private constant in inner class is not a violation",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.<Violation>empty()
        );
    }

    /**
     * PmdValidator can allow field initialization when constructor is missing.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsFieldInitializationWhenConstructorIsMissing()
        throws Exception {
        final String file = "FieldInitNoConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(
                        PmdValidatorTest.NO_CON_INIT,
                        file
                    )
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator can forbid field initialization when constructor exists.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsFieldInitializationWhenConstructorExists()
        throws Exception {
        final String file = "FieldInitConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(
                    PmdValidatorTest.NO_CON_INIT,
                    file
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator can allow static field initialization when constructor
     * exists.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsStaticFieldInitializationWhenConstructorExists()
        throws Exception {
        final String file = "StaticFieldInitConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(
                        PmdValidatorTest.NO_CON_INIT,
                        file
                    )
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator can forbid field initialization in several constructors.
     * Only one constructor should do real work. Others - delegate to it.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsFieldInitializationInSeveralConstructors()
        throws Exception {
        final String file = "FieldInitSeveralConstructors.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(
                    PmdValidatorTest.MULT_CON_INIT,
                    file
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator can allow field initialization in one constructor. Only one
     * constructor should do real work. Others - delegate to it.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsFieldInitializationInOneConstructor()
        throws Exception {
        final String file = "FieldInitOneConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(
                        PmdValidatorTest.MULT_CON_INIT,
                        file
                    )
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator forbids unnecessary final modifier for methods.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsUnnecessaryFinalModifier()
        throws Exception {
        new PmdAssert(
            "UnnecessaryFinalModifier.java",
            Matchers.is(false),
            Matchers.containsString("Unnecessary modifier 'final'")
        ).assertOk();
    }

    /**
     * PmdValidator forbid useless parentheses.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsUselessParentheses()
        throws Exception {
        new PmdAssert(
            "UselessParentheses.java",
            Matchers.is(false),
            Matchers.containsString("Useless parentheses")
        ).assertOk();
    }

    /**
     * PmdValidator forbids code in constructor other than field
     * initialization.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsCodeInConstructor()
        throws Exception {
        final String file = "CodeInConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(
                    PmdValidatorTest.CODE_IN_CON,
                    file
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator allows lambda in constructor.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsLambdaInConstructor()
        throws Exception {
        final String file = "LambdaInConstructor.java";
        new PmdAssert(
            file,
            new IsEqual<>(true),
            new IsNot<>(
                RegexMatchers.containsPattern(
                    String.format(
                        PmdValidatorTest.CODE_IN_CON,
                        file
                    )
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator forbids usage of Files.createFile in tests.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsFilesCreateFileInTests() throws Exception {
        new PmdAssert(
            "FilesCreateFileTest.java",
            Matchers.is(false),
            Matchers.containsString(
                PmdValidatorTest.FILES_CREATE_ERR
            )
        ).assertOk();
    }

    /**
     * PmdValidator allows usage of Files.createFile outside of tests.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsFilesCreateFileOutsideOfTests() throws Exception {
        new PmdAssert(
            "FilesCreateFileOther.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString(
                    PmdValidatorTest.FILES_CREATE_ERR
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator accepts calls to other constructors or call to super class
     * constructor in constructors.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void acceptsCallToConstructorInConstructor()
        throws Exception {
        final String file = "CallToConstructorInConstructor.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(
                        PmdValidatorTest.CODE_IN_CON,
                        file
                    )
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator accepts calls to static fields in a static way.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void acceptsCallToStaticFieldsInStaticWay()
        throws Exception {
        final String file = "StaticAccessToStaticFields.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.allOf(
                Matchers.not(
                    RegexMatchers.containsPattern(
                        String.format(
                            PmdValidatorTest.STATIC_ACCESS,
                            file
                        )
                    )
                ),
                Matchers.not(
                    RegexMatchers.containsPattern(
                        String.format(
                            PmdValidatorTest.STATIC_VIA_THIS,
                            file
                        )
                    )
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator accepts direct assignment to a static final field inside
     * a static initializer, because qualifying it with the class name would
     * not compile for a final field.
     *
     * @throws Exception If something wrong happens inside.
     * @see <a href="https://github.com/yegor256/qulice/issues/719">#719</a>
     */
    @Test
    void acceptsDirectAccessToStaticFieldInStaticInitializer()
        throws Exception {
        final String file = "StaticInitializerAssignsFinalField.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(
                        PmdValidatorTest.STATIC_ACCESS,
                        file
                    )
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator forbids calls to static fields directly in a non static
     * way.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsCallToStaticFieldsDirectly()
        throws Exception {
        final String file = "DirectAccessToStaticFields.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(
                    PmdValidatorTest.STATIC_ACCESS,
                    file
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator forbids calls to static fields in a non static way via
     * instance reference.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsCallToStaticFieldsViaThis()
        throws Exception {
        final String file = "AccessToStaticFieldsViaThis.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(
                    PmdValidatorTest.STATIC_VIA_THIS,
                    file
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator forbids calls to static methods in a non static way via
     * instance reference.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsCallToStaticMethodsViaThis()
        throws Exception {
        final String file = "AccessToStaticMethodsViaThis.java";
        new PmdAssert(
            file,
            Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(
                    PmdValidatorTest.STATIC_VIA_THIS,
                    file
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator forbids non public clone methods (PMD rule
     * rulesets/java/clone.xml/CloneMethodMustBePublic).
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsNonPublicCloneMethod() throws Exception {
        new PmdAssert(
            "CloneMethodMustBePublic.java",
            Matchers.is(false),
            Matchers.containsString(
                String.format(
                    PmdValidatorTest.BRACKETS,
                    "CloneMethodMustBePublic"
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator forbids clone methods with return type not matching class
     * name (PMD rule
     * rulesets/java/clone.xml/CloneMethodReturnTypeMustMatchClassName).
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsCloneMethodReturnTypeNotMatchingClassName()
        throws Exception {
        new PmdAssert(
            "CloneMethodReturnTypeMustMatchClassName.java",
            Matchers.is(false),
            Matchers.containsString(
                String.format(
                    PmdValidatorTest.BRACKETS,
                    "CloneMethodReturnTypeMustMatchClassName"
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator forbids ternary operators that can be simplified (PMD rule
     * rulesets/java/basic.xml/SimplifiedTernary).
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsNonSimplifiedTernaryOperators()
        throws Exception {
        new PmdAssert(
            "SimplifiedTernary.java",
            Matchers.is(false),
            Matchers.containsString(
                String.format(
                    PmdValidatorTest.BRACKETS,
                    "SimplifiedTernary"
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator can allow non-static, non-transient fields.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsNonTransientFields() throws Exception {
        new PmdAssert(
            "AllowNonTransientFields.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString(
                    "Found non-transient, non-static member."
                )
            )
        ).assertOk();
    }

    /**
     * PmdValidator can prohibit public static methods.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void prohibitsPublicStaticMethods() throws Exception {
        new PmdAssert(
            "StaticPublicMethod.java",
            Matchers.is(false),
            Matchers.containsString(PmdValidatorTest.STATIC_METHODS)
        ).assertOk();
    }

    /**
     * PmdValidator can allow public static void main(String...args) method.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsPublicStaticMainMethod() throws Exception {
        new PmdAssert(
            "StaticPublicVoidMainMethod.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString(PmdValidatorTest.STATIC_METHODS)
            )
        ).assertOk();
    }

    /**
     * PmdValidator can allow JUnit public static methods marked with:<br>
     * BeforeClass annotation.<br> AfterClass annotation.<br>
     * Parameterized.Parameters annotation.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsJunitFrameworkPublicStaticMethods() throws Exception {
        new PmdAssert(
            "JunitStaticPublicMethods.java",
            Matchers.is(false),
            Matchers.allOf(
                Matchers.not(
                    Matchers.containsString(PmdValidatorTest.STATIC_METHODS)
                ),
                Matchers.containsString("UnitTestShouldIncludeAssert")
            )
        ).assertOk();
    }

    /**
     * PmdValidator can allow duplicate literals in annotations.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsDuplicateLiteralsInAnnotations() throws Exception {
        new PmdAssert(
            "AllowsDuplicateLiteralsInAnnotations.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("AvoidDuplicateLiterals")
            )
        ).assertOk();
    }

    /**
     * PmdValidator can allow record classes.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    @EnabledForJreRange(min = JRE.JAVA_21, max = JRE.JAVA_25)
    void allowRecordClasses() throws Exception {
        new PmdAssert(
            "RecordParsed.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString(PmdValidatorTest.STATIC_METHODS)
            )
        ).assertOk();
    }

    /**
     * PmdValidator checks swagger annotation.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsSwaggerAnnotations() throws Exception {
        new PmdAssert(
            "SwaggerApi.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("RuleSetReferenceId")
            )
        ).assertOk();
    }

    /**
     * PmdValidator can prohibit unicode characters in method names.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void prohibitsUnicodeCharactersInMethodNames() throws Exception {
        new PmdAssert(
            "UnicodeCharactersInMethodNames.java",
            Matchers.is(false),
            Matchers.containsString("MethodNamingConventions")
        ).assertOk();
    }

    /**
     * PmdValidator can recognise io.github.artsok RepeatedIfExceptionsTest
     * annotation as a test method, so that the class is not flagged with
     * TestClassWithoutTestCases.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void recognisesArtsokRepeatedIfExceptionsTest() throws Exception {
        new PmdAssert(
            "RepeatedIfExceptionsTest.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("TestClassWithoutTestCases")
            )
        ).assertOk();
    }

    /**
     * PmdValidator does not complain about a private static field of an inner
     * class that is referenced inside a lambda through a fully-qualified
     * outer-class path. Regression test for
     * https://github.com/yegor256/qulice/issues/1520
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsPrivateStaticFieldAccessedViaFullyQualifiedName()
        throws Exception {
        new PmdAssert(
            "UnusedPrivateFieldInLambda.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("(UnusedPrivateField)")
            )
        ).assertOk();
    }

    /**
     * PmdValidator still flags a private field that is never referenced at
     * all. Guard against the suppression in
     * {@link #allowsPrivateStaticFieldAccessedViaFullyQualifiedName()}
     * over-matching.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void reportsTrulyUnusedPrivateField() throws Exception {
        new PmdAssert(
            "UnusedPrivateFieldTrulyUnused.java",
            Matchers.any(Boolean.class),
            Matchers.containsString("(UnusedPrivateField)")
        ).assertOk();
    }

    /**
     * PmdValidator does not flag
     * {@code @SuppressWarnings("PMD.UnnecessaryWarningSuppression")} as
     * an unnecessary suppression of itself (Fix #1534). The rule cannot
     * suppress its own violations, so suppressing it has no effect and
     * should therefore not be reported as unused.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void doesNotCatchUnnecessaryWarningSuppressionOnItself()
        throws Exception {
        new PmdAssert(
            "UnnecessaryWarningSuppressionOnItself.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("UnnecessaryWarningSuppression")
            )
        ).assertOk();
    }

    /**
     * Check if UseStringIsEmptyRule not throws an NullPointerException when
     * found a pattern matching.
     */
    @Test
    void notThrowsAnNullPointerExceptionOnPatternMatching() {
        Assertions.assertDoesNotThrow(
            () -> new PmdAssert(
                "UseStringIsEmptyRuleFailsOnPatternMatching.java",
                new IsEqual<>(false),
                new StringContains("UnusedLocalVariable")
            ).assertOk()
        );
    }

    /**
     * PmdValidator does not report ArrayIsStoredDirectly when the varargs
     * parameter is wrapped in a method call or constructor before being
     * assigned to a field. Regression test for
     * https://github.com/yegor256/qulice/issues/1053.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsArrayIsStoredDirectlyWhenWrapped() throws Exception {
        new PmdAssert(
            "ArrayIsStoredDirectlyWrapped.java",
            Matchers.any(Boolean.class),
            Matchers.not(
                Matchers.containsString("(ArrayIsStoredDirectly)")
            )
        ).assertOk();
    }

    /**
     * PmdValidator still reports ArrayIsStoredDirectly when a varargs or
     * array parameter is assigned to a field directly. Guards the fix for
     * https://github.com/yegor256/qulice/issues/1053 from over-suppressing.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void reportsArrayIsStoredDirectlyWhenPlain() throws Exception {
        new PmdAssert(
            "ArrayIsStoredDirectlyPlain.java",
            Matchers.is(false),
            Matchers.containsString("(ArrayIsStoredDirectly)")
        ).assertOk();
    }

    /**
     * PmdValidator reports a missing &#64;Override annotation on a method
     * that implements an interface method. Regression test for
     * https://github.com/yegor256/qulice/issues/770.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void reportsMissingOverrideOnInterfaceImplementation() throws Exception {
        new PmdAssert(
            "MissingOverrideOnInterfaceImpl.java",
            Matchers.is(false),
            Matchers.containsString("(MissingOverride)")
        ).assertOk();
    }

    /**
     * PmdValidator reports LooseCoupling when a class declares fields,
     * constructor parameters or method return types using concrete
     * collection implementations such as {@code ConcurrentHashMap} or
     * {@code HashMap}. Regression test for
     * https://github.com/yegor256/qulice/issues/734.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void reportsConcreteCollectionTypes() throws Exception {
        new PmdAssert(
            "ConcreteCollectionTypes.java",
            Matchers.is(false),
            Matchers.allOf(
                Matchers.containsString("(LooseCoupling)"),
                Matchers.containsString("ConcurrentHashMap"),
                Matchers.containsString("HashMap")
            )
        ).assertOk();
    }

    /**
     * PmdValidator does not run analysis when every file is excluded. This
     * is the short-circuit fix for
     * https://github.com/yegor256/qulice/issues/759 — the source would
     * otherwise produce a violation, but the exclusion must prevent PMD
     * from being invoked on it at all.
     *
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void skipsAnalysisWhenAllFilesExcluded() throws Exception {
        final String file = "src/main/java/Main.java";
        final Environment env = new ExcludingEnvironment(
            new Environment.Mock()
                .withFile(file, "class Main { int x = 0; }")
        );
        MatcherAssert.assertThat(
            "Excluded files must not yield violations",
            new PmdValidator(env).validate(
                Collections.singletonList(new File(env.basedir(), file))
            ),
            Matchers.<Violation>empty()
        );
    }
}
