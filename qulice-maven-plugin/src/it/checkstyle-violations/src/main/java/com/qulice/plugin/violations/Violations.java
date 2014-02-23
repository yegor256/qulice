package com.qulice.plugin.violations;
public class Violations {
    public void test() {
        System.setProperty("test", "test value");
    }

    /**
     * Test method.
     */
    public final void foreach() {
        for (String txt : new String[] {"test"}) {
            System.out.println(txt);
        }
    }
}
