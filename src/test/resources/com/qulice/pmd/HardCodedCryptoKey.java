/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import javax.crypto.spec.SecretKeySpec;

public final class HardCodedCryptoKey {

    public SecretKeySpec key() {
        return new SecretKeySpec("secretpassword12".getBytes(), "AES");
    }
}
