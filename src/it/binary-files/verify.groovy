/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

def log = new File(basedir, 'build.log')
assert !log.text.contains('pixel.png')
assert !log.text.contains('sample.gif')
assert !log.text.contains('RegexpSinglelineCheck')
assert log.text.contains('BUILD SUCCESS')
