/*
 * Copyright (c) 2011-2019, Qulice.com
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
package com.qulice.findbugs;

import com.qulice.spi.Environment;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

/**
 * Encapsulates the exclusion rules.
 * @since 0.18.14
 */
public class FindBugsExcludes {

    /**
     * Environment.
     */
    private final Environment env;

    /**
     * Ctor.
     * @param env Environment
     */
    public FindBugsExcludes(final Environment env) {
        this.env = env;
    }

    /**
     * Exclusions as FindBugs filter XML file.
     * @return XML
     */
    public final Document asXml() {
        final Document document = DocumentHelper.createDocument();
        final FindBugsExcludes.ExcludesXml xml = new FindBugsExcludes
            .ExcludesXml(
                document.addElement("FindBugsFilter")
            );
        this.env.excludes("findbugs").stream().forEach(xml::addExclude);
        return document;
    }

    /**
     * Converts excludes to command argument
     * as a path to the file.
     * @return List of arguments (zero or one)
     * @throws IOException If temp file cannot be created
     */
    public final Collection<String> asArguments() throws IOException {
        final Document document = this.asXml();
        final Element root = document.getRootElement();
        final List<String> arguments = new LinkedList<>();
        if (root.hasContent()) {
            final Path temp = Files.createTempFile(
                this.env.tempdir().toPath(),
                "findbug_excludes_",
                ".xml"
            );
            final BufferedWriter writer = Files.newBufferedWriter(temp);
            document.write(writer);
            writer.close();
            arguments.add(temp.toFile().getAbsolutePath());
        }
        return arguments;
    }

    /**
     * Converts the exclusion expressions to the FindBugs exclusion filter XML.
     * http://findbugs.sourceforge.net/manual/filter.html
     */
    static class ExcludesXml {

        /**
         * Name constant.
         */
        private static final String NAME = "name";

        /**
         * Root XML Element of the FindBugs exclusion filter.
         */
        private final Element root;

        /**
         * Constructor.
         * @param root Root element
         */
        ExcludesXml(final Element root) {
            this.root = root;
        }

        /**
         * Converts the exclusion expression into a XML element.
         * @param exclude An exclusion expression
         */
        public void addExclude(final String exclude) {
            if (!exclude.isEmpty()) {
                final Element match = new BaseElement("Match");
                final String[] names = exclude.split(":");
                IntStream.range(0, names.length).forEachOrdered(
                    i -> {
                        final String name = names[i];
                        if (!name.isEmpty()) {
                            switch (i) {
                                case 0:
                                    match.addElement("Class")
                                        .addAttribute(ExcludesXml.NAME, name);
                                    break;
                                case 1:
                                    match.addElement("Method")
                                        .addAttribute(ExcludesXml.NAME, name);
                                    break;
                                case 2:
                                    match.addElement("Bug")
                                        .addAttribute("pattern", name);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                );
                if (match.hasContent()) {
                    this.root.add(match);
                }
            }
        }
    }
}
