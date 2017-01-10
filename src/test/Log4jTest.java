package test;

import junit.framework.TestCase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author dlweber
 *
 */
public class Log4jTest extends TestCase {

    private static Logger log = LogManager.getLogger(Log4jTest.class.getName());

    /**
     * test message to log4j.
     */
    public final void testLog() {
        log.trace("Trace message.");
        log.debug("Debug message.");
        log.info("Info message.");
        log.warn("Warn message.");
        log.error("Error message.");
        log.fatal("Fatal message.");
        assertTrue(true);
    }

}