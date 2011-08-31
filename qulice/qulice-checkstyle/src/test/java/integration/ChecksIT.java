/**
 * Copyright (c) 2011, Qulice.com
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
package integration;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.xml.sax.InputSource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id: SvnPropertiesCheckTest.java 8 2011-08-25 01:01:25Z yegor256@yahoo.com $
 */
@RunWith(Parameterized.class)
public class ChecksIT {

    /**
     * Directories where test scripts are located.
     */
    private static final String[] DIRS = {
        "EmptyLinesCheck",
        "ImportCohesionCheck",
        "JavadocTagsCheck",
        "PuzzleFormatCheck",
        "CascadeIndentationCheck",
    };

    private final String dir;

    public ChecksIT(final String name) {
        this.dir = "ChecksIT/" + name;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> dirs() {
        final Collection<Object[]> dirs = new ArrayList<Object[]>();
        for (String url : ChecksIT.DIRS) {
            dirs.add(new Object[] {url});
        }
        return dirs;
    }

    @Test
    public void testCheckstyleTruePositive() throws Exception {
        final AuditListener listener = mock(AuditListener.class);
        final Collector collector = new ChecksIT.Collector();
        doAnswer(collector).when(listener).addError((AuditEvent) anyObject());
        this.check("/Invalid.java", listener);
        final String[] violations = StringUtils.split(
            IOUtils.toString(
                this.getClass()
                .getResourceAsStream(this.dir + "/violations.txt")
            ),
            "\n"
        );
        for (String line : violations) {
            final String[] sectors = StringUtils.split(line, ":");
            final Integer pos = Integer.valueOf(sectors[0]);
            assertThat(
                collector.has(pos, sectors[1]),
                describedAs(
                    String.format(
                        "Line no.%d ('%s') not reported by %s: '%s'",
                         pos,
                         sectors[1],
                         this.dir,
                         collector.summary()
                    ),
                    is(true)
                )
            );
        }
    }

    @Test
    public void testCheckstyleTrueNegative() throws Exception {
        final AuditListener listener = mock(AuditListener.class);
        this.check("/Valid.java", listener);
        verify(listener, times(0)).addError((AuditEvent) anyObject());
    }

    private static class Collector implements Answer {
        private final List<AuditEvent> events = new ArrayList<AuditEvent>();
        @Override
        public Object answer(final InvocationOnMock invocation) {
            this.events.add((AuditEvent) invocation.getArguments()[0]);
            return null;
        }
        public boolean has(final Integer line, final String msg) {
            for (AuditEvent event : this.events) {
                if (event.getLine() == line && event.getMessage().equals(msg)) {
                    return true;
                }
            }
            return false;
        }
        public String summary() {
            final List<String> msgs = new ArrayList<String>();
            for (AuditEvent event : this.events) {
                msgs.add(event.getLine() + ":" + event.getMessage());
            }
            return StringUtils.join(msgs, "; ");
        }
    }

    private void check(final String name, final AuditListener listener)
        throws Exception {
        final Checker checker = new Checker();
        final InputSource src = new InputSource(
            this.getClass().getResourceAsStream(this.dir + "/config.xml")
        );
        checker.setClassloader(this.getClass().getClassLoader());
        checker.setModuleClassLoader(this.getClass().getClassLoader());
        checker.configure(
            ConfigurationLoader.loadConfiguration(
                src,
                new PropertiesExpander(new Properties()),
                true
            )
        );
        final List<File> files = new ArrayList<File>();
        files.add(
            new File(this.getClass().getResource(this.dir + name).getFile())
        );
        checker.addListener(listener);
        checker.process(files);
        checker.destroy();
    }

}
