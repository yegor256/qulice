/*
 * Hello.
 */
package foo;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Mojo with parameters injected by Maven.
 * @since 1.0
 */
public abstract class MavenParameters {

    /**
     * Directory to scan, injected by Maven.
     */
    @Parameter(property = "foo.dir", defaultValue = "${project.basedir}")
    protected String dir;

    /**
     * Encoding of sources, injected by Maven.
     */
    @org.apache.maven.plugins.annotations.Parameter(property = "foo.charset")
    protected String charset;

    /**
     * Just a field, not a parameter.
     */
    protected String extra;
}
