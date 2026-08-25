/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TextBlock;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.Locale;

/**
 * Check for a Javadoc paragraph that is never closed.
 *
 * <p>A paragraph opened with {@code <p>} must be closed with {@code </p>}
 * before the next {@code <p>} starts and before the comment ends. Leaving
 * it open makes the reader guess where the paragraph stops, and invites
 * the habit of "closing" it with something else, such as a bare asterisk.
 * See <a href="https://github.com/yegor256/qulice/issues/1783">#1783</a>.</p>
 *
 * <p>The following Javadoc will be reported as a violation, since the
 * first paragraph is still open when the second one starts and the second
 * one is still open when the comment ends:</p>
 *
 * <pre>
 * &#47;**
 *  <span style="color:red" >* &lt;p&gt;The first paragraph.</span>
 *  *
 *  <span style="color:red" >* &lt;p&gt;The second one.</span>
 *  *&#47;
 * </pre>
 *
 * <p>And this is how it should be written instead:</p>
 *
 * <pre>
 * &#47;**
 *  * &lt;p&gt;The first paragraph.&lt;/p&gt;
 *  *
 *  * &lt;p&gt;The second one.&lt;/p&gt;
 *  *&#47;
 * </pre>
 *
 * <p>The violation is reported on the line that opens the paragraph, since
 * that is the tag left dangling. Lines inside a {@code <pre>...</pre>} block
 * or a {@code {@snippet ...}} block are skipped, and so is the text of an
 * inline {@code {@code ...}} or {@code {@literal ...}} tag, since a literal
 * {@code <p>} may appear in any of them as example content rather than as
 * a real tag.</p>
 *
 * @since 0.73.4
 */
public final class JavadocUnclosedParagraphCheck extends AbstractCheck {

    /**
     * Message about a paragraph that is never closed.
     */
    private static final String MSG =
        "Opening paragraph tag <p> must be closed with </p>";

    /**
     * Opening tag.
     */
    private static final String OPEN = "<p>";

    /**
     * Closing tag.
     */
    private static final String CLOSE = "</p>";

    /**
     * Default constructor.
     */
    public JavadocUnclosedParagraphCheck() {
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
    @SuppressWarnings("deprecation")
    public void visitToken(final DetailAST ast) {
        final TextBlock doc =
            this.getFileContents().getJavadocBefore(ast.getLineNo());
        if (doc != null) {
            this.check(doc);
        }
    }

    private void check(final TextBlock doc) {
        final String[] lines = JavadocUnclosedParagraphCheck.masked(doc);
        boolean pre = false;
        int depth = 0;
        int open = -1;
        for (int pos = 0; pos < lines.length; pos += 1) {
            final String low = lines[pos];
            if (depth > 0) {
                depth += JavadocUnclosedParagraphCheck.braces(low);
            } else if (low.contains("{@snippet")) {
                depth = Math.max(
                    0, JavadocUnclosedParagraphCheck.braces(low)
                );
            } else if (pre) {
                pre = !low.contains("</pre>");
            } else if (low.contains("<pre>")) {
                pre = !low.contains("</pre>");
            } else {
                open = this.scan(doc, pos, low, open);
            }
        }
        if (open >= 0) {
            this.log(
                doc.getStartLineNo() + open,
                JavadocUnclosedParagraphCheck.MSG
            );
        }
    }

    private int scan(final TextBlock doc, final int pos, final String low,
        final int start) {
        int open = start;
        for (int idx = 0; idx < low.length(); idx += 1) {
            if (low.startsWith(JavadocUnclosedParagraphCheck.OPEN, idx)) {
                if (open >= 0) {
                    this.log(
                        doc.getStartLineNo() + open,
                        JavadocUnclosedParagraphCheck.MSG
                    );
                }
                open = pos;
            } else if (low.startsWith(JavadocUnclosedParagraphCheck.CLOSE, idx)) {
                open = -1;
            }
        }
        return open;
    }

    private static String[] masked(final TextBlock doc) {
        final String[] lines = doc.getText();
        final String[] bodies = new String[lines.length];
        int inline = 0;
        for (int pos = 0; pos < lines.length; pos += 1) {
            final String body = JavadocUnclosedParagraphCheck.body(lines[pos])
                .toLowerCase(Locale.ENGLISH);
            final StringBuilder buf = new StringBuilder(body.length());
            for (int idx = 0; idx < body.length(); idx += 1) {
                final char chr = body.charAt(idx);
                if (inline > 0) {
                    if (chr == '{') {
                        inline += 1;
                    } else if (chr == '}') {
                        inline -= 1;
                    }
                    buf.append(' ');
                } else if (body.startsWith("{@code", idx)
                    || body.startsWith("{@literal", idx)) {
                    inline = 1;
                    buf.append(' ');
                } else {
                    buf.append(chr);
                }
            }
            bodies[pos] = buf.toString();
        }
        return bodies;
    }

    private static String body(final String line) {
        String trimmed = line.trim();
        if (trimmed.endsWith("*/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 2).trim();
        }
        if (trimmed.startsWith("/**")) {
            trimmed = trimmed.substring(3).trim();
        } else if (trimmed.startsWith("*")) {
            trimmed = trimmed.substring(1).trim();
        }
        return trimmed;
    }

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
}
