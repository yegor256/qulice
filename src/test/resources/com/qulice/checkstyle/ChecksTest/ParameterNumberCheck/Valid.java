/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle.ChecksTest.ParameterNumberCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    private final int first;
    private final int second;
    private final int third;
    private final int fourth;
    public Valid(final int one, final int two, final int three, final int four) {
        this.first = one;
        this.second = two;
        this.third = three;
        this.fourth = four;
    }
    public int sum(final int one, final int two, final int three) {
        return this.first + this.second + this.third + this.fourth + one + two + three;
    }
}

class FewerParamsThanAttributes {
    private final int alpha;
    private final int beta;
    private final int gamma;
    private final int delta;
    FewerParamsThanAttributes(final int one, final int two) {
        this.alpha = one;
        this.beta = two;
        this.gamma = 0;
        this.delta = 0;
    }
}

record Coordinates(int first, int second, int third, int fourth) {
    Coordinates(final int first, final int second, final int third, final int fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }
}

class Overriding {
    @Override
    public void accept(final int one, final int two, final int three, final int four) {
    }
}

class PrivateMethods {
    private int mix(final int one, final int two, final int three, final int four) {
        return one + two + three + four;
    }
    private static int blend(final int one, final int two, final int three, final int four) {
        return one * two * three * four;
    }
}

class StaticsAndAttributes {
    private static final int LIMIT = 10;
    private final int alpha;
    private final int beta;
    private final int gamma;
    private final int delta;
    StaticsAndAttributes(final int one, final int two, final int three, final int four) {
        this.alpha = one;
        this.beta = two;
        this.gamma = three;
        this.delta = four;
    }
}
