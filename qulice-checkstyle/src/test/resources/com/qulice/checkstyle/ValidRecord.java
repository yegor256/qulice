/*
 * Hello.
 */
package foo;

/**
 * Defines a simple record type.
 * @since 1.0
 */
public final class ValidRecord {
    /**
     * Just a record type which should be successfully parsed.
     * @param hello Some field.
     * @param world Another field.
     * @since 1.0
     */
    private record SomeRecord(String hello, int world) {
    }
}
