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
 * Check for compact paragraph tags in Javadoc.
 *
 * <p>The built-in {@code JavadocParagraph} module governs the blank lines
 * around {@code <p>}, but nothing stops the tag from sitting alone on its
 * own line. This check keeps {@code <p>} glued to the text it opens and,
 * when {@code </p>} is used at all, glued to the text it closes. So a line
 * whose trimmed content ends with {@code <p>}, or whose trimmed content
 * starts with {@code </p>}, is reported as a violation. See
 * <a href="https://github.com/yegor256/qulice/issues/1709">#1709</a>.
 *
 * <p>The following Javadoc will be reported as a violation, since the
 * opening tag ends a line and the closing tag starts a line:
 * <pre>
 * &#47;**
 *  <span style="color:red" >* &lt;p&gt;</span>
 *  * An example of how to configure the check is:
 *  <span style="color:red" >* &lt;/p&gt;</span>
 *  *&#47;
 * </pre>
 *
 * <p>And this is how it should be written instead:
 * <pre>
 * &#47;**
 *  * &lt;p&gt;An example of how to configure the check is:&lt;/p&gt;
 *  *&#47;
 * </pre>
 *
 * <p>Lines inside a {@code <pre>...</pre>} block or a {@code {@snippet ...}}
 * block are skipped, since a literal {@code <p>} or {@code </p>} may appear
 * there as example content rather than as a real tag.
 *
 * @since 0.73.3
 */
public final class JavadocCompactParagraphCheck extends AbstractCheck {

    /**
     * Message about an opening tag that ends a line.
     */
    private static final String MSG_OPEN =
        "Opening paragraph tag <p> must be followed by text on the same line";

    /**
     * Message about a closing tag that starts a line.
     */
    private static final String MSG_CLOSE =
        "Closing paragraph tag </p> must be preceded by text on the same line";

    /**
     * Default constructor.
     */
    public JavadocCompactParagraphCheck() {
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
            JavadocCompactParagraphCheck.findCommentStart(lines, current) + 1;
        final int end =
            JavadocCompactParagraphCheck.findCommentEnd(lines, current) - 1;
        if (JavadocCompactParagraphCheck.isNodeHavingJavadoc(ast, start)
            && start < lines.length && end >= start) {
            this.checkParagraphs(lines, start, end);
        }
    }

    /**
     * Report every non-compact paragraph tag inside the comment body.
     * @param lines All lines of the source file
     * @param start First body line of the comment
     * @param end Last body line of the comment
     */
    private void checkParagraphs(final String[] lines, final int start,
        final int end) {
        boolean pre = false;
        int depth = 0;
        for (int pos = start; pos <= end && pos < lines.length; pos += 1) {
            final String body = JavadocCompactParagraphCheck.body(lines[pos]);
            final String low = body.toLowerCase(Locale.ENGLISH);
            if (depth > 0) {
                depth += JavadocCompactParagraphCheck.braces(body);
            } else if (low.contains("{@snippet")) {
                depth = Math.max(0, JavadocCompactParagraphCheck.braces(body));
            } else if (pre) {
                pre = !low.contains("</pre>");
            } else if (low.contains("<pre>")) {
                pre = !low.contains("</pre>");
            } else {
                this.report(pos, body);
            }
        }
    }

    /**
     * Log a violation for a single Javadoc body line.
     * @param pos Zero-based index of the line
     * @param body Trimmed body of the line, without the leading asterisk
     */
    private void report(final int pos, final String body) {
        if (body.endsWith("<p>")) {
            this.log(pos + 1, JavadocCompactParagraphCheck.MSG_OPEN);
        }
        if (body.startsWith("</p>")) {
            this.log(pos + 1, JavadocCompactParagraphCheck.MSG_CLOSE);
        }
    }

    /**
     * Strip the leading asterisk and surrounding blanks from a Javadoc line.
     * @param line Raw source line
     * @return Trimmed body of the line, without the leading asterisk
     */
    private static String body(final String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("*")) {
            trimmed = trimmed.substring(1).trim();
        }
        return trimmed;
    }

    /**
     * Net brace balance of a line (opening minus closing braces).
     * @param body Trimmed body of the line
     * @return Number of opening braces minus number of closing braces
     */
    private static int braces(final String body) {
        int delta = 0;
        for (int pos = 0; pos < body.length(); pos += 1) {
            final char chr = body.charAt(pos);
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
        return start > JavadocCompactParagraphCheck.getLineNoOfPreviousNode(
            node
        );
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
        return JavadocCompactParagraphCheck.findTrimmedTextUp(
            lines, start, "/**"
        );
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
