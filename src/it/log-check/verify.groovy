/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

def log = new File(basedir, 'build.log')
assert log.text.contains('Checking compile classpath')
assert log.text =~ /Qulice checked 0 \.java files against 0 rules in \d+(ms|s|min|hr)/
