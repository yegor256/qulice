/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Check for the empty Javadoc line before the group of at-clauses.
 *
 * <p>The group of at-clauses ({@code @param}, {@code @return},
 * {@code @since}, {@code @throws} and the rest) must always be separated
 * from the description above it by an empty Javadoc line, no matter how
 * many paragraphs that description has. This holds for every Javadoc
 * block: packages, classes, interfaces, enums, enum constants,
 * annotations, annotation fields, records, fields, constructors and
 * methods. See
 * <a href="https://github.com/yegor256/qulice/issues/1810">#1810</a>.</p>
 *
 * <p>The following Javadoc will be reported as a violation, since its
 * description touches the first at-clause:</p>
 * <pre>
 * &#47;**
 *  * Just one line here.
 *  <span style="color:red" >* &#64;since 0.1</span>
 *  *&#47;
 * </pre>
 *
 * <p>And this is how it should be written instead:</p>
 * <pre>
 * &#47;**
 *  * Just one line here.
 *  *
 *  * &#64;since 0.1
 *  *&#47;
 * </pre>
 *
 * <p>A Javadoc block with no at-clauses at all, and a block whose very
 * first line already is an at-clause, are both left alone.</p>
 *
 * @since 0.27.0
 */
public final class JavadocEmptyLineBeforeTagCheck extends AbstractCheck {

    /**
     * Default constructor.
     */
    public JavadocEmptyLineBeforeTagCheck() {
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
            TokenTypes.RECORD_DEF,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.COMPACT_CTOR_DEF,
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
            JavadocEmptyLineBeforeTagCheck.findCommentStart(lines, current) + 1;
        final int end =
            JavadocEmptyLineBeforeTagCheck.findCommentEnd(lines, current) - 1;
        if (JavadocEmptyLineBeforeTagCheck.isNodeHavingJavadoc(ast, start)
            && start < lines.length && end >= start) {
            final int tag =
                JavadocEmptyLineBeforeTagCheck.findFirstTag(lines, start, end);
            if (tag > start
                && !JavadocEmptyLineBeforeTagCheck.isJavadocLineEmpty(lines[tag - 1])) {
                this.log(
                    tag + 1,
                    "Empty Javadoc line required before the block of at-clauses"
                );
            }
        }
    }

    private static boolean isJavadocLineEmpty(final String line) {
        return "*".equals(line.trim());
    }

    private static boolean isNodeHavingJavadoc(final DetailAST node,
        final int start) {
        int previous = 0;
        final DetailAST prev = node.getPreviousSibling();
        if (prev != null) {
            previous = prev.getLineNo();
        }
        return start > previous;
    }

    private static int findCommentStart(final String[] lines, final int start) {
        return JavadocEmptyLineBeforeTagCheck.findTrimmedTextUp(lines, start, "/**");
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

    private static int findFirstTag(final String[] lines, final int start,
        final int end) {
        int found = -1;
        for (int pos = start; pos <= end; pos += 1) {
            final String trimmed = lines[pos].trim();
            if (trimmed.startsWith("* @") || trimmed.startsWith("*@")) {
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
