/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Integration test for <a href="https://github.com/yegor256/qulice/issues/1264">
 * issue #1264</a>: binary resources (PNG, GIF, etc.) sitting in
 * {@code src/main/resources} must not be inspected by text-based checks
 * such as {@code RegexpSinglelineCheck}.
 * @since 0.24
 */
package com.qulice.plugin.binary;
