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
package com.qulice.pmd;

import com.google.common.base.Joiner;
import com.jcabi.matchers.RegexMatchers;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.io.StringWriter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link PMDValidator} class.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class PMDValidatorTest {

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
        MatcherAssert.assertThat(thrown, Matchers.is(true));
        MatcherAssert.assertThat(
            writer.toString(),
            Matchers.not(Matchers.containsString("(UnusedPrivateMethod)"))
        );
    }

    /**
     * PMDValidator can allow field initialization when constructor is missing.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void allowsFieldInitializationWhenConstructorIsMissing()
        throws Exception {
        final String file = "FieldInitNoConstructor.java";
        this.validatePMD(
            file, false,
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PMDValidatorTest.NO_CON_INIT, file)
                )
            )
        );
    }

    /**
     * PMDValidator can forbid field initialization when constructor exists.
     * @throws Exception If something wrong happens inside.
     */
    @Test
    public void forbidsFieldInitializationWhenConstructorExists()
        throws Exception {
        final String file = "FieldInitConstructor.java";
        this.validatePMD(
            file, false,
            RegexMatchers.containsPattern(
                String.format(PMDValidatorTest.NO_CON_INIT, file)
            )
        );
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
        this.validatePMD(
            file, true,
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PMDValidatorTest.NO_CON_INIT, file)
                )
            )
        );
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
        this.validatePMD(
            file, false,
            RegexMatchers.containsPattern(
                String.format(PMDValidatorTest.MULT_CON_INIT, file)
            )
        );
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
        this.validatePMD(
            file, true,
            Matchers.not(
                RegexMatchers.containsPattern(
                    String.format(PMDValidatorTest.MULT_CON_INIT, file)
                )
            )
        );
    }

    /**
     * Validates that PMD reported given violation.
     * @param file File to check.
     * @param result Expected validation result (true if valid).
     * @param matcher Matcher to call on checkstyle output.
     * @throws Exception In case of error
     */
    private void validatePMD(final String file, final boolean result,
        final Matcher<String> matcher) throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final StringWriter writer = new StringWriter();
        final WriterAppender appender =
            new WriterAppender(new SimpleLayout(), writer);
        try {
            Logger.getRootLogger().addAppender(
                appender
            );
            final Environment env = mock.withFile(
                String.format("src/main/java/foo/%s", file),
                IOUtils.toString(
                    this.getClass().getResourceAsStream(file)
                )
            );
            boolean valid = true;
            try {
                new PMDValidator().validate(env);
            } catch (final ValidationException ex) {
                valid = false;
            }
            MatcherAssert.assertThat(valid, Matchers.is(result));
            MatcherAssert.assertThat(writer.toString(), matcher);
        } finally {
            Logger.getRootLogger().removeAppender(appender);
        }
    }
}
