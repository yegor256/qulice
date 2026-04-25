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
 * <p>If the Javadoc body before at-clauses consists of a single paragraph,
 * there must be no empty line between the body and the first at-clause.
 * If the body contains more than one paragraph (separated by empty Javadoc
 * lines), then an empty Javadoc line is required right before the first
 * at-clause. See
 * <a href="https://github.com/yegor256/qulice/issues/708">#708</a>.
 *
 * <p>The following Javadoc will be reported as a violation, since its body
 * is a single paragraph and yet it is separated from the at-clauses by an
 * empty line:
 * <pre>
 * &#47;**
 *  * Just one line here.
 *  <span style="color:red" >*</span>
 *  * &#64;since 0.1
 *  *&#47;
 * </pre>
 *
 * <p>And this one will be reported too, since its body has more than one
 * paragraph but there is no empty line before the at-clauses:
 * <pre>
 * &#47;**
 *  * First line.
 *  *
 *  * Second par.
 *  <span style="color:red" >* &#64;since 0.1</span>
 *  *&#47;
 * </pre>
 *
 * @since 0.27.0
 */
public final class JavadocEmptyLineBeforeTagCheck extends AbstractCheck {

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
            JavadocEmptyLineBeforeTagCheck.findCommentStart(lines, current) + 1;
        final int end =
            JavadocEmptyLineBeforeTagCheck.findCommentEnd(lines, current) - 1;
        if (JavadocEmptyLineBeforeTagCheck.isNodeHavingJavadoc(ast, start)
            && start < lines.length && end >= start) {
            final int tag =
                JavadocEmptyLineBeforeTagCheck.findFirstTag(lines, start, end);
            if (tag > start) {
                this.inspect(lines, start, tag);
            }
        }
    }

    /**
     * Inspect the part of the Javadoc that lies between the opening
     * and the first at-clause.
     * @param lines All lines of the source file
     * @param start First Javadoc content line (0-based)
     * @param tag Line of the first at-clause (0-based)
     */
    private void inspect(final String[] lines, final int start, final int tag) {
        int body = tag - 1;
        while (body >= start
            && JavadocEmptyLineBeforeTagCheck.isJavadocLineEmpty(lines[body])) {
            body -= 1;
        }
        if (body >= start) {
            boolean multi = false;
            for (int pos = start; pos <= body; pos += 1) {
                if (JavadocEmptyLineBeforeTagCheck.isJavadocLineEmpty(lines[pos])) {
                    multi = true;
                    break;
                }
            }
            final boolean empty =
                JavadocEmptyLineBeforeTagCheck.isJavadocLineEmpty(lines[tag - 1]);
            if (multi && !empty) {
                this.log(
                    tag + 1,
                    "Empty Javadoc line required before at-clauses, since the description has multiple paragraphs"
                );
            } else if (!multi && empty) {
                this.log(
                    tag,
                    "Empty Javadoc line before at-clauses is not allowed, since the description is a single paragraph"
                );
            }
        }
    }

    /**
     * Check if Javadoc line is empty.
     * @param line Javadoc line
     * @return True when Javadoc line is empty
     */
    private static boolean isJavadocLineEmpty(final String line) {
        return "*".equals(line.trim());
    }

    /**
     * Check if node has Javadoc.
     * @param node Node to be checked for Javadoc
     * @param start Line number where comment starts
     * @return True when node has Javadoc
     */
    private static boolean isNodeHavingJavadoc(final DetailAST node,
        final int start) {
        int previous = 0;
        final DetailAST prev = node.getPreviousSibling();
        if (prev != null) {
            previous = prev.getLineNo();
        }
        return start > previous;
    }

    /**
     * Find Javadoc starting comment.
     * @param lines List of lines to check
     * @param start Start searching from this line number
     * @return Line number with found starting comment or -1 otherwise
     */
    private static int findCommentStart(final String[] lines, final int start) {
        return JavadocEmptyLineBeforeTagCheck.findTrimmedTextUp(lines, start, "/**");
    }

    /**
     * Find Javadoc ending comment.
     * @param lines Array of lines to check
     * @param start Start searching from this line number
     * @return Line number with found ending comment, or -1 if it wasn't found
     */
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

    /**
     * Find the first at-clause line inside the Javadoc comment.
     * @param lines All lines of the file
     * @param start First Javadoc content line (0-based)
     * @param end Last Javadoc content line (0-based)
     * @return Line number of the first at-clause, or -1 if not found
     */
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

    /**
     * Find a text in lines, by going up.
     * @param lines Array of lines to check
     * @param start Start searching from this line number
     * @param text Text to find
     * @return Line number with found text, or -1 if it wasn't found
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
