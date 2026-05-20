/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

public final class ExceptionAsFlowControl {

    public void register(final String host, final String login,
        final String key) {
        try {
            if (host.isEmpty()) {
                throw new IllegalArgumentException("SSH host is empty");
            }
            if (login.isEmpty()) {
                throw new IllegalArgumentException("SSH login is empty");
            }
            if (key.isEmpty()) {
                throw new IllegalArgumentException("SSH key is empty");
            }
        } catch (final IllegalArgumentException ex) {
            System.out.println(
                String.format("Failed to read profile: %s", ex.getMessage())
            );
        }
    }
}
