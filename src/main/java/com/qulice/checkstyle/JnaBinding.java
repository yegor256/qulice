/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * A method of a JNA binding.
 *
 * <p>A type that extends {@code com.sun.jna.Library} transcribes a native
 * library function by function. The name of every method and the length of
 * its parameter list come from the native side, so its author has no say in
 * either of them and the checks that judge them have nothing to say here.
 *
 * <p>Qulice runs Checkstyle on one file at a time, so {@code Library} is
 * never resolved to a class. The name in the {@code extends} or
 * {@code implements} clause is therefore trusted only when it is fully
 * qualified or when the file imports it from {@code com.sun.jna}.
 *
 * @since 1.0
 */
final class JnaBinding {

    /**
     * Package of the JNA interface that marks a type as a native binding.
     */
    private static final String PACKAGE = "com.sun.jna";

    /**
     * Simple name of that interface.
     */
    private static final String LIBRARY = "Library";

    /**
     * Canonical name of that interface.
     */
    private static final String QUALIFIED =
        JnaBinding.PACKAGE + '.' + JnaBinding.LIBRARY;

    /**
     * The node of the method declaration.
     */
    private final DetailAST node;

    /**
     * Ctor.
     * @param method The METHOD_DEF node
     */
    JnaBinding(final DetailAST method) {
        this.node = method;
    }

    /**
     * Is this method a part of a JNA binding?
     * @return TRUE if the type that declares it maps a native library
     */
    boolean is() {
        DetailAST type = this.node.getParent();
        boolean answer = false;
        while (type != null) {
            if (type.getType() == TokenTypes.CLASS_DEF
                || type.getType() == TokenTypes.INTERFACE_DEF) {
                answer = JnaBinding.mapped(type);
                break;
            }
            type = type.getParent();
        }
        return answer;
    }

    /**
     * Does this type declare JNA's interface as its supertype?
     * @param type The CLASS_DEF or INTERFACE_DEF node
     * @return TRUE if it does
     */
    private static boolean mapped(final DetailAST type) {
        return JnaBinding.declares(type, TokenTypes.EXTENDS_CLAUSE)
            || JnaBinding.declares(type, TokenTypes.IMPLEMENTS_CLAUSE);
    }

    /**
     * Does this clause of the type name JNA's interface?
     * @param type The CLASS_DEF or INTERFACE_DEF node
     * @param clause The token of the clause to look into
     * @return TRUE if it does
     */
    private static boolean declares(final DetailAST type, final int clause) {
        final DetailAST names = type.findFirstToken(clause);
        final boolean answer;
        if (names == null) {
            answer = false;
        } else {
            answer = new ChildStream(names).children()
                .anyMatch(JnaBinding::library);
        }
        return answer;
    }

    /**
     * Is this name the one of JNA's interface?
     * @param name The IDENT or DOT node of a supertype
     * @return TRUE if it is
     */
    private static boolean library(final DetailAST name) {
        final String text = FullIdent.createFullIdent(name).getText();
        return JnaBinding.QUALIFIED.equals(text)
            || JnaBinding.LIBRARY.equals(text) && JnaBinding.imported(name);
    }

    /**
     * Does the file that holds this node import JNA's interface?
     * @param node The node to start the walk from
     * @return TRUE if it does
     */
    private static boolean imported(final DetailAST node) {
        DetailAST root = node;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return new ChildStream(root).children().anyMatch(JnaBinding::importing);
    }

    /**
     * Does this node import JNA's interface?
     * @param node The node of a top-level declaration
     * @return TRUE if it does
     */
    private static boolean importing(final DetailAST node) {
        return node.getType() == TokenTypes.IMPORT
            && JnaBinding.jna(
                FullIdent.createFullIdentBelow(node).getText()
            );
    }

    /**
     * Does this imported name bring JNA's interface in?
     * @param text The name of the import
     * @return TRUE if it does
     */
    private static boolean jna(final String text) {
        return JnaBinding.QUALIFIED.equals(text)
            || String.format("%s.*", JnaBinding.PACKAGE).equals(text);
    }
}
