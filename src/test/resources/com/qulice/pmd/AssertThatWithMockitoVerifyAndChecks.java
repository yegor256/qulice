/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

final class AssertThatWithMockitoVerifyAndChecksTest {

    @Test
    void detectsSuccess() {
        final Pull pull = new Pull();
        final Observer observer = Mockito.mock(Observer.class);
        MatcherAssert.assertThat("ok", true, Matchers.is(true));
        final MkChecks checks = (MkChecks) pull.checks();
        Mockito.verify(observer).onSuccess();
    }

    private static final class Pull {
        Object checks() {
            return null;
        }
    }

    private static final class MkChecks {
    }

    private interface Observer {
        void onSuccess();
    }
}
