/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
def log = new File(basedir, 'build.log')
assert !log.text.contains('Unused declared dependencies found')
assert !log.text.contains('dependency problem(s) found')
assert log.text.contains('No dependency problems found')
