package test;

import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropGetTest {

    private static final Logger log = LogManager.getLogger();

    @Test
    public void getProps() throws Exception {
        System.out.println("PropGet Test");
        log.info("Testing PropGet");
    }

}