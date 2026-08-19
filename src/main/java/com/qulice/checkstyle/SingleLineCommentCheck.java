/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * C++ style inline comment is not allowed.
 * Use //-style comment instead.
 * @since 0.18
 */
public final class SingleLineCommentCheck extends AbstractCheck {

    /**
     * Pattern that matches an empty string only.
     */
    private static final Pattern EMPTY = Pattern.compile("^$");

    /**
     * Pattern for check.
     * It is not final as it is initialized from the configuration.
     */
    private Pattern format;

    /**
     * The message to report for a match.
     * It is not final as it is initialized from the configuration.
     */
    private String message;

    /**
     * Comment line.
     * It is not final because the visitToken method is called many times
     * during the class under test and the field is reinitialized with a new object.
     */
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private final StringBuilder line;

    /**
     * When inside a block comment, holds begin line number.
     */
    private int begin;

    /**
     * Default constructor.
     */
    public SingleLineCommentCheck() {
        this.format = SingleLineCommentCheck.EMPTY;
        this.message = "";
        this.line = new StringBuilder();
    }

    @Override
    public boolean isCommentNodesRequired() {
        return true;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{
            TokenTypes.BLOCK_COMMENT_BEGIN,
            TokenTypes.COMMENT_CONTENT,
            TokenTypes.BLOCK_COMMENT_END,
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
        if (ast.getType() == TokenTypes.BLOCK_COMMENT_BEGIN) {
            this.line.setLength(0);
            this.line.append(ast.getText());
            this.begin = ast.getLineNo();
        } else if (ast.getType() == TokenTypes.COMMENT_CONTENT) {
            this.line.append(ast.getText());
        } else {
            this.line.append(ast.getText());
            final Matcher matcher = this.format.matcher(this.line.toString());
            if (matcher.matches() && this.singleLineCStyleComment(ast)) {
                this.log(ast, this.message);
            }
        }
    }

    /**
     * The method is called from checkstyle to configure this class.
     * The parameter is set from the checks.xml file
     * {@code <module name="com.qulice.checkstyle.SingleLineCommentCheck"/>} and
     * {@code <property name="format" value=" this regexp "/>} property
     * @param fmt Validatig regexp
     */
    public void setFormat(final String fmt) {
        this.format = Pattern.compile(fmt);
    }

    /**
     * The method is called from checkstyle to configure this class.
     * The parameter is set from the checks.xml file
     * {@code <module name="com.qulice.checkstyle.SingleLineCommentCheck"/>} and
     * {@code <property name="message" value="This comment is not allowed."/>}
     * property
     * @param msg Error message
     */
    public void setMessage(final String msg) {
        this.message = msg;
    }

    private boolean singleLineCStyleComment(final DetailAST ast) {
        return ast.getType() == TokenTypes.BLOCK_COMMENT_END && this.begin == ast.getLineNo();
    }
}
