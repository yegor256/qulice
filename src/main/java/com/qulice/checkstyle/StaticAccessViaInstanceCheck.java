/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Checks that static members are not accessed through an instance reference.
 *
 * <p>A static method or field must be accessed through the declaring class
 * (for example {@code MyClass.staticMethod()}), not through
 * {@code this.staticMethod()}. Accessing a static member through an instance
 * reference is misleading because it looks like a polymorphic call while the
 * dispatch is actually resolved statically at compile time.
 *
 * <p>This check scans every class, enum and interface in the file, collects
 * the names of all declared static methods and fields, and reports every
 * {@code this.name} expression where {@code name} is in that set.
 *
 * @since 0.24
 */
public final class StaticAccessViaInstanceCheck extends AbstractCheck {

    /**
     * Stack of static member name sets, one per enclosing class-like scope.
     */
    private final Deque<Set<String>> scopes = new ArrayDeque<>();

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.DOT,
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
    public void beginTree(final DetailAST root) {
        this.scopes.clear();
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final int type = ast.getType();
        if (type == TokenTypes.DOT) {
            this.checkDot(ast);
        } else {
            this.scopes.push(collectStatic(ast));
        }
    }

    @Override
    public void leaveToken(final DetailAST ast) {
        if (ast.getType() != TokenTypes.DOT) {
            this.scopes.pop();
        }
    }

    /**
     * Reports the dot expression if it accesses a static member via
     * {@code this}.
     * @param dot DOT node
     */
    private void checkDot(final DetailAST dot) {
        final DetailAST left = dot.getFirstChild();
        if (!this.scopes.isEmpty()
            && left != null
            && left.getType() == TokenTypes.LITERAL_THIS
            && isStaticIdent(left.getNextSibling(), this.scopes.peek())) {
            this.log(
                dot,
                "Static member must be accessed via class name, not via instance"
            );
        }
    }

    /**
     * Tells whether the node is an IDENT whose text is in the set of known
     * static member names.
     * @param node Node to check
     * @param names Known static names
     * @return True if the node matches
     */
    private static boolean isStaticIdent(
        final DetailAST node, final Set<String> names) {
        return node != null
            && node.getType() == TokenTypes.IDENT
            && names.contains(node.getText());
    }

    /**
     * Collects the names of all static methods and fields directly declared
     * in the given class-like node.
     * @param clazz CLASS_DEF, ENUM_DEF or INTERFACE_DEF node
     * @return Set of static member names
     */
    private static Set<String> collectStatic(final DetailAST clazz) {
        final Set<String> names = new HashSet<>(0);
        final DetailAST body = clazz.findFirstToken(TokenTypes.OBJBLOCK);
        for (DetailAST child = body.getFirstChild();
            child != null; child = child.getNextSibling()) {
            if (isStaticMember(child)) {
                names.add(child.findFirstToken(TokenTypes.IDENT).getText());
            }
        }
        return names;
    }

    /**
     * Tells whether the node is a static method or a static field
     * declaration.
     * @param node Node to check
     * @return True if it is a static method or field
     */
    private static boolean isStaticMember(final DetailAST node) {
        final int type = node.getType();
        final boolean member = type == TokenTypes.METHOD_DEF
            || type == TokenTypes.VARIABLE_DEF;
        boolean result = false;
        if (member) {
            final DetailAST modifiers =
                node.findFirstToken(TokenTypes.MODIFIERS);
            result = modifiers != null
                && modifiers.findFirstToken(TokenTypes.LITERAL_STATIC) != null;
        }
        return result;
    }
}
