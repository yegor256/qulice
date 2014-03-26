package com.qulice.checkstyle.ChecksTest.CurlyBracketsStructureCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    // Check static declarations.
    static {
        private String[] array = new String[] {
            "first", "second"};
        public String[] array = new String[] {
            "first",
            "second"};
        public String[] arr = {"first", "second"
        };
        public String[] arr = {
            "first",
            "second"};
    }
    // Check instance declarations.
    {
        private String[] array = new String[] {
            "first", "second"};
        public String[] array = new String[] {
            "first",
            "second"};
        public String[] arr = {"first", "second"
        };
        public String[] arr = {
            "first",
            "second"};
    }
    // Check method declarations.
    public void main() {
        String[] array = new String[] {
            "first", "second"};
        String[] array = new String[] {
            "first",
            "second"};
        String[] arr = {"first", "second"
        };
        String[] arr = {
            "first",
            "second"};
    }
}
