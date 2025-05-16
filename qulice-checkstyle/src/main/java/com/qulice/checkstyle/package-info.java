/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Checkstyle integration for Qulice framework.
 * This package contains custom Checkstyle checks and integration code that implements
 * the Qulice validation rules using Checkstyle's infrastructure.
 *
 * Key components:
 * - {@link com.qulice.checkstyle.CheckstyleValidator}: Main validator implementation
 * - {@link com.qulice.checkstyle.CheckstyleListener}: Handles Checkstyle events
 * - Custom checks implementing specific Qulice rules
 *
 * The package provides a bridge between Qulice's validation framework and Checkstyle's
 * powerful static analysis capabilities, allowing for consistent code quality checks
 * across different types of Java projects.
 *
 * @since 0.3
 */
package com.qulice.checkstyle;
