/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import org.cactoos.text.Sub;

/**
 * Check indents in multi line JavaDoc tags.
 *
 * <p>This is how you should format javadoc tags that need a few lines:
 *
 * <pre>
 * &#47;**
 *  * This is my new method.
 *  * &#64;param text Some text information, provided to the
 *  *  method by another class
 *  * &#64;todo #123 I will implement it later, when more information
 *  *  come to light and I have documentation supplied by
 *  *  AAA team in the office accross the street
 *  *&#47;
 * public void func() {
 *     // ...
 * }
 * </pre>
 *
 * <p>Keep in mind that all free-text information should go <b>before</b>
 * javadoc tags, or else it will treated as part of the latest tag and
 * qulice will complain.
 *
 * @since 0.3
 */
public final class MultilineJavadocTagsCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.PACKAGE_DEF,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final String[] lines = this.getLines();
        final int start = ast.getLineNo();
        final int cstart =
            MultilineJavadocTagsCheck.findCommentStart(lines, start) + 1;
        final int cend =
            MultilineJavadocTagsCheck.findCommentEnd(lines, start) - 1;
        if (cend >= cstart && cstart >= 0) {
            this.checkJavaDoc(lines, cstart, cend);
        } else {
            this.log(0, "Can't find method comment");
        }
    }

    /**
     * Checks method's Java Doc for satisfy indentation rules.
     * @param lines Code of the whole class.
     * @param start Start line of the Java Doc.
     * @param end End line of the Java Doc.
     */
    @SuppressWarnings("PMD.InefficientEmptyStringCheck")
    private void checkJavaDoc(final String[] lines, final int start,
        final int end) {
        boolean tagged = false;
        int index = -1;
        for (int current = start; current <= end; current += 1) {
            final String line = lines[current];
            if (line.contains("* @")) {
                tagged = true;
                index = line.indexOf('@');
            } else {
                if (tagged) {
                    final int comment = line.indexOf('*');
                    final String sub = new Sub(
                        line, comment + 1, index + 1
                    ).toString();
                    final String ext = new Sub(
                        line, comment + 1, index + 2
                    ).toString();
                    if (!sub.trim().isEmpty() || ext.trim().isEmpty()) {
                        this.log(
                            current + 1,
                            "Should contain one indentation space"
                        );
                    }
                }
            }
        }
    }

    /**
     * Find javadoc starting comment.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @return Line number with found starting comment or -1 otherwise.
     */
    private static int findCommentStart(final String[] lines, final int start) {
        return MultilineJavadocTagsCheck.findTrimmedTextUp(lines, start, "/**");
    }

    /**
     * Find javadoc ending comment.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @return Line number with found ending comment, or -1 if it wasn't found.
     */
    private static int findCommentEnd(final String[] lines, final int start) {
        return MultilineJavadocTagsCheck.findTrimmedTextUp(lines, start, "*/");
    }

    /**
     * Find a text in lines, by going up.
     * @param lines List of lines to check.
     * @param start Start searching from this line number.
     * @param text Text to find.
     * @return Line number with found text, or -1 if it wasn't found.
     */
    private static int findTrimmedTextUp(final String[] lines,
        final int start, final String text) {
        int found = -1;
        for (int pos = start - 1; pos >= 0; pos -= 1) {
            if (lines[pos].trim().equals(text)) {
                found = pos;
                break;
            }
        }
        return found;
    }
}
