/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

/**
 * Maintains information about class' ctors.
 * @since 0.1
 */
final class ClassDesc {

    /**
     * Qualified class name(with package).
     */
    private final String qualified;

    /**
     * Is class declared as final.
     */
    private final boolean asfinal;

    /**
     * Is class declared as abstract.
     */
    private final boolean asabstract;

    /**
     * Create a new ClassDesc instance.
     * @param qualified Qualified class name(with package)
     * @param asfinal Indicates if the class declared as final
     * @param asabstract Indicates if the class declared as
     *  abstract
     */
    ClassDesc(final String qualified, final boolean asfinal,
        final boolean asabstract
    ) {
        this.qualified = qualified;
        this.asfinal = asfinal;
        this.asabstract = asabstract;
    }

    /**
     * Get qualified class name.
     * @return Qualified class name
     */
    String getQualified() {
        return this.qualified;
    }

    /**
     * Is class declared as final.
     * @return True if class is declared as final
     */
    boolean isAsfinal() {
        return this.asfinal;
    }

    /**
     * Is class declared as abstract.
     * @return True if class is declared as final
     */
    boolean isDeclaredAsAbstract() {
        return this.asabstract;
    }
}
