/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;
import java.util.Optional;

/**
 * A {@code split} method call whose receiver may be checked.
 *
 * <p>The Checkstyle AST carries no type information, so a call like
 * {@code PATTERN.split("a,b")} on a precompiled
 * {@code java.util.regex.Pattern} is indistinguishable from
 * {@code txt.split("a,b")} on a String by the method name alone. This
 * class recognizes a call as a String split only when its receiver is
 * known to be a String: a string literal, or an identifier whose
 * nearest enclosing declaration is a {@code String} — a local variable
 * or parameter of the enclosing method or constructor, or a field of
 * any enclosing class. The {@code this.field} form is resolved against
 * fields the same way. A local of another type hides a field of the
 * same name. Receivers of any other or unresolved kind are left
 * alone.</p>
 *
 * @since 1.0
 */
final class SplitCall {

    /**
     * The METHOD_CALL node to inspect.
     */
    private final DetailAST call;

    /**
     * Creates a new analysis of this method call.
     *
     * @param mcall METHOD_CALL node
     */
    SplitCall(final DetailAST mcall) {
        this.call = mcall;
    }

    /**
     * Is this a call of {@code split} on a receiver known to be String?
     *
     * @return True if the method is named {@code split} and its
     *  receiver is a string literal or an identifier declared as
     *  {@code String} in the enclosing scopes
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
            result = SplitCall.declaredAsString(receiver, false);
        } else if (receiver.getType() == TokenTypes.DOT
            && receiver.getFirstChild().getType() == TokenTypes.LITERAL_THIS) {
            result = SplitCall.declaredAsString(receiver.getLastChild(), true);
        } else {
            result = false;
        }
        return result;
    }

    private static boolean declaredAsString(
        final DetailAST name, final boolean fields
    ) {
        final String ident = name.getText();
        boolean found = false;
        boolean decided = false;
        for (DetailAST node = name.getParent();
            node != null && !decided;
            node = node.getParent()) {
            final Optional<Boolean> binding =
                SplitCall.scopeBinding(node, ident, fields);
            if (binding.isPresent()) {
                found = binding.get();
                decided = true;
            }
        }
        return found;
    }

    private static Optional<Boolean> scopeBinding(
        final DetailAST node, final String name, final boolean fields
    ) {
        final int type = node.getType();
        final Optional<Boolean> result;
        if (!fields
            && (type == TokenTypes.METHOD_DEF
                || type == TokenTypes.CTOR_DEF)) {
            result = SplitCall.inMethodTree(node, name);
        } else if (type == TokenTypes.OBJBLOCK) {
            result = SplitCall.inObjectBlock(node, name);
        } else {
            result = Optional.empty();
        }
        return result;
    }

    private static Optional<Boolean> inMethodTree(
        final DetailAST method, final String name
    ) {
        Optional<Boolean> found = Optional.empty();
        for (DetailAST child = method.getFirstChild();
            child != null && !found.isPresent();
            child = child.getNextSibling()) {
            found = SplitCall.scan(child, name);
        }
        return found;
    }

    private static Optional<Boolean> scan(
        final DetailAST node, final String name
    ) {
        Optional<Boolean> found = Optional.empty();
        final int type = node.getType();
        if (type == TokenTypes.VARIABLE_DEF
            || type == TokenTypes.PARAMETER_DEF) {
            found = SplitCall.named(node, name);
        } else if (!SplitCall.isScopeBarrier(type)) {
            for (DetailAST child = node.getFirstChild();
                child != null && !found.isPresent();
                child = child.getNextSibling()) {
                found = SplitCall.scan(child, name);
            }
        }
        return found;
    }

    private static boolean isScopeBarrier(final int type) {
        return type == TokenTypes.LAMBDA
            || type == TokenTypes.OBJBLOCK
            || TokenUtil.isTypeDeclaration(type);
    }

    private static Optional<Boolean> inObjectBlock(
        final DetailAST block, final String name
    ) {
        Optional<Boolean> found = Optional.empty();
        for (DetailAST child = block.getFirstChild();
            child != null && !found.isPresent();
            child = child.getNextSibling()) {
            if (child.getType() == TokenTypes.VARIABLE_DEF) {
                found = SplitCall.named(child, name);
            }
        }
        return found;
    }

    private static Optional<Boolean> named(
        final DetailAST def, final String name
    ) {
        final DetailAST ident = def.findFirstToken(TokenTypes.IDENT);
        final Optional<Boolean> result;
        if (ident != null && ident.getText().equals(name)) {
            result = Optional.of(SplitCall.stringType(def));
        } else {
            result = Optional.empty();
        }
        return result;
    }

    private static boolean stringType(final DetailAST def) {
        final DetailAST type = def.findFirstToken(TokenTypes.TYPE);
        final boolean result;
        if (type == null) {
            result = false;
        } else {
            final DetailAST ref = type.getFirstChild();
            result = ref != null
                && ref.getType() == TokenTypes.IDENT
                && "String".equals(ref.getText());
        }
        return result;
    }
}
