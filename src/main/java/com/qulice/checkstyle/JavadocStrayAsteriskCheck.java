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
 * Check for a stray asterisk at the end of a Javadoc line.
 *
 * <p>A paragraph opened with {@code <p>} is sometimes "closed" with a bare
 * {@code *} appended to the last line of its text, instead of {@code </p>}
 * or nothing at all. Javadoc renders that asterisk as literal text, right
 * after the sentence. See
 * <a href="https://github.com/yegor256/qulice/issues/1783">#1783</a>.
 *
 * <p>The following Javadoc will be reported as a violation, since the last
 * line of the paragraph ends with an asterisk that belongs to no comment
 * delimiter:
 * <pre>
 * &#47;**
 *  * &lt;p&gt;The sentinel is not a real expression kind, it is
 *  <span style="color:red" >* the parent for entries pushed at indent zero. *</span>
 *  *&#47;
 * </pre>
 *
 * <p>And this is how it should be written instead:
 * <pre>
 * &#47;**
 *  * &lt;p&gt;The sentinel is not a real expression kind, it is
 *  * the parent for entries pushed at indent zero.
 *  *&#47;
 * </pre>
 *
 * <p>The asterisks that open the comment, prefix its lines, and close it are
 * not touched. Lines inside a {@code <pre>...</pre>} block or a
 * {@code {@snippet ...}} block are skipped, since an asterisk may legally
 * end a line of example code there.
 *
 * @since 0.73.4
 */
public final class JavadocStrayAsteriskCheck extends AbstractCheck {

    /**
     * Message about a line ending with a stray asterisk.
     */
    private static final String MSG =
        "Javadoc line must not end with a stray asterisk";

    /**
     * Default constructor.
     */
    public JavadocStrayAsteriskCheck() {
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
        final String[] lines = doc.getText();
        boolean pre = false;
        int depth = 0;
        for (int pos = 0; pos < lines.length; pos += 1) {
            final String body = JavadocStrayAsteriskCheck.body(lines[pos]);
            final String low = body.toLowerCase(Locale.ENGLISH);
            if (depth > 0) {
                depth += JavadocStrayAsteriskCheck.braces(body);
            } else if (low.contains("{@snippet")) {
                depth = Math.max(0, JavadocStrayAsteriskCheck.braces(body));
            } else if (pre) {
                pre = !low.contains("</pre>");
            } else if (low.contains("<pre>")) {
                pre = !low.contains("</pre>");
            } else if (body.endsWith("*")) {
                this.log(
                    doc.getStartLineNo() + pos,
                    JavadocStrayAsteriskCheck.MSG
                );
            }
        }
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
