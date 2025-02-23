/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package emp;

class EmptyStatementNotInLoop {
    public void bar() {
        // this is probably not what you meant to do
        ;
        // the extra semicolon here this is not necessary
    }
}
