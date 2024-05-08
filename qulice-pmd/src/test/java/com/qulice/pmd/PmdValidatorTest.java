/*
 * Copyright (c) 2011-2024 Qulice.com
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
package com.qulice.pmd;

import com.google.common.base.Joiner;
import com.jcabi.matchers.RegexMatchers;
import com.qulice.pmd.rules.ProhibitPlainJunitAssertionsRule;
import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.condition.OS;

/**
 * Test case for {@link PmdValidator} class.
 * @since 0.3
 */
@SuppressWarnings("PMD.TooManyMethods")
final class PmdValidatorTest {

    /**
     * Error message for forbidding access to static fields
     * other than with a static way.
     * @checkstyle LineLengthCheck (40 lines)
     */
    private static final String STATIC_ACCESS =
        "%s\\[\\d+-\\d+\\]: Static fields should be accessed in a static way \\[CLASS_NAME.FIELD_NAME\\]\\.";

    /**
     * Error message for forbidding access to static members
     * via instance reference using 'this' keyword.
     */
    private static final String STATIC_VIA_THIS =
        "%s\\[\\d+-\\d+\\]: Static members should be accessed in a static way \\[CLASS_NAME.FIELD_NAME\\], not via instance reference.";

    /**
     * Error message for forbidding instructions inside a constructor
     * other than field initialization or call to other constructors.
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
     * Pattern using plain JUnit assertions.
     */
    private static final String PLAIN_ASSERTIONS =
        "Avoid using Plain JUnit assertions";

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
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void understandsMethodReferences() throws Exception {
        final String file = "UnderstandsMethodReferences.java";
        new PmdAssert(
            file,
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("(UnusedPrivateMethod)")
            )
        ).validate();
    }

    /**
     * PmdValidator does not think that constant is unused when it is used
     * just from the inner class.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
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
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsFieldInitializationWhenConstructorIsMissing()
        throws Exception {
        final String file = "FieldInitNoConstructor.java";
        new PmdAssert(
            file, Matchers.is(false),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PmdValidatorTest.NO_CON_INIT, file)
                )
            )
        ).validate();
    }

    /**
     * PmdValidator can forbid field initialization when constructor exists.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsFieldInitializationWhenConstructorExists()
        throws Exception {
        final String file = "FieldInitConstructor.java";
        new PmdAssert(
            file, Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdValidatorTest.NO_CON_INIT, file)
            )
        ).validate();
    }

    /**
     * PmdValidator can allow static field initialization when constructor
     * exists.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsStaticFieldInitializationWhenConstructorExists()
        throws Exception {
        final String file = "StaticFieldInitConstructor.java";
        new PmdAssert(
            file, Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PmdValidatorTest.NO_CON_INIT, file)
                )
            )
        ).validate();
    }

    /**
     * PmdValidator can forbid field initialization in several constructors.
     * Only one constructor should do real work. Others - delegate to it.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsFieldInitializationInSeveralConstructors()
        throws Exception {
        final String file = "FieldInitSeveralConstructors.java";
        new PmdAssert(
            file, Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdValidatorTest.MULT_CON_INIT, file)
            )
        ).validate();
    }

    /**
     * PmdValidator can allow field initialization in one constructor.
     * Only one constructor should do real work. Others - delegate to it.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsFieldInitializationInOneConstructor()
        throws Exception {
        final String file = "FieldInitOneConstructor.java";
        new PmdAssert(
            file, Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PmdValidatorTest.MULT_CON_INIT, file)
                )
            )
        ).validate();
    }

    /**
     * PmdValidator forbids unnecessary final modifier for methods.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsUnnecessaryFinalModifier()
        throws Exception {
        final String file = "UnnecessaryFinalModifier.java";
        new PmdAssert(
            file, Matchers.is(false),
            Matchers.containsString("Unnecessary modifier 'final'")
        ).validate();
    }

    /**
     * PmdValidator forbid useless parentheses.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsUselessParentheses()
        throws Exception {
        final String file = "UselessParentheses.java";
        new PmdAssert(
            file, Matchers.is(false),
            Matchers.containsString("Useless parentheses")
        ).validate();
    }

    /**
     * PmdValidator forbids code in constructor
     * other than field initialization.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsCodeInConstructor()
        throws Exception {
        final String file = "CodeInConstructor.java";
        new PmdAssert(
            file, Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdValidatorTest.CODE_IN_CON, file)
            )
        ).validate();
    }

    /**
     * PmdValidator allows lambda in constructor.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsLambdaInConstructor()
        throws Exception {
        final String file = "LambdaInConstructor.java";
        new PmdAssert(
            file, new IsEqual<>(true),
            new IsNot<>(
                RegexMatchers.containsPattern(
                    String.format(PmdValidatorTest.CODE_IN_CON, file)
                )
            )
        ).validate();
    }

    /**
     * PmdValidator forbids usage of Files.createFile in tests.
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
        ).validate();
    }

    /**
     * PmdValidator allows usage of Files.createFile outside of tests.
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
        ).validate();
    }

    /**
     * PmdValidator accepts calls to other constructors
     * or call to super class constructor in constructors.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void acceptsCallToConstructorInConstructor()
        throws Exception {
        final String file = "CallToConstructorInConstructor.java";
        new PmdAssert(
            file, Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PmdValidatorTest.CODE_IN_CON, file)
                )
            )
        ).validate();
    }

    /**
     * PmdValidator accepts calls to static fields
     * in a static way.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void acceptsCallToStaticFieldsInStaticWay()
        throws Exception {
        final String file = "StaticAccessToStaticFields.java";
        new PmdAssert(
            file, Matchers.is(true),
            Matchers.allOf(
                Matchers.not(
                    RegexMatchers.containsPattern(
                        String.format(PmdValidatorTest.STATIC_ACCESS, file)
                    )
                ),
                Matchers.not(
                    RegexMatchers.containsPattern(
                        String.format(PmdValidatorTest.STATIC_VIA_THIS, file)
                    )
                )
            )
        ).validate();
    }

    /**
     * PmdValidator forbids calls to static fields directly
     * in a non static way.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsCallToStaticFieldsDirectly()
        throws Exception {
        final String file = "DirectAccessToStaticFields.java";
        new PmdAssert(
            file, Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdValidatorTest.STATIC_ACCESS, file)
            )
        ).validate();
    }

    /**
     * PmdValidator forbids calls to static fields
     * in a non static way via instance reference.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsCallToStaticFieldsViaThis()
        throws Exception {
        final String file = "AccessToStaticFieldsViaThis.java";
        new PmdAssert(
            file, Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdValidatorTest.STATIC_VIA_THIS, file)
            )
        ).validate();
    }

    /**
     * PmdValidator forbids calls to static methods
     * in a non static way via instance reference.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void forbidsCallToStaticMethodsViaThis()
        throws Exception {
        final String file = "AccessToStaticMethodsViaThis.java";
        new PmdAssert(
            file, Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PmdValidatorTest.STATIC_VIA_THIS, file)
            )
        ).validate();
    }

    /**
     * PmdValidator forbids non public clone methods (PMD rule
     * rulesets/java/clone.xml/CloneMethodMustBePublic).
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
        ).validate();
    }

    /**
     * PmdValidator forbids clone methods with return type not matching class
     * name (PMD rule
     * rulesets/java/clone.xml/CloneMethodReturnTypeMustMatchClassName).
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
        ).validate();
    }

    /**
     * PmdValidator forbids ternary operators that can be simplified (PMD rule
     * rulesets/java/basic.xml/SimplifiedTernary).
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
        ).validate();
    }

    /**
     * PmdValidator can prohibit plain JUnit assertion in import block like
     * import static org.junit.Assert.assert*
     * import static junit.framework.Assert.assert*.
     *
     * Custom Rule {@link ProhibitPlainJunitAssertionsRule}
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void prohibitsStaticImportsPlainAssertionsInTests()
        throws Exception {
        final String file = "PlainJUnitAssertionStaticImportBlock.java";
        new PmdAssert(
            file, Matchers.is(false),
            Matchers.containsString(
                PmdValidatorTest.PLAIN_ASSERTIONS
            )
        ).validate();
    }

    /**
     * PmdValidator can prohibit plain JUnit assertion in test methods like
     * Assert.assertEquals.
     *
     * Custom Rule {@link ProhibitPlainJunitAssertionsRule}
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void prohibitsPlainJunitAssertionsInTestMethods()
        throws Exception {
        final String file = "PlainJUnitAssertionTestMethod.java";
        new PmdAssert(
            file, Matchers.is(false),
            Matchers.containsString(
                PmdValidatorTest.PLAIN_ASSERTIONS
            )
        ).validate();
    }

    /**
     * PmdValidator can allow Assert.fail().
     *
     * Custom Rule {@link ProhibitPlainJunitAssertionsRule}
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsAssertFail()
        throws Exception {
        final String file = "AllowAssertFail.java";
        new PmdAssert(
            file, Matchers.is(true),
            Matchers.not(
                Matchers.containsString(
                    PmdValidatorTest.PLAIN_ASSERTIONS
                )
            )
        ).validate();
    }

    /**
     * PmdValidator can allow non-static, non-transient fields.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsNonTransientFields() throws Exception {
        final String file = "AllowNonTransientFields.java";
        new PmdAssert(
            file, Matchers.is(true),
            Matchers.not(
                Matchers.containsString(
                    "Found non-transient, non-static member."
                )
            )
        ).validate();
    }

    /**
     * PmdValidator can prohibit public static methods.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void prohibitsPublicStaticMethods() throws Exception {
        new PmdAssert(
            "StaticPublicMethod.java",
            Matchers.is(false),
            Matchers.containsString(PmdValidatorTest.STATIC_METHODS)
        ).validate();
    }

    /**
     * PmdValidator can allow public static void main(String...args) method.
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
        ).validate();
    }

    /**
     * PmdValidator can allow JUnit public static methods marked with:<br>
     * BeforeClass annotation.<br>
     * AfterClass annotation.<br>
     * Parameterized.Parameters annotation.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowsJunitFrameworkPublicStaticMethods() throws Exception {
        new PmdAssert(
            "JunitStaticPublicMethods.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString(PmdValidatorTest.STATIC_METHODS)
            )
        ).validate();
    }

    /**
     * PmdValidator can allow duplicate literals in annotations.
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
        ).validate();
    }

    /**
     * PmdValidator can allow only package private methods,
     * annotated by @Test, @RepeatedTest, @TestFactory, @TestTemplate or @ParameterizedTest.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void testShouldBePackagePrivate() throws Exception {
        new PmdAssert(
            "TestShouldBePackagePrivate.java",
            Matchers.is(false),
            Matchers.containsString("JUnit5TestShouldBePackagePrivate")
        ).validate();
    }

    /**
     * PmdValidator can allow only final JUnit3 test classes.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowJunitThirdTestClassToBeFinal() throws Exception {
        new PmdAssert(
            "Junit3TestClassShouldBeFinal.java",
            Matchers.is(false),
            Matchers.containsString("JUnitTestClassShouldBeFinal")
        ).validate();
    }

    /**
     * PmdValidator can allow only final JUnit4 test classes.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowJunitFourthTestClassToBeFinal() throws Exception {
        new PmdAssert(
            "Junit4TestClassShouldBeFinal.java",
            Matchers.is(false),
            Matchers.containsString("JUnitTestClassShouldBeFinal")
        ).validate();
    }

    /**
     * PmdValidator can allow only final JUnit5 test classes.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowJunitFifthTestClassToBeFinal() throws Exception {
        new PmdAssert(
            "Junit5TestClassShouldBeFinal.java",
            Matchers.is(false),
            Matchers.containsString("JUnitTestClassShouldBeFinal")
        ).validate();
    }

    /**
     * PmdValidator can allow only final Junit test classes.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    void allowJunitTestClassToBeFinal() throws Exception {
        new PmdAssert(
            "JunitTestClassIsFinal.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString("JUnitTestClassShouldBeFinal")
            )
        ).validate();
    }

    /**
     * PmdValidator can allow record classes.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    @EnabledForJreRange(min = JRE.JAVA_14, max = JRE.JAVA_21)
    void allowRecordClasses() throws Exception {
        new PmdAssert(
            "RecordParsed.java",
            Matchers.is(true),
            Matchers.not(
                Matchers.containsString(PmdValidatorTest.STATIC_METHODS)
            )
        ).validate();
    }

    /**
     * PmdValidator checks swagger annotation.
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
        ).validate();
    }

    /**
     * PmdValidator can prohibit unicode characters in method names.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void prohibitsUnicodeCharactersInMethodNames() throws Exception {
        new PmdAssert(
            "UnicodeCharactersInMethodNames.java",
            Matchers.is(false),
            Matchers.containsString("MethodNamingConventions")
        ).validate();
    }
}
