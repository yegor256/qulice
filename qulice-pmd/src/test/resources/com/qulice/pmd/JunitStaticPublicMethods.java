package foo.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

public class SomeTest {

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

}