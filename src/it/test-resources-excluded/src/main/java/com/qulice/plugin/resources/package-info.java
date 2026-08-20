/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Integration test for the default exclusion of
 * {@code src/test/resources}: Java files that live there are fixtures of
 * the tests, not sources of the product, and neither Checkstyle, PMD nor
 * ErrorProne may complain about them, even though the project says
 * nothing about them in its {@code <excludes>}.
 * @since 1.0
 */
package com.qulice.plugin.resources;
