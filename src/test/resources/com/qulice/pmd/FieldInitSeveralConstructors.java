/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public class FieldInitSeveralConstructors {
    private final transient Engine engine;
    private final transient Steering steering;

    public FieldInitSeveralConstructors(final Engine eng, final Steering steer) {
        this.engine = eng;
        this.steering = steer;
    }

    public FieldInitSeveralConstructors(final Engine eng) {
        this.engine = eng;
        this.steering = new DefaultSteering();
    }

    public FieldInitSeveralConstructors() {
        this(new DefaultEngine(), new DefaultSteering());
    }

    public Engine getEngine() {
        return this.engine;
    }

    public Steering getSteering() {
        return steering;
    }
}
