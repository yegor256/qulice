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
     * Pattern for check.
     * It is not final as it is initialized from the configuration.
     */
    private Pattern format = Pattern.compile("^$");

    /**
     * The message to report for a match.
     * It is not final as it is initialized from the configuration.
     */
    private String message = "";

    /**
     * Comment line.
     * It is not final because the visitToken method is called many times
     * during the class under test and the field is reinitialized with a new object.
     */
    @SuppressWarnings("PMD.AvoidStringBufferField")
    private final StringBuilder line = new StringBuilder();

    /**
     * When inside a block comment, holds begin line number.
     */
    private int begin;

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
     * <module name="com.qulice.checkstyle.SingleLineCommentCheck"/> and
     * <property name="format" value=" this regexp "/> property
     *
     * @param fmt Validatig regexp.
     */
    public void setFormat(final String fmt) {
        this.format = Pattern.compile(fmt);
    }

    /**
     * The method is called from checkstyle to configure this class.
     * The parameter is set from the checks.xml file
     * <module name="com.qulice.checkstyle.SingleLineCommentCheck"/> and
     * <property name="message" value="This kind of comment is not allowed."/>
     * property
     *
     * @param msg Error message.
     */
    public void setMessage(final String msg) {
        this.message = msg;
    }

    /**
     * Checks for the end of a comment line.
     * @param ast Checkstyle's AST nodes.
     * @return True if this is the end of the comment
     *  and the starting line number is equal to the ending line number.
     */
    private boolean singleLineCStyleComment(final DetailAST ast) {
        return ast.getType() == TokenTypes.BLOCK_COMMENT_END && this.begin == ast.getLineNo();
    }
}
