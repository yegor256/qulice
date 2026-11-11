/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

def log = new File(basedir, 'build.log')
assert log.text.contains('Qulice checked')
assert !log.text.contains('SiteSample.java')
assert !log.text.contains('Sample.java')
assert log.text.contains('BUILD SUCCESS')
