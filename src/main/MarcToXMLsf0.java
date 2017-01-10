package main;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class MarcToXMLsf0 {

    private static final Logger log = LogManager.getLogger();

    public static void main (String args[]) throws IOException {

    log.info("\nCONVERTING MARC TO XML\n%n");

    Transform.toXMLrecord(args[0]);

    log.info("DONE WITH MARCXML CONVERSION\n");
  }
}
