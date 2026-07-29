/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

import java.io.IOException;
import java.net.ServerSocket;

public final class UnnecessaryLocalTryWithResources {

    public int free() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (final IOException exception) {
            throw new IllegalStateException(
                "Could not find a free port", exception
            );
        }
    }
}
