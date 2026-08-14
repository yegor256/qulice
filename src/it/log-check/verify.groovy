/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

def log = new File(basedir, 'build.log')
assert log.text.contains('Checking compile classpath')
assert log.text =~ /Qulice checked \d+ \.java files? against \d+ rules \(\d+ Checkstyle, \d+ PMD\) in \d+(ms|s|min|hr)/
