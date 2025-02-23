/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

def log = new File(basedir, 'build.log')
assert log.text.contains("Avoid unused private fields such as 'var'")
assert !log.text.contains('java.lang.Error: Invalid escape character')
// @see https://github.com/tpc2/qulice/issues/146
//assert log.text.contains("Avoid creating unnecessary local variables like 'name'")
//assert log.text.contains("Avoid creating unnecessary local variables like 'message'")
