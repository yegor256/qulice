/*
 * Hello.
 */
package foo;

/**
 * Regression for https://github.com/yegor256/qulice/issues/770.
 * A method that implements an interface method must be annotated
 * with &#64;Override. PMD's MissingOverride rule should flag this.
 * @since 0.25
 */
public final class MissingOverrideOnInterfaceImpl
    implements java.util.function.Supplier<String> {

    public String get() {
        return "";
    }
}
