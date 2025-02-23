/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.parameters;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.javadoc.JavadocTag;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Class, interface, constructor or method generic type parameters.
 *
 * @since 0.18.18
 */
public class TypeParameters {

    /**
     * Parameters.
     */
    private final Parameters parameters;

    /**
     * Secondary ctor.
     * @param node Class, interface, constructor or method defenition node.
     */
    public TypeParameters(final DetailAST node) {
        this(
            new Parameters(
                node, TokenTypes.TYPE_PARAMETERS, TokenTypes.TYPE_PARAMETER
            )
        );
    }

    /**
     * Primary ctor.
     * @param parameters Parameters.
     */
    public TypeParameters(final Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     * Return number of arguments.
     * @return Number of arguments.
     */
    public final int count() {
        return this.parameters.count();
    }

    /**
     * Checks for consistency the order of generic type parameters and
     * their Javadoc parameters.
     * @param tags Javadoc parameter tags.
     * @param consumer Consumer accepts JavadocTag which is located out of
     *  order.
     */
    public final void checkOrder(
        final List<JavadocTag> tags, final Consumer<JavadocTag> consumer
    ) {
        final List<DetailAST> params = this.parameters.parameters();
        if (tags.size() < params.size()) {
            throw new IllegalStateException(
                "Number of Javadoc parameters does not match the number of type parameters"
            );
        }
        final Iterator<JavadocTag> iterator =
            tags.listIterator(tags.size() - params.size());
        for (final DetailAST param : params) {
            final String type =
                param.findFirstToken(TokenTypes.IDENT).getText();
            final JavadocTag tag = iterator.next();
            final String arg = tag.getFirstArg();
            if (!arg.equals(String.format("<%s>", type))) {
                consumer.accept(tag);
            }
        }
    }
}
