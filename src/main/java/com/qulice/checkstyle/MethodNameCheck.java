/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

/**
 * Names of methods, tolerant to the methods of a JNA binding.
 *
 * <p>The stock {@code MethodName} check demands the same format from every
 * method, while a method of a type that extends {@code com.sun.jna.Library}
 * carries the name of a native function, spelled the way the library that
 * exports it spells it. Renaming it breaks the binding, so the check stays
 * silent there.
 *
 * <p>Its messages come from the bundle of the stock check, since the two
 * report the very same things and one wording for them is enough.
 *
 * @since 1.0
 */
public final class MethodNameCheck
    extends com.puppycrawl.tools.checkstyle.checks.naming.MethodNameCheck {

    /**
     * Default constructor.
     */
    public MethodNameCheck() {
        // nothing to initialize
    }

    @Override
    public void visitToken(final DetailAST ast) {
        if (!new JnaBinding(ast).is()) {
            super.visitToken(ast);
        }
    }

    @Override
    public String getMessageBundle() {
        return "com.puppycrawl.tools.checkstyle.checks.naming.messages";
    }
}
