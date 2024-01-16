/*
 * Copyright (c) 2011-2024 Qulice.com
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.ant;

import org.apache.tools.ant.types.Path;

/**
 * Represents subset of org.apache.tools.ant.types.Path API which is relevant to Qulice.
 *
 * @since 1.0
 */
public interface AntPath {
    /**
     * Returns all indivudual pathes of this path.
     * @return List of elements.
     */
    String[] list();

    /**
     * Default implementation which wraps Ant path.
     * @since 1.0
     */
    class Default implements AntPath {
        /**
         * Wrapped path.
         */
        private final Path path;

        /**
         * Returns AntPath equivalent to the given path.
         * @param path A path to wrap.
         */
        Default(final Path path) {
            this.path = path;
        }

        @Override
        public String[] list() {
            return this.path.list();
        }
    }

    /**
     * Simple implementation for tests.
     * @since 1.0
     */
    class Fake implements AntPath {
        /**
         * Result for the list() method.
         */
        private final String[] listres;

        /**
         * Creates fake AntPath.
         * @param listres Array to return from the list(), assumed to be immutable.
         * @since 1.0
         */
        Fake(final String... listres) {
            this.listres = listres;
        }

        @Override
        public String[] list() {
            return this.listres;
        }
    }
}
