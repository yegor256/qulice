/**
 * Copyright (c) 2011-2014, Qulice.com
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
import java.io.File;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test case for teamed/qulice#113 and checkstyle/checkstyle#90 issues,
 * the issues in question being checkstyle RedundantThrowsCheck failing
 * in case of javadoc @throws clause featuring a descendant of
 * ${@link Exception} implemented as an inner class in an outer
 * class different from the one containing @throws in question.
 * Should fail on qulice 0.3.1, pass on qulice 0.10+
 * (Requires checkstyle 5.7+ to pass)
 *
 * @author Eugene Bukhtin (maurezen@gmail.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class RedundantThrowsCheckTest {

    /**
     * Name of property to set to change location of the license.
     */
    private static final String LICENSE_PROP = "license";

    /**
     * License rule.
     *
     * @checkstyle VisibilityModifierCheck (5 lines)
     */
    @Rule
    public final transient LicenseRule rule = new LicenseRule();

    /**
     * Tries to pass RedundantThrowsCheck with a test class with
     * inner Exception descendant in another class.
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void passesRedundantThrowsCheckWithInnerExceptionInAnotherClass()
        throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule
            .savePackageInfo(new File(mock.basedir(), "src/main/java/foo"))
            .withLines(new String[]{"HELLO"})
            .withEol("\n")
            .file();
        final String parent = Joiner.on("\n").join(
            "/**",
            " * HELLO",
            " */",
            "package foo;",
            "/**",
            " * A.",
            " * @author Eugene Bukhtin (maurezen@gmail.com)",
            " * @version $Id$",
            " */",
            "public class A {",
            "    /**",
            "     * Test illustration method.",
            "     * @throws E no problem here",
            "     */",
            "    public final void mthd() throws E {",
            "        throw new E(\"just a demo\");",
            "    }",
            "",
            "    /**",
            "     * E.",
            "     */",
            "    public static class E extends Exception {",
            "        /**",
            "         * Ctor.",
            "         * @param message Message",
            "         */",
            "        public E(final String message) {",
            "            super(message);",
            "        }",
            "    }",
            "}",
            ""
        );
        final String valid = Joiner.on("\n").join(
            "/**",
            " * HELLO",
            " */",
            "package foo;",
            "/**",
            " * B.",
            " * @author Eugene Bukhtin (maurezen@gmail.com)",
            " * @version $Id$",
            " */",
            "public class B extends A {",
            "    /**",
            "     * Test illustration method.",
            "     * @throws E THIS CAUSES THE PROBLEM",
            "     */",
            "    public final void method() throws E {",
            "        throw new E(\"just a demo\");",
            "    }",
            "}",
            ""
        );
        final Environment env = mock
            .withParam(
                RedundantThrowsCheckTest.LICENSE_PROP,
                this.toURL(license)
        )
            .withFile("src/main/java/foo/A.java", parent)
            .withFile("src/main/java/foo/B.java", valid);
        new CheckstyleValidator().validate(env);
    }

    /**
     * Tries to pass RedundantThrowsCheck with a test class with
     * inner Exception descendant in the same class.
     *
     * @throws Exception If something wrong happens inside
     */
    @Test
    public void passesRedundantThrowsCheckWithInnerExceptionInSameClass()
        throws Exception {
        final Environment.Mock mock = new Environment.Mock();
        final File license = this.rule
            .savePackageInfo(new File(mock.basedir(), "src/main/java/foo"))
            .withLines(new String[]{"HELLO"})
            .withEol("\n")
            .file();
        final String valid = Joiner.on("\n").join(
            "/**",
            " * HELLO",
            " */",
            "package foo;",
            "/**",
            " * A.",
            " * @author Eugene Bukhtin (maurezen@gmail.com)",
            " * @version $Id$",
            " */",
            "public class A {",
            "    /**",
            "     * Test illustration method.",
            "     * @throws E no problem here",
            "     */",
            "    public final void mthd() throws E {",
            "        throw new E(\"just a demo\");",
            "    }",
            "",
            "    /**",
            "     * E.",
            "     */",
            "    public static class E extends Exception {",
            "        /**",
            "         * Ctor.",
            "         * @param message Message",
            "         */",
            "        public E(final String message) {",
            "            super(message);",
            "        }",
            "    }",
            "}",
            ""
        );
        final Environment env = mock
            .withParam(
                RedundantThrowsCheckTest.LICENSE_PROP,
                this.toURL(license)
        )
            .withFile("src/main/java/foo/A.java", valid);
        new CheckstyleValidator().validate(env);
    }

    /**
     * Convert file name to URL.
     *
     * @param file The file
     * @return The URL
     */
    private String toURL(final File file) {
        return String.format("file:%s", file);
    }
}
