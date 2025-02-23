/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.nio.file.Files;
import java.nio.file.Paths;

public final class FilesCreateFileOther {
    public void other() {
        Files.createFile(Paths.get("test"));
    }
}
