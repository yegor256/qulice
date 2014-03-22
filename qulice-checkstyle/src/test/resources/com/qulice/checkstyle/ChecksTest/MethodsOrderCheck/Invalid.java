package com.qulice.checkstyle.ChecksTest.MethodsOrderCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    public void print(String format, String text ) {
    }
    private void print(String format, String[] text) {
    }
    protected void print(){
    }
    void print(){
    }
    private void print(String format, String text ) {
    }
    static class InvalidStaticInner {
        protected void print(){
        }
        private void print(String format, String[] text) {
        }
        private Valid(String name, String value) {
        }
        public void print(String format, String text ) {
        }
        void print(){
        }
    }

    class InvalidInner {
        public void print(String format, String text ) {
        }
        protected void print(){
        }
        void print(){
        }
        private void print(String format, String[] text) {
        }
        private Valid(String name, String value) {
        }
        protected void print(){
        }
    }
}
