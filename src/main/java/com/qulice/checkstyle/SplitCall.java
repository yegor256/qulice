/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.regex.Pattern;

/**
 * A {@code split} method call whose receiver may be checked.
 *
 * <p>The Checkstyle AST carries no type information, so a call like
 * {@code PATTERN.split("a,b")} on a precompiled
 * {@code java.util.regex.Pattern} is indistinguishable from
 * {@code txt.split("a,b")} on a String by the method name alone. This
 * class recognizes a call as a String split only when its receiver is
 * known to be a String: a string literal, or an identifier declared
 * with a {@code String} type somewhere in the checked file. Receivers
 * of any other or unresolved kind are left alone.</p>
 *
 * @since 1.0
 */
final class SplitCall {

    /**
     * The METHOD_CALL node to inspect.
     */
    private final DetailAST call;

    /**
     * Lines of the file being checked, used to resolve declarations.
     */
    private final String[] lines;

    /**
     * Creates a new analysis of this method call.
     *
     * @param mcall METHOD_CALL node
     * @param src Lines of the file being checked
     */
    SplitCall(final DetailAST mcall, final String... src) {
        this.call = mcall;
        this.lines = src.clone();
    }

    /**
     * Is this a call of {@code split} on a receiver known to be String?
     *
     * @return True if the method is named {@code split} and its
     *  receiver is a string literal or an identifier declared as
     *  {@code String} in the checked file
     */
    boolean isStringSplitCall() {
        return SplitCall.isSplitCall(this.call)
            && this.knownStringReceiver();
    }

    private static boolean isSplitCall(final DetailAST mcall) {
        final DetailAST dot = mcall.getFirstChild();
        final boolean result;
        if (dot == null || dot.getType() != TokenTypes.DOT) {
            result = false;
        } else {
            final DetailAST name = dot.getLastChild();
            result = name != null && name.getType() == TokenTypes.IDENT
                && "split".equals(name.getText());
        }
        return result;
    }

    private boolean knownStringReceiver() {
        final DetailAST receiver = this.call.getFirstChild().getFirstChild();
        final boolean result;
        if (receiver == null) {
            result = false;
        } else if (receiver.getType() == TokenTypes.STRING_LITERAL) {
            result = true;
        } else if (receiver.getType() == TokenTypes.IDENT) {
            result = this.declaredAsString(receiver.getText());
        } else {
            result = false;
        }
        return result;
    }

    private boolean declaredAsString(final String name) {
        final Pattern declaration = Pattern.compile(
            String.format("\\bString\\s+%s\\b", Pattern.quote(name))
        );
        boolean found = false;
        for (final String line : this.lines) {
            if (declaration.matcher(line).find()) {
                found = true;
                break;
            }
        }
        return found;
    }
}
