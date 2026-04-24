/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.TextBlock;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks that every {@code @throws} (or {@code @exception}) tag in the
 * javadoc of a method or constructor refers to an exception actually
 * declared in the {@code throws} clause of that method/constructor.
 *
 * <p>A javadoc that advertises a thrown exception the signature does
 * not declare is misleading. The same applies when the tag names a
 * different type than what the signature throws, for example when the
 * javadoc says {@code @throws IOException} but the signature declares
 * {@code throws Exception}. Both examples below are rejected:
 *
 * <pre>
 * &#47;**
 *  * &#64;throws Exception If something goes wrong.
 *  *&#47;
 * public void foo() {
 *     // ...
 * }
 *
 * &#47;**
 *  * &#64;throws IOException If something goes wrong.
 *  *&#47;
 * public void foo() throws Exception {
 *     // ...
 * }
 * </pre>
 *
 * <p>Types are compared by their simple name, so a javadoc that uses a
 * fully qualified name (e.g. {@code java.io.IOException}) still
 * matches a signature that uses the unqualified form, and vice-versa.
 *
 * @since 0.24.1
 */
public final class JavadocThrowsCheck extends AbstractCheck {

    /**
     * Compiled regexp matching a single {@code @throws}/{@code @exception}
     * javadoc line; captures tag name (group 1) and type (group 2).
     */
    private static final Pattern TAG = Pattern.compile(
        "^\\s*(?:\\*|/\\*\\*)?\\s*@(throws|exception)\\s+(\\S+)"
    );

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
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
        final FileContents contents = this.getFileContents();
        final TextBlock doc = contents.getJavadocBefore(ast.getLineNo());
        if (doc == null) {
            return;
        }
        final Set<String> declared = JavadocThrowsCheck.declared(ast);
        final String[] lines = doc.getText();
        final int first = doc.getStartLineNo();
        for (int idx = 0; idx < lines.length; idx += 1) {
            final Matcher matcher = JavadocThrowsCheck.TAG.matcher(lines[idx]);
            if (!matcher.find()) {
                continue;
            }
            final String type = matcher.group(2);
            if (!declared.contains(JavadocThrowsCheck.simple(type))) {
                this.log(
                    first + idx,
                    "Javadoc ''@{0} {1}'' is not declared in method signature",
                    matcher.group(1),
                    type
                );
            }
        }
    }

    /**
     * Collect simple names of the exceptions declared in the
     * {@code throws} clause of a method or constructor.
     * @param ast Method/constructor definition node
     * @return Simple names of declared checked exceptions
     */
    private static Set<String> declared(final DetailAST ast) {
        final Set<String> names = new HashSet<>(0);
        final DetailAST clause = ast.findFirstToken(TokenTypes.LITERAL_THROWS);
        if (clause != null) {
            DetailAST child = clause.getFirstChild();
            while (child != null) {
                if (child.getType() == TokenTypes.IDENT) {
                    names.add(child.getText());
                } else if (child.getType() == TokenTypes.DOT) {
                    names.add(JavadocThrowsCheck.rightmost(child));
                }
                child = child.getNextSibling();
            }
        }
        return names;
    }

    /**
     * Extract the simple name from a possibly qualified type reference
     * as written in javadoc text.
     * @param text Full textual type reference
     * @return Simple name (last dot-separated segment)
     */
    private static String simple(final String text) {
        final int dot = text.lastIndexOf('.');
        final String result;
        if (dot < 0) {
            result = text;
        } else {
            result = text.substring(dot + 1);
        }
        return result;
    }

    /**
     * Walk down a DOT-chained name and return the rightmost identifier
     * text (i.e. the simple name of a qualified reference in the AST).
     * @param dot AST node of type {@code DOT}
     * @return Rightmost identifier's text
     */
    private static String rightmost(final DetailAST dot) {
        DetailAST right = dot.getLastChild();
        while (right.getType() == TokenTypes.DOT) {
            right = right.getLastChild();
        }
        return right.getText();
    }
}
