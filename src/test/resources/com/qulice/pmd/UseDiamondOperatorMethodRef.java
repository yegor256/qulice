/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.math.BigDecimal;
import org.cactoos.iterable.Mapped;
import org.cactoos.number.NumberOfScalars;
import org.cactoos.scalar.Constant;
import org.cactoos.scalar.Reduced;

public final class UseDiamondOperatorMethodRef {

    public Number make(final Iterable<? extends Number> src) {
        return new NumberOfScalars(
            new Reduced<BigDecimal>(
                BigDecimal::multiply,
                new Mapped<>(
                    n -> new Constant<>(new BigDecimal(n.toString())),
                    src
                )
            )
        );
    }
}
