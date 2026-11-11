/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import com.sun.jna.Library;

public interface JnaLibraryNames extends Library {

    int CreateFileW(String name, int access);

    int GetLastError();
}
