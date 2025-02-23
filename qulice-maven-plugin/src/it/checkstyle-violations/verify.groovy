/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

def log = new File(basedir, 'build.log')
assert log.text.findAll('.*Brackets.java.*BracketsStructureCheck').size() == 4
assert log.text.contains('Tabs.java[34]: Line contains a tab character. (FileTabCharacterCheck)')
assert log.text.findAll('Brackets.java.*(HeaderCheck)').empty
//assert log.text.contains('Violations.java[6]: This method must be static, because it does not refer to "this"')
assert log.text.contains('Brackets.java[54]: Closing bracket should be on a new line (BracketsStructureCheck)')
assert log.text.contains('Violations.java[44]: Parameter txt should be final. (FinalParametersCheck)')
assert log.text.contains('Violations.java[55]: Parameter ex should be final. (FinalParametersCheck)')
assert log.text.contains('Violations.java[63]: ArrayList should be initialized with a size parameter')
assert log.text.contains('Violations.java[64]: ArrayList should be initialized with a size parameter')
assert !log.text.contains('Got an exception - java.lang.NullPointerException')
assert log.text.findAll('SomeTest.java .+ (JavadocMethodCheck)').isEmpty()
assert !log.text.contains('IndentationChecks.java[49]: method call rparen at indentation level 12')
