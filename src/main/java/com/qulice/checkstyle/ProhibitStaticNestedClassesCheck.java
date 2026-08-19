/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.regex.Pattern;

/**
 * Checks that a class does not declare a static nested class, since a
 * static nested class is a self-contained abstraction and belongs in
 * a file of its own. Non-static (inner) nested classes are always
 * allowed, since they keep a reference to their enclosing instance
 * and are not self-contained.
 *
 * <p>Only files whose names do not match the configured pattern (by
 * default {@code *Test.java}, {@code *IT.java}, {@code *ITCase.java})
 * are inspected, since fake/stub helpers nested inside test classes
 * are a normal pattern (see
 * {@link ProhibitFieldsInTestClassesCheck}).
 *
 * @since 0.73.4
 */
public final class ProhibitStaticNestedClassesCheck extends AbstractCheck {

    /**
     * File names of test classes.
     */
    private static final Pattern TESTS =
        Pattern.compile(".*(Test|IT|ITCase)\\.java$");

    /**
     * File names that this check does not apply to.
     */
    private Pattern exclude;

    /**
     * Default constructor.
     */
    public ProhibitStaticNestedClassesCheck() {
        this.exclude = ProhibitStaticNestedClassesCheck.TESTS;
    }

    /**
     * Do not apply this check to files matching the given pattern.
     * @param regex Regex of file names to exclude
     */
    public void setExcludeFileNamePattern(final String regex) {
        this.exclude = Pattern.compile(regex);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.CLASS_DEF};
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
        if (!this.exclude.matcher(this.getFilePath()).find()
            && ProhibitStaticNestedClassesCheck.isStaticNestedClass(ast)) {
            final DetailAST name = ast.findFirstToken(TokenTypes.IDENT);
            this.log(
                name.getLineNo(),
                String.format(
                    "Static class \"%s\" must be moved into its own file",
                    name.getText()
                )
            );
        }
    }

    /**
     * Is this CLASS_DEF a static class nested inside another type?
     * @param ast Class definition node
     * @return True if the class is nested and declared as static
     */
    private static boolean isStaticNestedClass(final DetailAST ast) {
        final DetailAST parent = ast.getParent();
        return parent != null && parent.getType() == TokenTypes.OBJBLOCK
            && ast.findFirstToken(TokenTypes.MODIFIERS)
                .findFirstToken(TokenTypes.LITERAL_STATIC) != null;
    }
}
