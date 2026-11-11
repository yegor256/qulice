/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Check for empty lines inside Javadoc.
 *
 * <p>You can't have an empty line at the beginning or at the end of Javadoc,
 * and two consecutive empty lines are not allowed anywhere inside it.</p>
 *
 * <p>The following red lines in class Javadoc will be reported as violations.</p>
 * <pre>
 * &#47;**
 *  <span style="color:red" >*</span>
 *  * This is my class.
 *  *
 *  <span style="color:red" >*</span>
 *  * More text.
 *  <span style="color:red" >*</span>
 *  *&#47;
 * public final class Foo {
 *     // ...
 * </pre>
 *
 * @since 0.17
 */
public final class JavadocEmptyLineCheck extends AbstractCheck {

    /**
     * Default constructor.
     */
    public JavadocEmptyLineCheck() {
        // nothing to initialize
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.PACKAGE_DEF,
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ANNOTATION_DEF,
            TokenTypes.ANNOTATION_FIELD_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ENUM_CONSTANT_DEF,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.METHOD_DEF,
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
        final int current = ast.getLineNo();
        final int start =
            JavadocEmptyLineCheck.findCommentStart(lines, current) + 1;
        if (JavadocEmptyLineCheck.isNodeHavingJavadoc(ast, start)
            && start < lines.length) {
            if (JavadocEmptyLineCheck.isJavadocLineEmpty(lines[start])) {
                this.log(start + 1, "Empty Javadoc line at the beginning");
            }
            final int end =
                JavadocEmptyLineCheck.findCommentEnd(lines, current) - 1;
            if (end >= start
                && JavadocEmptyLineCheck.isJavadocLineEmpty(lines[end])) {
                this.log(end + 1, "Empty Javadoc line at the end");
            }
            for (int pos = start + 1; pos <= end; pos += 1) {
                if (JavadocEmptyLineCheck.isJavadocLineEmpty(lines[pos])
                    && JavadocEmptyLineCheck.isJavadocLineEmpty(lines[pos - 1])
                ) {
                    this.log(pos + 1, "Two consecutive empty Javadoc lines");
                }
            }
        }
    }

    private static boolean isJavadocLineEmpty(final String line) {
        return "*".equals(line.trim());
    }

    private static boolean isNodeHavingJavadoc(final DetailAST node,
        final int start) {
        return start > getLineNoOfPreviousNode(node);
    }

    private static int getLineNoOfPreviousNode(final DetailAST node) {
        int start = 0;
        final DetailAST previous = node.getPreviousSibling();
        if (previous != null) {
            start = previous.getLineNo();
        }
        return start;
    }

    private static int findCommentStart(final String[] lines, final int start) {
        return JavadocEmptyLineCheck.findTrimmedTextUp(lines, start, "/**");
    }

    private static int findCommentEnd(final String[] lines, final int start) {
        int found = -1;
        for (int pos = start - 1; pos >= 0; pos -= 1) {
            final String trimmed = lines[pos].trim();
            if ("*/".equals(trimmed) || "**/".equals(trimmed)) {
                found = pos;
                break;
            }
        }
        return found;
    }

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
