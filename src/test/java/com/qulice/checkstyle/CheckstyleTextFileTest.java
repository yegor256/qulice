/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.qulice.spi.Environment;
import com.qulice.spi.Violation;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link CheckstyleValidator} covering checks that apply to
 * non-Java text files and whitespace before closing braces.
 * @since 0.25.0
 */
final class CheckstyleTextFileTest {

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

    /**
     * CheckstyleValidator reports a tab character in a non-Java text file
     * such as JavaScript. See https://github.com/yegor256/qulice/issues/521.
     * @throws Exception when error.
     */
    @Test
    void rejectsTabInNonJavaTextFile() throws Exception {
        final String file = "script.js";
        MatcherAssert.assertThat(
            "Tab character in .js file is not reported",
            this.runValidationWithContent(
                file,
                "\tconsole.log(\"Hello World\");".concat(String.valueOf('\n'))
            ),
            Matchers.hasItem(
                new ViolationMatcher(
                    "tab", file, "1", "FileTabCharacterCheck"
                )
            )
        );
    }

    /**
     * CheckstyleValidator reports missing final newline in a non-Java text
     * file such as Markdown. See https://github.com/yegor256/qulice/issues/521.
     * @throws Exception when error.
     */
    @Test
    void rejectsMissingNewlineInNonJavaTextFile() throws Exception {
        final String file = "README.md";
        MatcherAssert.assertThat(
            "Missing final newline in .md file is not reported",
            this.runValidationWithContent(
                file,
                "# Title".concat(String.valueOf('\n')).concat("No newline at end")
            ),
            Matchers.hasItem(
                new ViolationMatcher(
                    "File does not end with a newline.", file, "",
                    "NewlineAtEndOfFileCheck"
                )
            )
        );
    }

    /**
     * CheckstyleValidator rejects empty lines before closing braces.
     * See https://github.com/yegor256/qulice/issues/710.
     * @throws Exception when error.
     */
    @Test
    void rejectsEmptyLineBeforeClosingBrace() throws Exception {
        final String file = "EmptyLineBeforeBrace.java";
        MatcherAssert.assertThat(
            "Empty line before closing brace is not reported",
            this.runValidationWithContent(
                file,
                new IoCheckedText(
                    new Joined(
                        String.valueOf('\n'),
                        "package foo;",
                        "public final class EmptyLineBeforeBrace {",
                        "    public void foo() {",
                        "        int x = 1;",
                        "",
                        "    }",
                        "}",
                        ""
                    )
                ).asString()
            ),
            Matchers.hasItem(
                new ViolationMatcher(
                    "Empty line before closing brace is not allowed",
                    file, "", "RegexpMultilineCheck"
                )
            )
        );
    }

    /**
     * CheckstyleValidator does not report a false positive when a closing
     * brace immediately follows a non-empty line.
     * See https://github.com/yegor256/qulice/issues/710.
     * @throws Exception when error.
     */
    @Test
    void acceptsNoEmptyLineBeforeClosingBrace() throws Exception {
        final String file = "NoEmptyLineBeforeBrace.java";
        MatcherAssert.assertThat(
            "Absence of empty line before closing brace was reported",
            this.runValidationWithContent(
                file,
                new IoCheckedText(
                    new Joined(
                        String.valueOf('\n'),
                        "package foo;",
                        "public final class NoEmptyLineBeforeBrace {",
                        "    public void foo() {",
                        "        int x = 1;",
                        "    }",
                        "}",
                        ""
                    )
                ).asString()
            ),
            Matchers.not(
                Matchers.hasItem(
                    new ViolationMatcher(
                        "Empty line before closing brace is not allowed",
                        file, "", "RegexpMultilineCheck"
                    )
                )
            )
        );
    }

    /**
     * Runs Checkstyle validation over a file whose content is supplied
     * in-place (as opposed to loaded from a resource).
     * @param file Name of the file to check
     * @param content Bytes to write as the file content
     * @return Violations reported by the validator
     * @throws IOException If some IO problem
     */
    private Collection<Violation> runValidationWithContent(final String file,
        final String content) throws IOException {
        final Environment.Mock mock = new Environment.Mock();
        final Environment env = mock.withParam(
            CheckstyleTextFileTest.LICENSE_PROP,
            String.format(
                "file:%s",
                this.rule.savePackageInfo(
                    new File(mock.basedir(), CheckstyleTextFileTest.DIRECTORY)
                ).withLines(CheckstyleTextFileTest.LICENSE)
                    .withEol(String.valueOf('\n')).file()
            )
        ).withFile(String.format("src/main/resources/%s", file), content);
        return new CheckstyleValidator(env).validate(env.files(file));
    }
}
