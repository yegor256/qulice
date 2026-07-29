/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public class ManyOverriddenMethods implements Wide {

    @Override
    public void first() {
        throw new IllegalStateException("never");
    }

    @Override
    public void second() {
        throw new IllegalStateException("never");
    }

    @Override
    public void third() {
        throw new IllegalStateException("never");
    }

    @Override
    public void fourth() {
        throw new IllegalStateException("never");
    }

    @Override
    public void fifth() {
        throw new IllegalStateException("never");
    }

    @Override
    public void sixth() {
        throw new IllegalStateException("never");
    }

    @Override
    public void seventh() {
        throw new IllegalStateException("never");
    }

    @Override
    public void eighth() {
        throw new IllegalStateException("never");
    }

    @Override
    public void ninth() {
        throw new IllegalStateException("never");
    }

    @Override
    public void tenth() {
        throw new IllegalStateException("never");
    }

    @Override
    public void eleventh() {
        throw new IllegalStateException("never");
    }
}
