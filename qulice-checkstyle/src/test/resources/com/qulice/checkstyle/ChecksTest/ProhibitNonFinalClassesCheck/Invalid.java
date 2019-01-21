package com.qulice.checkstyle.ChecksTest.ProhibitNonFinalClassesCheck;

/**
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public class Invalid {
    public void foo() {}

    private static class Inner {
       protected void bar() {}
    }
}
