/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.Collections;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

final class MatcherAssertBooleanWithMessageTest {

    @Test
    void checksBooleanWithMessage() {
        MatcherAssert.assertThat(
            "Collection should be empty",
            Collections.emptyList().isEmpty()
        );
    }
}
