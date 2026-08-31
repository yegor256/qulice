/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.Set;

/**
 * Prohibits an implicit constructor in a class that Javadoc documents.
 *
 * <p>A class that declares no constructor gets one from the compiler,
 * with the access of the class itself. Javadoc puts that constructor
 * into the generated page, finds no comment on it, and says "use of
 * default constructor, which does not provide a comment". A build that
 * turns Javadoc warnings into errors, which the {@code maven-javadoc-plugin}
 * does through {@code failOnWarnings}, breaks over it, and the mistake
 * surfaces at site time rather than at the moment the class is written.</p>
 *
 * <p>The fix is an explicit constructor with a Javadoc block above it,
 * and this check asks for it before Javadoc does.</p>
 *
 * <p>Only the classes Javadoc documents are reported, because only they
 * carry the warning. A class is documented when it is public or
 * protected, and so is every class around it, since a nested class of a
 * package-private one never reaches the page. A member of an interface
 * or of an annotation counts as public without saying so.</p>
 *
 * <p>The other type declarations stay out. An interface and an
 * annotation have no constructor to document. An enum gets a private
 * one, which Javadoc leaves out of the page. A record gets a canonical
 * one, which Javadoc documents from the {@code @param} tags of the
 * record itself. A local class and a member of an anonymous one are not
 * documented either, whatever their modifiers say.</p>
 *
 * @since 0.73.4
 */
public final class ImplicitConstructorCheck extends AbstractCheck {

    /**
     * Types that may hold a class, and whose own visibility therefore
     * decides whether the class inside them reaches the Javadoc page.
     */
    private static final Set<Integer> TYPES = Set.of(
        TokenTypes.CLASS_DEF,
        TokenTypes.INTERFACE_DEF,
        TokenTypes.ENUM_DEF,
        TokenTypes.RECORD_DEF,
        TokenTypes.ANNOTATION_DEF
    );

    /**
     * Default constructor.
     */
    public ImplicitConstructorCheck() {
        // nothing to initialize
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.CLASS_DEF};
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
        if (ImplicitConstructorCheck.documented(ast)
            && ast.findFirstToken(TokenTypes.OBJBLOCK)
                .findFirstToken(TokenTypes.CTOR_DEF) == null) {
            this.log(
                ast.getLineNo(),
                String.format(
                    "Implicit constructor of \"%s\" gets no Javadoc, declare it explicitly",
                    ast.findFirstToken(TokenTypes.IDENT).getText()
                )
            );
        }
    }

    private static boolean documented(final DetailAST ast) {
        boolean docs = true;
        DetailAST node = ast;
        while (node != null && node.getType() != TokenTypes.COMPILATION_UNIT) {
            final int type = node.getType();
            if (ImplicitConstructorCheck.TYPES.contains(type)) {
                if (!ImplicitConstructorCheck.visible(node)) {
                    docs = false;
                    break;
                }
            } else if (type != TokenTypes.OBJBLOCK) {
                docs = false;
                break;
            }
            node = node.getParent();
        }
        return docs;
    }

    private static boolean visible(final DetailAST type) {
        final DetailAST mods = type.findFirstToken(TokenTypes.MODIFIERS);
        return mods.findFirstToken(TokenTypes.LITERAL_PUBLIC) != null
            || mods.findFirstToken(TokenTypes.LITERAL_PROTECTED) != null
            || ImplicitConstructorCheck.implied(type);
    }

    private static boolean implied(final DetailAST type) {
        final DetailAST block = type.getParent();
        boolean implied = false;
        if (block != null && block.getType() == TokenTypes.OBJBLOCK) {
            final int owner = block.getParent().getType();
            implied = owner == TokenTypes.INTERFACE_DEF
                || owner == TokenTypes.ANNOTATION_DEF;
        }
        return implied;
    }
}
