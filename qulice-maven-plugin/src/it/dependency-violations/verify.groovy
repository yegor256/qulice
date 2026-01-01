/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

def log = new File(basedir, 'build.log')
assert log.text.contains('Unused declared dependencies found')
assert log.text.contains('1 dependency problem(s) found')
assert log.text.contains('commons-lang:commons-lang:jar:2.5:compile')
assert !log.text.contains('commons-io:commons-io:jar:2.0.1:compile')
