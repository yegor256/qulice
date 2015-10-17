/**
 * Copyright (c) 2011-2015, Qulice.com
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
package com.qulice.xml;

import com.jcabi.log.Logger;
import com.jcabi.xml.StrictXML;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

/**
 * Validates XML files for formatting.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@SuppressWarnings(
    { "PMD.AvoidInstantiatingObjectsInLoops", "PMD.ExceptionAsFlowControl" }
)
public final class XmlValidator implements Validator {

    /**
     * New line escaped, for use in regex.
     */
    private static final String ESCAPED_EOL = "\\n";

    /**
     * Pattern for lines starting with XML attributes.
     */
    private static final Pattern ATTRS_PATTERN = Pattern.compile("^ *[^ <].*");

    /**
     * Should XML format be checked.
     */
    private final transient boolean format;

    /**
     * Constructor.
     */
    public XmlValidator() {
        this(true);
    }

    /**
     * Constructor with XML formatting option.
     * @param formatting Should XML formatting be checked.
     */
    public XmlValidator(final boolean formatting) {
        this.format = formatting;
    }

    @Override
    public void validate(final Environment env) throws ValidationException {
        try {
            for (final File file : env.files("*.xml")) {
                final String name = file.getAbsolutePath().substring(
                    env.basedir().toString().length()
                );
                if (env.exclude("xml", name)) {
                    Logger.info(this, "%s: skipped", name);
                    continue;
                }
                Logger.info(this, "%s: to be validated", name);
                final XML document = new XMLDocument(file);
                try {
                    new StrictXML(document);
                } catch (final IllegalStateException ex) {
                    if (ex.getCause() != null
                        && ex.getCause() instanceof SAXException) {
                        Logger.warn(
                            // @checkstyle LineLength (1 line)
                            this, "Failed to validate file %s against schema(s). Cause: %s",
                            name,
                            ex.toString()
                        );
                    } else {
                        throw new IllegalStateException(ex);
                    }
                } catch (final IllegalArgumentException ex) {
                    throw new ValidationException(ex);
                }
                if (this.format) {
                    this.formatting(
                        file.toString(), FileUtils.readFileToString(file)
                    );
                }
            }
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Check formatting of given XML.
     * @param name Name of XML file.
     * @param before XML to check.
     * @throws ValidationException In case of validation error.
     */
    private void formatting(final String name, final String before)
        throws ValidationException {
        // @checkstyle MultipleStringLiterals (3 lines)
        final String after = new Prettifier().prettify(before)
            .replace("\r\n", "\n");
        final String bnormalized = before.replace("\r\n", "\n");
        final List<String> blines =
            Arrays.asList(bnormalized.split(XmlValidator.ESCAPED_EOL, -1));
        final Patch filter = this.filter(
            DiffUtils.diff(
                blines,
                Arrays.asList(after.split(XmlValidator.ESCAPED_EOL, -1))
            )
        );
        if (!filter.getDeltas().isEmpty()) {
            final int context = 5;
            throw new ValidationException(
                // @checkstyle LineLength (1 line)
                "The provided XML %s is not well formatted, it should look like this:\n%s\npatch:\n%s",
                name, after,
                StringUtils.join(
                    DiffUtils.generateUnifiedDiff(
                        "before", "after", blines,
                        filter, context
                    ), "\n"
                )
            );
        }
    }

    /**
     * Remove unwanted deltas.
     * @param diff Patch to filter.
     * @return Patch with unwanted deltas removed.
     * @todo #469:30min Remove the method below and find a way to format tags
     *  correctly in XML. Attributes should be indented by 4 spaces, just like
     *  XML tags, but in IT xml-violations there is a tag that our Prettifier
     *  want to be indented by 3 spaces which is wrong. Another problem is
     *  that in the parent tag, attributes are indented to match the first
     *  attribute, this is also wrong - all attributes on new line should be
     *  indented by 4 spaces.
     */
    private Patch filter(final Patch diff) {
        final Patch patch = new Patch();
        for (final Delta delta : diff.getDeltas()) {
            final List<?> prev = delta.getOriginal().getLines();
            if (
                prev.size() != 1 || delta.getRevised().getLines().size() != 1
                    || !XmlValidator.ATTRS_PATTERN
                        .matcher(prev.get(0).toString()).matches()
                ) {
                patch.addDelta(delta);
            }
        }
        return patch;
    }
}
