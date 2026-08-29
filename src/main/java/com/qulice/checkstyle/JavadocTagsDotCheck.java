/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Prohibit a trailing dot in the description of {@code @param} and
 * {@code @return} Javadoc tags.
 *
 * <p>For consistency, descriptions of these tags must not end with
 * a period.</p>
 *
 * <p>Valid:</p>
 *
 * <pre>
 * &#47;**
 *  * &#64;param text A string with contents. Cannot be null
 *  * &#64;return True when empty, false otherwise
 *  *&#47;
 * </pre>
 *
 * <p>Invalid:</p>
 *
 * <pre>
 * &#47;**
 *  * &#64;param text A string with contents. Cannot be null.
 *  * &#64;return True when empty, false otherwise.
 *  *&#47;
 * </pre>
 *
 * @since 0.24.1
 */
public final class JavadocTagsDotCheck extends AbstractCheck {

    /**
     * Default constructor.
     */
    public JavadocTagsDotCheck() {
        // nothing to initialize
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
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
        final int cend = JavadocTagsDotCheck.findTrimmedTextUp(
            lines, ast.getLineNo() - 1, "*/"
        );
        final int cstart = JavadocTagsDotCheck.findTrimmedTextUp(
            lines, cend, "/**"
        );
        if (cstart >= 0 && cend > cstart) {
            this.inspect(lines, cstart, cend);
        }
    }

    private void inspect(final String[] lines, final int cstart, final int cend) {
        int tag = -1;
        for (int pos = cstart + 1; pos <= cend; pos += 1) {
            final String trimmed = lines[pos].trim();
            final boolean next = trimmed.startsWith("* @")
                || "*/".equals(trimmed);
            if (next && tag >= 0) {
                this.verify(lines, tag, pos - 1);
                tag = -1;
            }
            if (JavadocTagsDotCheck.isParamOrReturn(trimmed)) {
                tag = pos;
            }
        }
    }

    private void verify(final String[] lines, final int from, final int until) {
        for (int pos = until; pos >= from; pos -= 1) {
            final String content = JavadocTagsDotCheck.stripMarker(lines[pos]);
            if (!content.isEmpty()) {
                if (content.endsWith(".")) {
                    this.log(
                        pos + 1,
                        "No dot allowed at the end of a '@param' or '@return' Javadoc tag"
                    );
                }
                break;
            }
        }
    }

    private static boolean isParamOrReturn(final String trimmed) {
        final String tag;
        if (trimmed.startsWith("* @")) {
            tag = trimmed.substring("* @".length());
        } else {
            tag = "";
        }
        return tag.startsWith("param ")
            || tag.startsWith("return ")
            || "return".equals(tag);
    }

    private static String stripMarker(final String line) {
        String result = line.trim();
        if (result.startsWith("*")) {
            result = result.substring(1).trim();
        }
        return result;
    }

    private static int findTrimmedTextUp(
        final String[] lines,
        final int start,
        final String text
    ) {
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
