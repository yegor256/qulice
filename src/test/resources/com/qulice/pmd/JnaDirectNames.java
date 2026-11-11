/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class JnaDirectNames implements com.sun.jna.Library {

    public native int CreateFileW(String name, int access);

    public native int GetLastError();
}
