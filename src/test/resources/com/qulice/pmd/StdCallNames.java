/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public interface StdCallNames extends com.sun.jna.win32.StdCallLibrary {

    int CreateFileW(String name, int access);

    int GetLastError();
}
