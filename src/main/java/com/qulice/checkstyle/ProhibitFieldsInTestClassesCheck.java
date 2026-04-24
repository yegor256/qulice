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
 * Checks that test classes do not declare instance fields, since
 * such fields couple tests together through shared state.
 *
 * <p>Only files whose names match the configured pattern (by default
 * {@code *Test.java}, {@code *IT.java}, {@code *ITCase.java}) are
 * inspected. Within those files, every instance field declared at
 * class level is flagged unless it carries at least one annotation
 * (e.g. {@code @Rule}, {@code @ClassRule}, {@code @Parameter},
 * {@code @TempDir}, {@code @Mock}). Static fields are ignored,
 * because they represent compile-time constants or shared fixtures
 * rather than per-test state.
 *
 * <p>See also
 * <a href="http://www.yegor256.com/2015/05/25/unit-test-scaffolding.html">
 * Unit Test Scaffolding</a>.
 *
 * @since 0.24
 */
public final class ProhibitFieldsInTestClassesCheck extends AbstractCheck {

    /**
     * File names that this check applies to.
     */
    private Pattern include = Pattern.compile(".*(Test|IT|ITCase)\\.java$");

    /**
     * Restrict the check to files matching the given pattern.
     * @param regex Regex of file names to include.
     */
    public void setIncludeFileNamePattern(final String regex) {
        this.include = Pattern.compile(regex);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.VARIABLE_DEF,
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
        if (this.include.matcher(this.getFilePath()).find()
            && ProhibitFieldsInTestClassesCheck.isUnannotatedInstanceField(ast)) {
            final DetailAST name = ast.findFirstToken(TokenTypes.IDENT);
            this.log(
                name.getLineNo(),
                String.format(
                    "Field \"%s\" is not allowed in a test class, move it into a test method or annotate it",
                    name.getText()
                )
            );
        }
    }

    /**
     * Is this VARIABLE_DEF a class field that is neither static nor
     * annotated?
     * @param node Variable definition node.
     * @return True if the field should be flagged.
     */
    private static boolean isUnannotatedInstanceField(final DetailAST node) {
        final DetailAST parent = node.getParent();
        final boolean field = parent != null
            && parent.getType() == TokenTypes.OBJBLOCK;
        boolean flag = false;
        if (field) {
            final DetailAST modifiers = node.findFirstToken(TokenTypes.MODIFIERS);
            flag = modifiers.findFirstToken(TokenTypes.LITERAL_STATIC) == null
                && modifiers.findFirstToken(TokenTypes.ANNOTATION) == null;
        }
        return flag;
    }
}
