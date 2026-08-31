/**
 *
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */

def log = new File(basedir, 'build.log')
assert log.text.findAll('.*Brackets.java.*BracketsStructureCheck').size() == 4
assert log.text.contains('Tabs.java[8]: Line contains a tab character. (FileTabCharacterCheck)')
assert log.text.findAll('Brackets.java.*(HeaderCheck)').empty
//assert log.text.contains('Violations.java[6]: This method must be static, because it does not refer to "this"')
assert log.text.contains('Brackets.java[28]: Closing bracket should be on a new line (BracketsStructureCheck)')
assert log.text.contains('Violations.java[19]: Parameter txt should be final. (FinalParametersCheck)')
assert log.text.contains('Violations.java[30]: Parameter ex should be final. (FinalParametersCheck)')
assert log.text.contains('Violations.java[38]: ArrayList should be initialized with a size parameter')
assert log.text.contains('Violations.java[39]: ArrayList should be initialized with a size parameter')
assert log.text.contains('Violations.java[48]: Lists.newArrayList should be initialized with a size parameter')
assert log.text.contains('Violations.java[49]: Lists.newArrayList should be initialized with a size parameter')
assert !log.text.contains('Got an exception - java.lang.NullPointerException')
assert log.text.findAll('SomeTest.java .+ (JavadocMethodCheck)').isEmpty()
assert !log.text.contains('IndentationChecks.java[49]: method call rparen at indentation level 12')
assert log.text.contains('Constants.java[12]: Private constant "ONCE" is used only once, inline it (SingleUseConstantCheck)')
assert !log.text.contains('Private constant "TWICE" is used only once')
assert log.text.contains('Violations.java[39]: Fully qualified "java.util.ArrayList" is redundant, import it and use "ArrayList" (FullyQualifiedTypeCheck)')
assert log.text.contains('Violations.java[49]: Fully qualified "com.google.common.collect.Lists" is redundant, import it and use "Lists" (FullyQualifiedTypeCheck)')
assert log.text.contains('Violations.java[10]: Implicit constructor of "Violations" gets no Javadoc, declare it explicitly (ImplicitConstructorCheck)')
