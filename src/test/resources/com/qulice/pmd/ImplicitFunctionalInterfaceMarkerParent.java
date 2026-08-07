/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.Serializable;

public interface ImplicitFunctionalInterfaceMarkerParent extends Serializable {
    String exception(String text);
}
