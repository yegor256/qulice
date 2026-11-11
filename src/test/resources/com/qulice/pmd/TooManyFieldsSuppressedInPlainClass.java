/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

@SuppressWarnings("PMD.TooManyFields")
public final class TooManyFieldsSuppressedInPlainClass {

    private int one;
    private int two;
    private int three;
    private int four;
    private int five;
    private int six;
    private int seven;
    private int eight;
    private int nine;
    private int ten;
    private int eleven;
    private int twelve;
    private int thirteen;
    private int fourteen;
    private int fifteen;
    private int sixteen;

    public int total() {
        return this.one + this.two + this.three + this.four
            + this.five + this.six + this.seven + this.eight + this.nine
            + this.ten + this.eleven + this.twelve + this.thirteen
            + this.fourteen + this.fifteen + this.sixteen;
    }
}
