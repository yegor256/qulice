/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.maven;

import com.qulice.spi.Environment;
import com.qulice.spi.Validator;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A test fake {@link Validator} that remembers the value one
 * {@link Environment} parameter had when the validator was called.
 * @since 1.0
 */
final class RecordingValidator implements Validator {

    /**
     * Name of the parameter to remember.
     */
    private final String param;

    /**
     * What the parameter was, or empty if the validator was never called.
     */
    private final AtomicReference<String> value;

    RecordingValidator(final String name) {
        this.param = name;
        this.value = new AtomicReference<>("");
    }

    @Override
    public void validate(final Environment env) {
        this.value.set(env.param(this.param, ""));
    }

    @Override
    public String name() {
        return "Recording";
    }

    String seen() {
        return this.value.get();
    }
}
