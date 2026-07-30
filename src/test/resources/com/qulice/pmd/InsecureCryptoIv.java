/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import javax.crypto.spec.IvParameterSpec;

public final class InsecureCryptoIv {

    public IvParameterSpec vector() {
        return new IvParameterSpec(new byte[] {1, 2, 3, 4, 5, 6, 7, 8});
    }
}
