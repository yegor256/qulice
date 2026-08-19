/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.Collection;
import java.util.Set;

/**
 * A method of a JNA binding.
 *
 * <p>A type that extends {@code com.sun.jna.Library}, or its Windows
 * flavour {@code com.sun.jna.win32.StdCallLibrary}, transcribes a native
 * library function by function. The name of every method and the length of
 * its parameter list come from the native side, so its author has no say in
 * either of them and the checks that judge them have nothing to say here.
 *
 * <p>Qulice runs Checkstyle on one file at a time, so neither of the two
 * interfaces is ever resolved to a class. The name in the {@code extends}
 * or {@code implements} clause is therefore trusted only when it is fully
 * qualified or when the file imports it from its own package.
 *
 * @since 1.0
 */
final class JnaBinding {

    /**
     * Canonical names of the JNA interfaces that mark a type as a native
     * binding.
     */
    private static final Collection<String> LIBRARIES = Set.of(
        "com.sun.jna.Library",
        "com.sun.jna.win32.StdCallLibrary"
    );

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

    private static boolean mapped(final DetailAST type) {
        return JnaBinding.declares(type, TokenTypes.EXTENDS_CLAUSE)
            || JnaBinding.declares(type, TokenTypes.IMPLEMENTS_CLAUSE);
    }

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

    private static boolean library(final DetailAST name) {
        final String text = FullIdent.createFullIdent(name).getText();
        return JnaBinding.LIBRARIES.stream().anyMatch(
            known -> known.equals(text)
                || JnaBinding.simple(known).equals(text)
                && JnaBinding.imported(name, known)
        );
    }

    private static boolean imported(final DetailAST node, final String known) {
        DetailAST root = node;
        while (root.getParent() != null) {
            root = root.getParent();
        }
        return new ChildStream(root).children().anyMatch(
            child -> JnaBinding.importing(child, known)
        );
    }

    private static boolean importing(final DetailAST node, final String known) {
        return node.getType() == TokenTypes.IMPORT
            && JnaBinding.brings(
                FullIdent.createFullIdentBelow(node).getText(), known
            );
    }

    private static boolean brings(final String text, final String known) {
        return known.equals(text)
            || String.format("%s.*", JnaBinding.pack(known)).equals(text);
    }

    private static String simple(final String known) {
        return known.substring(known.lastIndexOf('.') + 1);
    }

    private static String pack(final String known) {
        return known.substring(0, known.lastIndexOf('.'));
    }
}
