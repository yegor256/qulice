/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.LinkedList;
import java.util.List;

/**
 * Checks the order of constructor declarations.
 *
 * <p>A primary constructor is the one that does the real initialization
 * work and does not delegate to another constructor via {@code this(...)}.
 * A secondary constructor delegates, as its first statement, to another
 * constructor in the same class. The rule requires the primary constructor
 * to be declared after all secondary ones, so that the delegation chain
 * reads top-down towards the primary.
 *
 * @since 0.24
 */
public final class ConstructorsOrderCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.RECORD_DEF,
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
        final DetailAST obj = ast.findFirstToken(TokenTypes.OBJBLOCK);
        if (obj != null) {
            this.checkOrder(obj);
        }
    }

    /**
     * Checks that every secondary constructor is declared before
     * the primary one.
     * @param obj Object block node
     */
    private void checkOrder(final DetailAST obj) {
        boolean primary = false;
        for (final DetailAST ctor : ConstructorsOrderCheck.constructors(obj)) {
            if (primary) {
                this.log(
                    ctor.getLineNo(),
                    "Primary constructor must be the last one declared"
                );
            } else if (!ConstructorsOrderCheck.delegates(ctor)) {
                primary = true;
            }
        }
    }

    /**
     * Collects all constructors declared directly in the given
     * object block, in declaration order.
     * @param obj Object block node
     * @return Constructors
     */
    private static List<DetailAST> constructors(final DetailAST obj) {
        final List<DetailAST> ctors = new LinkedList<>();
        for (DetailAST child = obj.getFirstChild();
            child != null; child = child.getNextSibling()) {
            if (child.getType() == TokenTypes.CTOR_DEF) {
                ctors.add(child);
            }
        }
        return ctors;
    }

    /**
     * Tells whether the given constructor delegates to another
     * constructor via {@code this(...)}.
     * @param ctor Constructor node
     * @return True if delegating
     */
    private static boolean delegates(final DetailAST ctor) {
        boolean delegates = false;
        final DetailAST body = ctor.findFirstToken(TokenTypes.SLIST);
        if (body != null) {
            for (DetailAST stmt = body.getFirstChild();
                stmt != null; stmt = stmt.getNextSibling()) {
                if (stmt.getType() == TokenTypes.CTOR_CALL) {
                    delegates = true;
                    break;
                }
            }
        }
        return delegates;
    }
}
