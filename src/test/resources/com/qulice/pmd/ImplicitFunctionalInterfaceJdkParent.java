/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.util.Collection;

public interface ImplicitFunctionalInterfaceJdkParent
    extends Collection<String> {
    String exception(String text);
}
