package com.qulice.checkstyle.ChecksTest.MethodsOrderCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Valid {
    public Valid() {
    }
    public Valid(String name, String value) {
    }
    protected Valid(String name, String value) {
    }
    protected void print(){
    }
    private void print(String format, String[] text) {
    }
    private void print(String format, String text ) {
    }

    static class StaticInner {
        public Valid() {
        }
        public Valid(String name, String value) {
        }
        protected Valid(String name, String value) {
        }
        protected void print(){
        }
        private void print(String format, String[] text) {
        }
        private Valid(String name, String value) {
        }
    }

    class Inner {
        public Valid() {
        }
        public Valid(String name, String value) {
        }
        protected Valid(String name, String value) {
        }
        protected void print(){
        }
        private void print(String format, String[] text) {
        }
        private Valid(String name, String value) {
        }
    }
}
