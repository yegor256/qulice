/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.Iterator;
import java.util.Map;
import org.cactoos.Func;
import org.cactoos.Scalar;
import org.cactoos.iterable.IterableOf;
import org.cactoos.iterator.IteratorOf;
import org.cactoos.scalar.Constant;
import org.cactoos.scalar.Equals;
import org.cactoos.scalar.Ternary;

public final class UseDiamondOperatorInference<X> {

    public Iterable<X> paged(final Iterable<? extends X> first,
        final Func<? super Iterable<? extends X>, ? extends Iterable<? extends X>> next) {
        return new IterableOf<>(
            () -> new org.cactoos.iterator.Paged<X>(
                first.iterator(),
                page -> next.apply(new IterableOf<>(page)).iterator()
            )
        );
    }

    public Scalar<Boolean> ambiguous(final Map.Entry<Integer, Integer> entry) {
        return new Equals<Integer, Integer>(
            entry::getValue, new Constant<>(Integer.MIN_VALUE)
        );
    }

    public Iterator<String> ternary(final Iterator<Iterator<String>> pages) {
        return new org.cactoos.iterator.Paged<>(
            pages.next(),
            page -> new Ternary<>(
                pages::hasNext, pages::next, () -> new IteratorOf<String>()
            ).value()
        );
    }
}
