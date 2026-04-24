/*
 * Hello.
 */
package foo;

/**
 * Declares enum constants with names that violate the required
 * upper-case, underscore-separated convention.
 * @since 1.0
 */
public enum InvalidEnumValues {

    /**
     * Camel case name is forbidden.
     */
    anyName,

    /**
     * Mixed case name is forbidden.
     */
    MixedCase,

    /**
     * Lowercase name is forbidden.
     */
    lowercase;
}
