/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd;

public class FieldInitOneConstructor {
    private final transient Engine engine;
    private final transient Steering steering;

    public FieldInitOneConstructor(final Engine eng, final Steering steer) {
        this.engine = eng;
        this.steering = steer;
    }

    public FieldInitOneConstructor(final Engine eng) {
        this(eng, new DefaultSteering());
    }

    public FieldInitOneConstructor() {
        this(new DefaultEngine(), new DefaultSteering());
    }

    public Engine getEngine() {
        return this.engine;
    }

    public Steering getSteering() {
        return steering;
    }
}
