/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.function.IntSupplier;

public final class UnusedPrivateFieldInLambda {
    public static final class Alpha {
        private static int count;

        @SuppressWarnings("PMD.AssignmentToNonFinalStatic")
        Alpha() {
            final IntSupplier sup = () -> {
                --UnusedPrivateFieldInLambda.Alpha.count;
                final int result;
                if (UnusedPrivateFieldInLambda.Beta.counter <= 0) {
                    result = 0;
                } else {
                    result = 1;
                }
                return result;
            };
        }
    }

    public static final class Beta {
        static int counter;
    }
}
