package com.qulice.foo;
import org.apache.commons.io.IOUtils;
public class Sample {
    public synchronized void test() {
        IOUtils.toInputStream("oops");
        System.out.println("test");
    }
}
