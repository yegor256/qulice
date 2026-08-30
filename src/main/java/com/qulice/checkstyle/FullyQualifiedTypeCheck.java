/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Prohibits a fully qualified type name where an import would do.
 *
 * <p>A package name in the middle of a statement carries nothing the
 * import block does not already hold, while it lets two files of the
 * same package spell the very same type in two different ways. A body
 * that says {@code final java.util.List<String> spans = new
 * java.util.ArrayList<>(cap)} should import both types and say
 * {@code final List<String> spans = new ArrayList<>(cap)} instead.</p>
 *
 * <p>It is wrong for the reason the {@code java.lang.} prefix is wrong,
 * which {@link UnnecessaryJavaLangCheck} reports on its own and this
 * check therefore leaves alone.</p>
 *
 * <p>The genuine disambiguation cases stay untouched. A file that
 * imports {@code java.awt.List} has no way to spell {@code
 * java.util.List} short, and neither has a class that extends a type of
 * its own simple name, the way {@code MethodNameCheck} extends the
 * Checkstyle class of that very name. In both of them the simple name
 * is taken by something else in the file and the qualified name is the
 * only name left. A name that two imports of the file bring in is taken
 * by both of them and stays alone as well.</p>
 *
 * <p>A name counts as a type name when it starts with a capital letter,
 * has a small one after it, and stands behind at least two package
 * names, so that {@code Foo.BAR} and {@code System.out.println} stay
 * out of the way. Only plain names count, which is why the chain of
 * {@code java.util.List.class} ends at its third name and the one of
 * {@code this.foo} is empty.</p>
 *
 * @since 0.73.4
 */
public final class FullyQualifiedTypeCheck extends AbstractCheck {

    /**
     * Types a file may declare, taking their simple names.
     */
    private static final Set<Integer> DECLARATIONS = Set.of(
        TokenTypes.CLASS_DEF,
        TokenTypes.INTERFACE_DEF,
        TokenTypes.ENUM_DEF,
        TokenTypes.RECORD_DEF,
        TokenTypes.ANNOTATION_DEF
    );

    /**
     * Simple names the file binds to something of its own, either by an
     * import or by a type declaration of that name.
     */
    private final Set<String> taken;

    /**
     * Fully qualified names the file imports, by their simple names.
     */
    private final Map<String, String> imports;

    /**
     * Default constructor.
     */
    public FullyQualifiedTypeCheck() {
        this.taken = new HashSet<>(0);
        this.imports = new HashMap<>(0);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.DOT};
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
    public void beginTree(final DetailAST root) {
        this.taken.clear();
        this.imports.clear();
        this.scan(root);
    }

    @Override
    public void visitToken(final DetailAST ast) {
        if (ast.getParent().getType() != TokenTypes.DOT
            && !FullyQualifiedTypeCheck.declaring(ast)) {
            this.inspect(FullyQualifiedTypeCheck.chain(ast), ast.getLineNo());
        }
    }

    private void inspect(final List<String> names, final int line) {
        final int pos = FullyQualifiedTypeCheck.classy(names);
        if (pos > 1) {
            final String simple = names.get(pos);
            final String pkg = String.join(".", names.subList(0, pos));
            final String full = String.format("%s.%s", pkg, simple);
            if (!"java.lang".equals(pkg) && this.free(simple, full)) {
                this.log(
                    line,
                    String.format(
                        "Fully qualified \"%s\" is redundant, import it and use \"%s\"",
                        full, simple
                    )
                );
            }
        }
    }

    private boolean free(final String simple, final String full) {
        return !this.taken.contains(simple)
            || full.equals(this.imports.get(simple));
    }

    private void scan(final DetailAST node) {
        final int type = node.getType();
        if (type == TokenTypes.IMPORT || type == TokenTypes.STATIC_IMPORT) {
            this.remember(
                FullyQualifiedTypeCheck.chain(node.getFirstChild())
            );
        } else if (FullyQualifiedTypeCheck.DECLARATIONS.contains(type)) {
            this.taken.add(node.findFirstToken(TokenTypes.IDENT).getText());
        }
        DetailAST child = node.getFirstChild();
        while (child != null) {
            this.scan(child);
            child = child.getNextSibling();
        }
    }

    private void remember(final List<String> names) {
        final int pos = FullyQualifiedTypeCheck.classy(names);
        if (pos >= 0) {
            final String simple = names.get(pos);
            final String full = String.join(".", names.subList(0, pos + 1));
            if (this.taken.add(simple)) {
                this.imports.put(simple, full);
            } else if (!full.equals(this.imports.get(simple))) {
                this.imports.remove(simple);
            }
        }
    }

    private static boolean declaring(final DetailAST node) {
        boolean found = false;
        DetailAST parent = node.getParent();
        while (parent != null) {
            final int type = parent.getType();
            if (type == TokenTypes.PACKAGE_DEF
                || type == TokenTypes.IMPORT
                || type == TokenTypes.STATIC_IMPORT) {
                found = true;
                break;
            }
            parent = parent.getParent();
        }
        return found;
    }

    private static List<String> chain(final DetailAST node) {
        final List<String> names = new ArrayList<>(0);
        DetailAST dot = node;
        while (dot.getType() == TokenTypes.DOT) {
            final DetailAST right = dot.getFirstChild().getNextSibling();
            if (right != null && right.getType() == TokenTypes.IDENT) {
                names.add(0, right.getText());
            } else {
                names.clear();
            }
            dot = dot.getFirstChild();
        }
        if (dot.getType() == TokenTypes.IDENT) {
            names.add(0, dot.getText());
        } else {
            names.clear();
        }
        return names;
    }

    private static int classy(final List<String> names) {
        int found = -1;
        for (int pos = 0; pos < names.size(); ++pos) {
            if (FullyQualifiedTypeCheck.classy(names.get(pos))) {
                found = pos;
                break;
            }
        }
        return found;
    }

    private static boolean classy(final String name) {
        return Character.isUpperCase(name.charAt(0))
            && name.chars().anyMatch(Character::isLowerCase);
    }
}
