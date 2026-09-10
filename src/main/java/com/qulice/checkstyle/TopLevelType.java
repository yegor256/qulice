/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Unchecked;

/**
 * A type declared at the top level of a Java file.
 *
 * <p>A class, an interface, an enum, a record or an annotation, together
 * with the package it belongs to and the names its declaration mentions.
 * That is all {@link Mentions} needs in order to tell whether one type of
 * a package uses another one. The object lives only as long as the
 * abstract syntax tree it reads, which is one file at a time.</p>
 *
 * @since 1.0
 */
final class TopLevelType {

    /**
     * The names Maven Surefire and Failsafe give to a test class.
     */
    private static final Pattern TESTS = Pattern.compile(".*(Test|IT|ITCase)");

    /**
     * The annotations of a test class. A test is package-private by
     * convention and mentioned by nobody, since the runner finds it by
     * itself, so it must never be reported as unused.
     */
    private static final Collection<String> JUNIT = Set.of(
        "Test", "ParameterizedTest", "RepeatedTest", "TestFactory",
        "TestTemplate", "ExtendWith"
    );

    /**
     * The file where the type is declared.
     */
    private final File source;

    /**
     * The package the file belongs to.
     */
    private final String pack;

    /**
     * The declaration of the type.
     */
    private final DetailAST ast;

    /**
     * The names the declaration mentions, collected once.
     */
    private final Unchecked<Collection<String>> names;

    /**
     * Ctor.
     *
     * @param file The file where the type is declared
     * @param pkg The package the file belongs to
     * @param node The declaration of the type
     */
    TopLevelType(final File file, final String pkg, final DetailAST node) {
        this.source = file;
        this.pack = pkg;
        this.ast = node;
        this.names = new Unchecked<>(
            new Sticky<>(() -> TopLevelType.mentions(node))
        );
    }

    /**
     * The file where the type is declared.
     *
     * @return The file
     */
    File file() {
        return this.source;
    }

    /**
     * The package the file belongs to.
     *
     * @return The name of the package, empty for the default one
     */
    String pack() {
        return this.pack;
    }

    /**
     * The name of the type.
     *
     * @return The name, as written in the declaration
     */
    String name() {
        return this.ast.findFirstToken(TokenTypes.IDENT).getText();
    }

    /**
     * The line where the name of the type is written.
     *
     * @return The number of the line
     */
    int line() {
        return this.ast.findFirstToken(TokenTypes.IDENT).getLineNo();
    }

    /**
     * Is this type declared without the {@code public} modifier?
     *
     * @return TRUE if only its own package can see it
     */
    boolean hidden() {
        return this.ast.findFirstToken(TokenTypes.MODIFIERS)
            .getChildCount(TokenTypes.LITERAL_PUBLIC) == 0;
    }

    /**
     * Is this a test class?
     *
     * @return TRUE if JUnit runs it, by its name or by its annotations
     */
    boolean test() {
        return TopLevelType.TESTS.matcher(this.name()).matches()
            || this.names.value().stream().anyMatch(TopLevelType.JUNIT::contains);
    }

    /**
     * The names this declaration mentions.
     *
     * <p>Every identifier of the subtree, including the name of the type
     * itself and the names of its members, since telling a type name from
     * the name of a method or a variable takes a compiler and matters
     * little: an identifier that happens to repeat the name of a class
     * only makes the class look used.</p>
     *
     * @return The names, without duplicates
     */
    Collection<String> mentioned() {
        return this.names.value();
    }

    private static Collection<String> mentions(final DetailAST node) {
        final Collection<String> found = new HashSet<>(0);
        if (node.getType() == TokenTypes.IDENT) {
            found.add(node.getText());
        }
        DetailAST child = node.getFirstChild();
        while (child != null) {
            found.addAll(TopLevelType.mentions(child));
            child = child.getNextSibling();
        }
        return found;
    }
}
