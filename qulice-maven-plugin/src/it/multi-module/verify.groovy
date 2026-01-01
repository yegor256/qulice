/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

// Let's validate that the build never touched Groovy files
// see ticket #39 for explanation
def log = new File(basedir, 'build.log')
assert !log.text.contains('CodeNarc completed:')
