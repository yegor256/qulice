/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

/**
 * Rule to flag redundant {@code String.format(...)} calls passed as
 * arguments to {@code com.jcabi.log.Logger} methods. The Logger already
 * supports printf-style format strings as the message argument, so
 * pre-formatting with {@code String.format} is unnecessary and should be
 * inlined into the Logger call.
 * @since 0.26.0
 */
public final class ProhibitFormatInLoggerRule
    extends AbstractJavaRulechainRule {

    /**
     * Logger method names that accept a format-string message.
     */
    private static final Set<String> METHODS = new HashSet<>(
        Arrays.asList("trace", "debug", "info", "warn", "error")
    );

    public ProhibitFormatInLoggerRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(final ASTMethodCall call, final Object data) {
        if (ProhibitFormatInLoggerRule.isLoggerCall(call)
            && ProhibitFormatInLoggerRule.hasFormatArgument(call)) {
            this.asCtx(data).addViolation(call);
        }
        return data;
    }

    private static boolean isLoggerCall(final ASTMethodCall call) {
        boolean result = false;
        if (ProhibitFormatInLoggerRule.METHODS.contains(call.getMethodName())) {
            final ASTExpression qualifier = call.getQualifier();
            result = qualifier != null
                && ProhibitFormatInLoggerRule.endsWith(
                    qualifier.getText().toString(), "Logger"
                );
        }
        return result;
    }

    private static boolean hasFormatArgument(final ASTMethodCall call) {
        final ASTArgumentList args = call.getArguments();
        return args != null
            && args.toStream()
                .any(ProhibitFormatInLoggerRule::isStringFormatCall);
    }

    private static boolean isStringFormatCall(final ASTExpression expr) {
        boolean result = false;
        if (expr instanceof ASTMethodCall) {
            final ASTMethodCall method = (ASTMethodCall) expr;
            final ASTExpression qualifier = method.getQualifier();
            result = "format".equals(method.getMethodName())
                && qualifier != null
                && ProhibitFormatInLoggerRule.endsWith(
                    qualifier.getText().toString(), "String"
                );
        }
        return result;
    }

    private static boolean endsWith(final String text, final String name) {
        return text.equals(name) || text.endsWith(".".concat(name));
    }
}
