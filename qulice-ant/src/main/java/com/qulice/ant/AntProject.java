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

import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.tools.ant.Project;

/**
 * Represents subset of org.apache.tools.ant.Project API which is relevant to Qulice.
 *
 * @since 1.0
 */
interface AntProject {
    /**
     * Returns Ant project property.
     *
     * @param property Name of the property to get.
     * @return Property value or null.
     */
    String getProperty(String property);

    /**
     * Returns Ant project base directory.
     *
     * @return Base directory.
     */
    File getBaseDir();

    /**
     * Default implementation which wraps Ant Project.
     * @since 1.0
     */
    class Default implements AntProject {
        /**
         * Wrapped project.
         */
        private final Project project;

        /**
         * Creates a new AntProject equivalent to the given Project.
         * @param project Project to wrap.
         */
        Default(final Project project) {
            this.project = project;
        }

        @Override
        public String getProperty(final String property) {
            return this.project.getProperty(property);
        }

        @Override
        public File getBaseDir() {
            return this.project.getBaseDir();
        }
    }

    /**
     * Fake implementation of AntProject which allows caller to
     * customize both methods with lambdas.
     *
     * @since 1.0
     */
    class Fake implements AntProject {
        /**
         * Implementation of the getProperty() method.
         */
        private final Function<String, String> prop;

        /**
         * Implementation of the getBaseDir() method.
         */
        private final Supplier<File> basedir;

        /**
         * Creates a new FakeAntProject which will use provided
         * functions.
         *
         * @param prop Implementation of the getProperty(String).
         * @param basedir Implementation of the getBaseDir().
         */
        Fake(
            final Function<String, String> prop,
            final Supplier<File> basedir
        ) {
            this.prop = prop;
            this.basedir = basedir;
        }

        @Override
        public String getProperty(final String property) {
            return this.prop.apply(property);
        }

        @Override
        public File getBaseDir() {
            return this.basedir.get();
        }
    }
}
