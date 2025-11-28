/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Multi line comment checker.
 * Used by the checkstyle process multiple times as a singleton.
 * @since 0.23.1
 */
public final class MultiLineCommentCheck extends AbstractCheck {
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
    private final StringBuilder text = new StringBuilder();

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
            this.text.setLength(0);
            this.text.append(ast.getText());
        } else if (ast.getType() == TokenTypes.COMMENT_CONTENT) {
            this.text.append(ast.getText());
        } else {
            this.text.append(ast.getText());
            final Matcher matcher = this.format.matcher(this.text.toString());
            if (matcher.matches()) {
                this.log(ast, this.message);
            }
        }
    }

    /**
     * The method is called from checkstyle to configure this class.
     * The parameter is set from the checks.xml file
     * <module name="com.qulice.checkstyle.MultiLineCommentCheck"/> and
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
     * <module name="com.qulice.checkstyle.MultiLineCommentCheck"/> and
     * <property name="message" value="First sentence in a comment should start with ....."/>
     * property
     *
     * @param msg Error message.
     */
    public void setMessage(final String msg) {
        this.message = msg;
    }
}
