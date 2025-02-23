/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
def log = new File(basedir, 'build.log')
assert !log.text.contains('Caused by: java.lang.NullPointerException')
assert log.text.contains('Unused declared dependencies found')
assert log.text.contains('1 dependency problem(s) found')
assert log.text.contains('org.hibernate:hibernate-entitymanager:jar:5.0.7.Final:compile')
