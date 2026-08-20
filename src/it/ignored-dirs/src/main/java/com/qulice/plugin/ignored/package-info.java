/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

/**
 * Integration test for the directories Qulice ignores by default:
 * {@code src/site} and {@code src/it}. Both are declared as resource
 * directories in the POM, so that Qulice walks them, and both hold a
 * Java file that breaks plenty of rules. The build must stay green
 * anyway, with no {@code <exclude>} in the POM.
 * @since 1.0
 */
package com.qulice.plugin.ignored;
