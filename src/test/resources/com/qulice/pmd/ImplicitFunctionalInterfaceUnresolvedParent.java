/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public interface ImplicitFunctionalInterfaceUnresolvedParent
    extends org.example.Mentioned, org.example.Signature {
    String exception(String text);
}
