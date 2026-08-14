/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

def log = new File(basedir, 'build.log')
assert !log.text.contains('JSR-303 validator failed to initialize')
assert log.text =~ /Qulice checked 2 \.java files against \d+ rules \(\d+ Checkstyle, \d+ PMD\) in \d+(ms|s|min|hr)/
