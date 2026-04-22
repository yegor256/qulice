/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;
import java.util.regex.Pattern;

/**
 * Checks that non static method must contain at least one reference to
 * {@code this}.
 *
 * <p>If your method doesn't need {@code this} than why it is not
 * {@code static}?
 *
 * The exception here is when method has {@code @Override} annotation. There's
 * no concept of inheritance and polymorphism for static methods even if they
 * don't need {@code this} to perform the actual work.
 *
 * Another exception is when method is {@code abstract} or {@code native}.
 * Such methods don't have body so detection based on {@code this} doesn't
 * make sense for them.
 *
 * @since 0.3
 */
public final class NonStaticMethodCheck extends AbstractCheck {

    /**
     * Files to exclude from this check.
     * This is mostly to exclude JUnit tests.
     */
    private Pattern exclude = Pattern.compile("^$");

    /**
     * Exclude files matching given pattern.
     * @param excl Regexp of classes to exclude.
     */
    public void setExcludeFileNamePattern(final String excl) {
        this.exclude = Pattern.compile(excl);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
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
    @SuppressWarnings("deprecation")
    public void visitToken(final DetailAST ast) {
        if (this.exclude.matcher(this.getFileContents().getFileName())
            .find()) {
            return;
        }
        if (TokenTypes.CLASS_DEF == ast.getParent().getParent().getType()) {
            this.checkClassMethod(ast);
        }
    }

    /**
     * Check that non static class method refer {@code this}. Methods that
     * are {@code native}, {@code abstract} or annotated with {@code @Override}
     * are excluded.  Additionally, if the method only throws an exception, it
     * too is excluded.
     * @param method DetailAST of method
     */
    private void checkClassMethod(final DetailAST method) {
        final DetailAST modifiers = method
            .findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers.findFirstToken(TokenTypes.LITERAL_STATIC) != null) {
            return;
        }
        final BranchContains checker = new BranchContains(method);
        final boolean onlythrow =
            checker.check(TokenTypes.LITERAL_THROW)
                && !checker.check(TokenTypes.LCURLY)
                && this.countSemiColons(method) == 1;
        if (!AnnotationUtil.containsAnnotation(method, "Override")
            && !isInAbstractOrNativeMethod(method)
            && !checker.check(TokenTypes.LITERAL_THIS)
            && !onlythrow) {
            final int line = method.getLineNo();
            this.log(
                line,
                "This method must be static, because it does not refer to \"this\""
            );
        }
    }

    /**
     * Determines whether a method is {@code abstract} or {@code native}.
     * @param method Method to check.
     * @return True if method is abstract or native.
     */
    private static boolean isInAbstractOrNativeMethod(final DetailAST method) {
        final DetailAST modifiers = method.findFirstToken(TokenTypes.MODIFIERS);
        final BranchContains checker = new BranchContains(modifiers);
        return checker.check(TokenTypes.ABSTRACT)
            || checker.check(TokenTypes.LITERAL_NATIVE);
    }

    /**
     * Determines the number semicolons in a method excluding those in
     * comments.
     * @param method Method to count
     * @return The number of semicolons in the method as an int
     */
    @SuppressWarnings("deprecation")
    private int countSemiColons(final DetailAST method) {
        final DetailAST openingbrace = method.findFirstToken(TokenTypes.SLIST);
        int count = 0;
        if (openingbrace != null) {
            final DetailAST closingbrace =
                openingbrace.findFirstToken(TokenTypes.RCURLY);
            final int lastline = closingbrace.getLineNo();
            final int firstline = openingbrace.getLineNo();
            final FileContents contents = this.getFileContents();
            for (int line = firstline - 1; line < lastline; line += 1) {
                if (!contents.lineIsBlank(line)
                    && !contents.lineIsComment(line)
                    && contents.getLine(line).contains(";")) {
                    count += 1;
                }
            }
        }
        return count;
    }
}
