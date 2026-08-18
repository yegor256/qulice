/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Number of parameters, tolerant to constructors that only take attributes
 * and to private methods.
 *
 * <p>The stock {@code ParameterNumber} check treats a constructor as it
 * treats a method, while a constructor has no choice: it must accept a
 * value for every attribute of its class. A long list of parameters there
 * is a symptom of a class with too many attributes, and the class is what
 * has to be blamed, not its constructor. That's why a constructor with no
 * more parameters than the number of attributes of its class passes here,
 * no matter how big the limit is.
 *
 * <p>A private method passes too. It is invisible outside its class, its
 * parameters belong to no contract anybody else can see, and wrapping them
 * into a new type only to satisfy the limit buys nothing.
 *
 * <p>So does a method of a JNA binding, whose parameter list repeats the
 * signature of a native function and cannot be shortened without breaking
 * the mapping.
 *
 * @since 1.0
 */
public final class ParameterNumberCheck
    extends com.puppycrawl.tools.checkstyle.checks.sizes.ParameterNumberCheck {

    /**
     * Default constructor.
     */
    public ParameterNumberCheck() {
        // nothing to initialize
    }

    @Override
    public void visitToken(final DetailAST ast) {
        if (!ParameterNumberCheck.tolerated(ast)) {
            super.visitToken(ast);
        }
    }

    /**
     * May this node keep its long list of parameters?
     *
     * <p>A constructor may, when it takes no more parameters than the
     * number of attributes of its class. A method may, when it is
     * private or when it belongs to a JNA binding.</p>
     *
     * @param ast The CTOR_DEF or METHOD_DEF node
     * @return TRUE if the check has to stay silent about it
     */
    private static boolean tolerated(final DetailAST ast) {
        final boolean answer;
        if (ast.getType() == TokenTypes.CTOR_DEF) {
            answer = ParameterNumberCheck.parameters(ast)
                <= ParameterNumberCheck.attributes(ast);
        } else {
            answer = ast.findFirstToken(TokenTypes.MODIFIERS)
                .findFirstToken(TokenTypes.LITERAL_PRIVATE) != null
                || new JnaBinding(ast).is();
        }
        return answer;
    }

    /**
     * How many parameters does this constructor take?
     * @param ctor The CTOR_DEF node
     * @return Number of parameters
     */
    private static int parameters(final DetailAST ctor) {
        return ctor.findFirstToken(TokenTypes.PARAMETERS)
            .getChildCount(TokenTypes.PARAMETER_DEF);
    }

    /**
     * How many attributes does the class of this constructor have?
     *
     * <p>Static fields stay out of the count, since they belong to the
     * class and not to its objects. Components of a record count in, as
     * they are the attributes the canonical constructor has to take.</p>
     *
     * @param ctor The CTOR_DEF node
     * @return Number of attributes
     */
    private static long attributes(final DetailAST ctor) {
        final DetailAST block = ctor.getParent();
        return ParameterNumberCheck.components(block.getParent())
            + new ChildStream(block).children().filter(
                node -> node.getType() == TokenTypes.VARIABLE_DEF
                    && node.findFirstToken(TokenTypes.MODIFIERS)
                        .findFirstToken(TokenTypes.LITERAL_STATIC) == null
            ).count();
    }

    /**
     * How many components does this type declare, if it is a record?
     * @param type The node of the type declaration
     * @return Number of record components
     */
    private static int components(final DetailAST type) {
        final DetailAST components = type.findFirstToken(TokenTypes.RECORD_COMPONENTS);
        final int count;
        if (components == null) {
            count = 0;
        } else {
            count = components.getChildCount(TokenTypes.RECORD_COMPONENT_DEF);
        }
        return count;
    }
}
