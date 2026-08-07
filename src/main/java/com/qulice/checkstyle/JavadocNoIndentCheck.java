/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.Locale;

/**
 * Checks that Javadoc body text is not over-indented.
 *
 * <p>Every line of a Javadoc comment must have exactly one space between the
 * leading asterisk and the first word of the text. Extra spaces, used to
 * push a paragraph line to the right, are not allowed:
 *
 * <pre>
 * &#47;**
 *  * This is a paragraph.
 *  *     This line is over-indented and will be reported.
 *  *&#47;
 * </pre>
 *
 * <p>Lines inside a {@code <pre>} block or a {@code @snippet} block are left
 * alone, since the indentation there is semantically meaningful and has to be
 * preserved as written. Block tags and their wrapped continuation lines are
 * also left alone, since their indentation is governed by other checks.
 *
 * @since 0.74
 */
public final class JavadocNoIndentCheck extends AbstractCheck {

    /**
     * Message about the extra indentation.
     */
    private static final String MESSAGE =
        "Extra indentation is not allowed in Javadoc";

    /**
     * Default constructor.
     */
    public JavadocNoIndentCheck() {
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
            JavadocNoIndentCheck.findCommentStart(lines, current) + 1;
        if (JavadocNoIndentCheck.isNodeHavingJavadoc(ast, start)
            && start < lines.length) {
            this.check(
                lines, start,
                JavadocNoIndentCheck.findCommentEnd(lines, current) - 1
            );
        }
    }

    /**
     * Check the body of the Javadoc for extra indentation.
     * @param lines Code of the whole class
     * @param start First line of the Javadoc body
     * @param end Last line of the Javadoc body
     */
    private void check(final String[] lines, final int start, final int end) {
        boolean pre = false;
        boolean tagged = false;
        int depth = 0;
        for (int pos = start; pos <= end; pos += 1) {
            final String line = lines[pos];
            final String body = JavadocNoIndentCheck.afterAsterisk(line);
            final boolean region = JavadocNoIndentCheck.region(pre, depth, line);
            if (!region && body.trim().startsWith("@")) {
                tagged = true;
            }
            if (!region && !tagged
                && JavadocNoIndentCheck.overIndented(body)) {
                this.log(pos + 1, JavadocNoIndentCheck.MESSAGE);
            }
            pre = JavadocNoIndentCheck.nextPre(pre, line);
            depth = JavadocNoIndentCheck.nextDepth(depth, line);
        }
    }

    /**
     * Is this line inside a region where indentation is preserved.
     * @param pre Are we inside a {@code <pre>} block
     * @param depth Current brace depth of an open {@code @snippet} block
     * @param line The line being examined
     * @return True when the line's indentation must be left alone
     */
    private static boolean region(final boolean pre, final int depth,
        final String line) {
        return pre || depth > 0 || line.contains("{@snippet");
    }

    /**
     * The text that follows the leading asterisk of a Javadoc line.
     * @param line The Javadoc line
     * @return Everything after the first asterisk, or empty if there is none
     */
    private static String afterAsterisk(final String line) {
        final int star = line.indexOf('*');
        final String result;
        if (star < 0) {
            result = "";
        } else {
            result = line.substring(star + 1);
        }
        return result;
    }

    /**
     * Does the body start with more than one space before its first word.
     * @param body The text after the leading asterisk
     * @return True when the body is over-indented
     */
    private static boolean overIndented(final String body) {
        int spaces = 0;
        while (spaces < body.length() && body.charAt(spaces) == ' ') {
            spaces += 1;
        }
        return spaces > 1 && spaces < body.length();
    }

    /**
     * Compute the {@code <pre>} flag for the next line.
     * @param pre Whether the current line is inside a {@code <pre>} block
     * @param line The current line
     * @return Whether the next line is inside a {@code <pre>} block
     */
    private static boolean nextPre(final boolean pre, final String line) {
        final String lower = line.toLowerCase(Locale.ENGLISH);
        final boolean result;
        if (pre) {
            result = !lower.contains("</pre>");
        } else {
            result = lower.contains("<pre>") && !lower.contains("</pre>");
        }
        return result;
    }

    /**
     * Compute the {@code @snippet} brace depth after this line.
     * @param depth The brace depth before this line
     * @param line The current line
     * @return The brace depth after this line
     */
    private static int nextDepth(final int depth, final String line) {
        final int result;
        if (depth > 0) {
            result = Math.max(0, depth + JavadocNoIndentCheck.braces(line));
        } else {
            final int idx = line.indexOf("{@snippet");
            if (idx < 0) {
                result = 0;
            } else {
                result = Math.max(
                    0, JavadocNoIndentCheck.braces(line.substring(idx))
                );
            }
        }
        return result;
    }

    /**
     * Net number of opening minus closing braces in the text.
     * @param text The text to scan
     * @return The brace balance
     */
    private static int braces(final String text) {
        int delta = 0;
        for (int pos = 0; pos < text.length(); pos += 1) {
            final char chr = text.charAt(pos);
            if (chr == '{') {
                delta += 1;
            } else if (chr == '}') {
                delta -= 1;
            }
        }
        return delta;
    }

    /**
     * Check if node has Javadoc.
     * @param node Node to be checked for Javadoc
     * @param start Line number where comment starts
     * @return True when node has Javadoc
     */
    private static boolean isNodeHavingJavadoc(final DetailAST node,
        final int start) {
        return start > JavadocNoIndentCheck.getLineNoOfPreviousNode(node);
    }

    /**
     * Returns line number of previous node.
     * @param node Current node
     * @return Line number of previous node
     */
    private static int getLineNoOfPreviousNode(final DetailAST node) {
        int start = 0;
        final DetailAST previous = node.getPreviousSibling();
        if (previous != null) {
            start = previous.getLineNo();
        }
        return start;
    }

    /**
     * Find Javadoc starting comment.
     * @param lines List of lines to check
     * @param start Start searching from this line number
     * @return Line number with found starting comment or -1 otherwise
     */
    private static int findCommentStart(final String[] lines, final int start) {
        return JavadocNoIndentCheck.findTrimmedTextUp(lines, start, "/**");
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
