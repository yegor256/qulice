/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class ManyPrivateMethods {

    public void everything() {
        this.first();
        this.second();
        this.third();
        this.fourth();
        this.fifth();
        this.sixth();
        this.seventh();
        this.eighth();
        this.ninth();
        this.tenth();
        this.eleventh();
    }

    private void first() {
        throw new IllegalStateException("never");
    }

    private void second() {
        throw new IllegalStateException("never");
    }

    private void third() {
        throw new IllegalStateException("never");
    }

    private void fourth() {
        throw new IllegalStateException("never");
    }

    private void fifth() {
        throw new IllegalStateException("never");
    }

    private void sixth() {
        throw new IllegalStateException("never");
    }

    private void seventh() {
        throw new IllegalStateException("never");
    }

    private void eighth() {
        throw new IllegalStateException("never");
    }

    private void ninth() {
        throw new IllegalStateException("never");
    }

    private void tenth() {
        throw new IllegalStateException("never");
    }

    private void eleventh() {
        throw new IllegalStateException("never");
    }
}
