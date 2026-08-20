/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

interface Foo {
  final class Bar implements Foo {
    private static final Pattern TEST =
      Pattern.compile("hey");
    String doSomething() {
      return Foo.Bar.TEST.toString();
    }
  }
}
