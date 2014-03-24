package com.qulice.checkstyle.ChecksTest.ProtectedMethodInFinalClassCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public final class Invalid {
    public void foo() {}
    protected void invalid() {}
    private void bar() {}
    protected void invalid2() {}

    private static final class Bar {
       protected void valid() {}
    }
}
