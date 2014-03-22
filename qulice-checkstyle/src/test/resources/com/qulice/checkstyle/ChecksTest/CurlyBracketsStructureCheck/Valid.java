package com.qulice.checkstyle.ChecksTest.CurlyBracketsStructureCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {

    // Check static declarations.
    static {
        new String[] {"first", "second"};
        new String[] {
            "first", "second"
        };
        new String[] {
            "first",
            "second"
        };
        String[] arr = {"first", "second"};
        String[] arr = new String[] {
            "first", "second"
        };
        String[] arr = {
            "first",
            "second"
        };
    }
    // Check instance declarations.
    {
        new String[] {"first", "second"};
        new String[] {
            "first", "second"
        };
        new String[] {
            "first",
            "second"
        };
        String[] arr = {"first", "second"};
        String[] arr = new String[] {
            "first", "second"
        };
        String[] arr = {
            "first",
            "second"
        };
    }
    // Check method declarations.
    public void main() {
        new String[] {"first", "second"};
        new String[] {
            "first", "second"
        };
        new String[] {
            "first",
            "second"
        };
        String[] arr = {"first", "second"};
        String[] arr = new String[] {
            "first", "second"
        };
        String[] arr = {
            "first",
            "second"
        };
    }
}
