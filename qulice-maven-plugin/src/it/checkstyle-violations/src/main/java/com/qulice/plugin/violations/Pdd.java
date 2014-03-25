package com.qulice.plugin.violations;

public class Pdd {

    /**
     * Test.
     * @todo #123:2h This is also valid.
     */
    public void test() {
        new Integer(1);
    }

    /**
     * Test method.
     * @todo #123? This is valid.
     */
    public final void foreach() {
        System.out.println("test");
    }
    /**
     * Test.
     */
    public void inner() {
        // @todo #123 Here we should capture
        //  also another line.
        System.out.println("inner");
    }
}
