/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids {@code @Test}-annotated methods whose name starts with
 * {@code test} or {@code should}.
 *
 * <p>Test method names should start with a verb describing the scenario
 * under test, not with the generic prefixes {@code test} (which merely
 * repeats the annotation) or {@code should} (which frames the test as a
 * specification rather than as a behaviour). A method called
 * {@code parsesIntegers()} is more informative than
 * {@code testParseInteger()} or {@code shouldParseInteger()}. Names
 * starting with {@code tests} are allowed, since the method may be
 * responsible for testing something (e.g. {@code testsAllBranches()}).
 * See
 * <a href="https://www.yegor256.com/2014/04/27/typical-mistakes-in-java-code.html#test-method-names">
 * this article</a> and
 * <a href="https://github.com/yegor256/qulice/issues/663">#663</a>.</p>
 *
 * @since 0.24
 */
public final class ProhibitTestMethodNameCheck extends AbstractCheck {

    @Override
    public int[] getDefaultTokens() {
        return this.getRequiredTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getRequiredTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] {TokenTypes.METHOD_DEF};
    }

    @Override
    public void visitToken(final DetailAST ast) {
        if (ProhibitTestMethodNameCheck.isTest(ast)) {
            final DetailAST name = ast.findFirstToken(TokenTypes.IDENT);
            final String text = name.getText();
            if (ProhibitTestMethodNameCheck.startsWithForbidden(text)) {
                this.log(
                    name.getLineNo(),
                    String.format(
                        "Test method name \"%s\" must not start with \"test\" or \"should\", use a verb that describes the behaviour",
                        text
                    )
                );
            }
        }
    }

    /**
     * Does this method declaration carry a JUnit {@code @Test} annotation?
     * @param ast The METHOD_DEF node
     * @return True if annotated with {@code @Test}
     */
    private static boolean isTest(final DetailAST ast) {
        final DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        boolean found = false;
        if (modifiers != null) {
            DetailAST child = modifiers.getFirstChild();
            while (child != null) {
                if (child.getType() == TokenTypes.ANNOTATION
                    && ProhibitTestMethodNameCheck.isTestAnnotation(child)) {
                    found = true;
                    break;
                }
                child = child.getNextSibling();
            }
        }
        return found;
    }

    /**
     * Is this ANNOTATION node the JUnit {@code @Test} annotation
     * (either the short or fully-qualified form)?
     * @param ast The ANNOTATION node
     * @return True if its simple name is {@code Test}
     */
    private static boolean isTestAnnotation(final DetailAST ast) {
        final DetailAST ident = ast.findFirstToken(TokenTypes.IDENT);
        final boolean match;
        if (ident == null) {
            final DetailAST dot = ast.findFirstToken(TokenTypes.DOT);
            match = dot != null
                && dot.getLastChild() != null
                && "Test".equals(dot.getLastChild().getText());
        } else {
            match = "Test".equals(ident.getText());
        }
        return match;
    }

    /**
     * Does the name begin with a forbidden prefix?
     * @param name The method name
     * @return True if it starts with {@code should} or with {@code test}
     *  but not with {@code tests}
     */
    private static boolean startsWithForbidden(final String name) {
        return startsWithWord(name, "should")
            || startsWithWord(name, "test") && !startsWithWord(name, "tests");
    }

    /**
     * Does {@code name} start with {@code prefix} as a whole lowercase
     * word (either equal, or followed by an uppercase/underscore boundary)?
     * @param name The method name
     * @param prefix The prefix to check
     * @return True if the prefix is a word at the start of the name
     */
    private static boolean startsWithWord(final String name, final String prefix) {
        final boolean result;
        if (!name.startsWith(prefix)) {
            result = false;
        } else if (name.length() == prefix.length()) {
            result = true;
        } else {
            final char next = name.charAt(prefix.length());
            result = Character.isUpperCase(next) || next == '_';
        }
        return result;
    }
}
