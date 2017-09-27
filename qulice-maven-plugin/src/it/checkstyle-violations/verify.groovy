/**
 *
 * Copyright (c) 2011, Qulice.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * @version $Id$
 *
 * Validate that the build really failed and violations were reported.
 */

def log = new File(basedir, 'build.log')
assert log.text.findAll('.*Brackets.java.*BracketsStructureCheck').size() == 4
assert log.text.contains('Tabs.java[4]: Line contains a tab character. (FileTabCharacterCheck)')
assert log.text.contains('tabs.txt[1]: Line contains a tab character. (FileTabCharacterCheck)')
assert log.text.findAll('Brackets.java.*(HeaderCheck)').empty
//assert log.text.contains('Violations.java[6]: This method must be static, because it does not refer to "this"')
assert log.text.contains('Brackets.java[57]: Closing bracket should be on a new line (BracketsStructureCheck)')
assert log.text.contains('Violations.java[14]: Parameter txt should be final. (FinalParametersCheck)')
assert log.text.contains('Violations.java[25]: Parameter ex should be final. (FinalParametersCheck)')
assert log.text.contains('NewLines.java[4]: Lines in file should end with Unix-like end of line')
assert log.text.contains('newlines.txt[3]: Lines in file should end with Unix-like end of line')
assert log.text.contains('Violations.java[33]: ArrayList should be initialized with a size parameter')
assert log.text.contains('Violations.java[34]: ArrayList should be initialized with a size parameter')
assert log.text.findAll('Pdd.java.*: .todo tag has wrong format').empty
assert !log.text.contains('Got an exception - java.lang.NullPointerException')
//assert !log.text.contains('SomeTest.java[5]: This method must be static, because it does not refer to "this"')
assert !log.text.contains('IndentationChecks.java[19]: method call rparen at indentation level 12')
