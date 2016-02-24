package foo.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

public class SomeTest {

    public static final InnerClass INNER = new InnerClass(10, 10);

    @BeforeClass
    public static void beforeClass(){
        // setup before class
    }

    @Test
    public void emptyTest(){
        //test something
    }

    @AfterClass
    public static void afterClass() throws Exception {
        //tear down after class
    }

    @Parameterized.Parameters
    public static Collection parameters() {
        return Collections.EMPTY_LIST;
    }

    public static class InnerClass {
        private final int number;

        public InnerClass(final int num) {
            this.number = num;
        }

        public int calculate() {
            return number;
        }
    }
}