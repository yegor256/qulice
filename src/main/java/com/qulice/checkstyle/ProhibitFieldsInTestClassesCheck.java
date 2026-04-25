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
 * inspected. Within those files, only instance fields declared on the
 * top-level type are flagged, unless the field carries at least one
 * annotation (e.g. {@code @Rule}, {@code @ClassRule},
 * {@code @Parameter}, {@code @TempDir}, {@code @Mock}). Static fields
 * are ignored, because they represent compile-time constants or
 * shared fixtures rather than per-test state. Fields declared on
 * nested helper types (stubs, fakes, recorders) are not flagged,
 * because they belong to the helper, not to the test class.
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
     * @param regex Regex of file names to include
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
     * Is this VARIABLE_DEF an instance field of the top-level type
     * that is neither static nor annotated? Fields of nested or
     * anonymous types are not considered, because they belong to a
     * helper rather than to the test class itself.
     * @param node Variable definition node
     * @return True if the field should be flagged
     */
    private static boolean isUnannotatedInstanceField(final DetailAST node) {
        boolean flag = false;
        final DetailAST parent = node.getParent();
        if (parent != null && parent.getType() == TokenTypes.OBJBLOCK
            && ProhibitFieldsInTestClassesCheck.isTopLevelType(parent.getParent())) {
            final DetailAST modifiers = node.findFirstToken(TokenTypes.MODIFIERS);
            flag = modifiers.findFirstToken(TokenTypes.LITERAL_STATIC) == null
                && modifiers.findFirstToken(TokenTypes.ANNOTATION) == null;
        }
        return flag;
    }

    /**
     * Is this AST node a type declaration that sits at the top level
     * of the compilation unit (i.e. its parent is not an
     * {@code OBJBLOCK} of an enclosing type, and it is not the body
     * of an anonymous inner class spawned by {@code LITERAL_NEW})?
     * @param type Candidate type declaration node
     * @return True if it is the file's top-level type
     */
    private static boolean isTopLevelType(final DetailAST type) {
        return type != null
            && (type.getType() == TokenTypes.CLASS_DEF
                || type.getType() == TokenTypes.ENUM_DEF
                || type.getType() == TokenTypes.RECORD_DEF
                || type.getType() == TokenTypes.INTERFACE_DEF)
            && type.getParent() != null
            && type.getParent().getType() != TokenTypes.OBJBLOCK
            && type.getParent().getType() != TokenTypes.LITERAL_NEW;
    }
}
