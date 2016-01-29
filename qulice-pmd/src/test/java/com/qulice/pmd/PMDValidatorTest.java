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
package com.qulice.pmd;

import com.google.common.base.Joiner;
import com.jcabi.matchers.RegexMatchers;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.io.StringWriter;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link PMDValidator} class.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class PMDValidatorTest {

    /**
     * Error message for forbidding access to static fields
     * other than with a static way.
     * @checkstyle LineLength (2 lines)
     */
    private static final String STATIC_ACCESS = "%s\\[\\d+-\\d+\\]: Static fields should be accessed in a static way \\[CLASS_NAME.FIELD_NAME\\]\\.";

    /**
     * Error message for forbidding instructions inside a constructor
     * other than field initialization or call to other contructors.
     * @checkstyle LineLength (2 lines)
     */
    private static final String CODE_IN_CON = "%s\\[\\d+-\\d+\\]: Only field initialization or call to other contructors in a constructor";

    /**
     * Pattern for non-constructor field initialization.
     * @checkstyle LineLength (2 lines)
     */
    private static final String NO_CON_INIT = "%s\\[\\d+-\\d+\\]: Avoid doing field initialization outside constructor.";

    /**
     * Pattern multiple constructors field initialization.
     * @checkstyle LineLength (2 lines)
     */
    private static final String MULT_CON_INIT = "%s\\[\\d+-\\d+\\]: Avoid field initialization in several constructors.";

    /**
     * Template for string inside brackets.
     */
    private static final String BRACKETS = "(%s)";

    /**
     * PMDValidator can find violations in Java file(s).
     * @throws Exception If something wrong happens inside.
     */
    @Test(expected = ValidationException.class)
    public void findsProblemsInJavaFiles() throws Exception {
        final Environment env = new Environment.Mock()
            .withFile("src/main/java/Main.java", "class Main { int x = 0; }");
        final Validator validator = new PMDValidator();
        validator.validate(env);
    }

    /**
     * PMDValidator can understand method references.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void understandsMethodReferences() throws Exception {
        // @checkstyle MultipleStringLiteralsCheck (10 lines)
        final Environment env = new Environment.Mock().withFile(
            "src/main/java/Other.java",
            Joiner.on('\n').join(
                "import java.util.ArrayList;",
                "class Other {",
                "    public static void test() {",
                "        new ArrayList<String>().forEach(Other::other);",
                "    }",
                "    private static void other(String some) {}",
                "}"
            )
        );
        final StringWriter writer = new StringWriter();
        Logger.getRootLogger().addAppender(
            new WriterAppender(new SimpleLayout(), writer)
        );
        final Validator validator = new PMDValidator();
        boolean thrown = false;
        try {
            validator.validate(env);
        } catch (final ValidationException ex) {
            thrown = true;
        }
        MatcherAssert.assertThat(thrown, Matchers.is(Matchers.is(true)));
        MatcherAssert.assertThat(
            writer.toString(),
            Matchers.not(Matchers.containsString("(UnusedPrivateMethod)"))
        );
    }

    /**
     * PMDValidator does not think that constant is unused when it is used
     * just from the inner class.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void doesNotComplainAboutConstantsInInnerClasses() throws Exception {
        // @checkstyle MultipleStringLiteralsCheck (10 lines)
        final Environment env = new Environment.Mock().withFile(
            "src/main/java/foo/Foo.java",
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
        final StringWriter writer = new StringWriter();
        final Appender appender = new WriterAppender(
            new SimpleLayout(),
            writer
        );
        try {
            Logger.getRootLogger().addAppender(appender);
            new PMDValidator().validate(env);
            writer.flush();
            MatcherAssert.assertThat(
                writer.toString(),
                Matchers.allOf(
                    Matchers.not(Matchers.containsString("UnusedPrivateField")),
                    Matchers.containsString("No PMD violations found")
                )
            );
        } finally {
            Logger.getRootLogger().removeAppender(appender);
        }
    }

    /**
     * PMDValidator can allow field initialization when constructor is missing.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void allowsFieldInitializationWhenConstructorIsMissing()
        throws Exception {
        final String file = "FieldInitNoConstructor.java";
        new PMDAssert(
            file, Matchers.is(false),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PMDValidatorTest.NO_CON_INIT, file)
                )
            )
        ).validate();
    }

    /**
     * PMDValidator can forbid field initialization when constructor exists.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void forbidsFieldInitializationWhenConstructorExists()
        throws Exception {
        final String file = "FieldInitConstructor.java";
        new PMDAssert(
            file, Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PMDValidatorTest.NO_CON_INIT, file)
            )
        ).validate();
    }

    /**
     * PMDValidator can allow static field initialization when constructor
     * exists.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void allowsStaticFieldInitializationWhenConstructorExists()
        throws Exception {
        final String file = "StaticFieldInitConstructor.java";
        new PMDAssert(
            file, Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PMDValidatorTest.NO_CON_INIT, file)
                )
            )
        ).validate();
    }

    /**
     * PMDValidator can forbid field initialization in several constructors.
     * Only one constructor should do real work. Others - delegate to it.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void forbidsFieldInitializationInSeveralConstructors()
        throws Exception {
        final String file = "FieldInitSeveralConstructors.java";
        new PMDAssert(
            file, Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PMDValidatorTest.MULT_CON_INIT, file)
            )
        ).validate();
    }

    /**
     * PMDValidator can allow field initialization in one constructor.
     * Only one constructor should do real work. Others - delegate to it.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void allowsFieldInitializationInOneConstructor()
        throws Exception {
        final String file = "FieldInitOneConstructor.java";
        new PMDAssert(
            file, Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PMDValidatorTest.MULT_CON_INIT, file)
                )
            )
        ).validate();
    }

    /**
     * PMDValidator forbids unnecessary final modifier for methods.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void forbidsUnnecessaryFinalModifier()
        throws Exception {
        final String file = "UnnecessaryFinalModifier.java";
        new PMDAssert(
            file, Matchers.is(false),
            Matchers.containsString("Unnecessary final modifier")
        ).validate();
    }

    /**
     * PMDValidator forbid useless parentheses.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void forbidsUselessParentheses()
        throws Exception {
        final String file = "UselessParentheses.java";
        new PMDAssert(
            file, Matchers.is(false),
            Matchers.containsString("Useless parentheses")
        ).validate();
    }

    /**
     * PMDValidator forbids code in constructor
     * other than field initialization.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void forbidsCodeInConstructor()
        throws Exception {
        final String file = "CodeInConstructor.java";
        new PMDAssert(
            file, Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PMDValidatorTest.CODE_IN_CON, file)
            )
        ).validate();
    }

    /**
     * PMDValidator accepts calls to other constructors
     * or call to super class constructor in constructors.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void acceptsCallToConstructorInConstructor()
        throws Exception {
        final String file = "CallToConstructorInConstructor.java";
        new PMDAssert(
            file, Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PMDValidatorTest.CODE_IN_CON, file)
                )
            )
        ).validate();
    }

    /**
     * PMDValidator accepts calls to static fields
     * in a static way.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void acceptsCallToStaticFieldsInStaticWay()
        throws Exception {
        final String file = "StaticAccessToStaticFields.java";
        new PMDAssert(
            file, Matchers.is(true),
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PMDValidatorTest.STATIC_ACCESS, file)
                )
            )
        ).validate();
    }

    /**
     * PMDValidator forbids calls to static fields
     * in a non static way.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void forbidsCallToStaticFieldsInNonStaticWay()
        throws Exception {
        final String file = "NonStaticAccessToStaticFields.java";
        new PMDAssert(
            file, Matchers.is(false),
            RegexMatchers.containsPattern(
                String.format(PMDValidatorTest.STATIC_ACCESS, file)
            )
        ).validate();
    }

    /**
     * PMDValidator forbids non public clone methods (PMD rule
     * rulesets/java/clone.xml/CloneMethodMustBePublic).
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void forbidsNonPublicCloneMethod() throws Exception {
        new PMDAssert(
            "CloneMethodMustBePublic.java",
            Matchers.is(false),
            Matchers.containsString(
                String.format(
                    PMDValidatorTest.BRACKETS,
                    "CloneMethodMustBePublic"
                )
            )
        ).validate();
    }

    /**
     * PMDValidator forbids clone methods with return type not matching class
     * name (PMD rule
     * rulesets/java/clone.xml/CloneMethodReturnTypeMustMatchClassName).
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void forbidsCloneMethodReturnTypeNotMatchingClassName()
        throws Exception {
        new PMDAssert(
            "CloneMethodReturnTypeMustMatchClassName.java",
            Matchers.is(false),
            Matchers.containsString(
                String.format(
                    PMDValidatorTest.BRACKETS,
                    "CloneMethodReturnTypeMustMatchClassName"
                )
            )
        ).validate();
    }

    /**
     * PMDValidator forbids ternary operators that can be simplified (PMD rule
     * rulesets/java/basic.xml/SimplifiedTernary).
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void forbidsNonSimplifiedTernaryOperators()
        throws Exception {
        new PMDAssert(
            "SimplifiedTernary.java",
            Matchers.is(false),
            Matchers.containsString(
                String.format(
                    PMDValidatorTest.BRACKETS,
                    "SimplifiedTernary"
                )
            )
        ).validate();
    }

    /**
     * PMDValidator prohibit old style junit assertion in import block like
     * import static org.junit.Assert.assert*
     * import static junit.framework.Assert.assert*.
     *
     * Custom Rule {@link com.qulice.pmd.rules.ProhibitOldStyleAssertionsRule}
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void prohibitStaticImportsOldStyleAssertionsInTests()
        throws Exception {
        final String file = "OldStyleJUnitAssertion.java";
        new PMDAssert(
            file, Matchers.is(false),
            Matchers.containsString("Avoid to use old style assertions")
        ).validate();
    }

}
