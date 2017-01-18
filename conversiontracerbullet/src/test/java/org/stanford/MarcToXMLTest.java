package org.stanford;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.FileInputStream;

import org.marc4j.*;

/**
 * Created by Joshua Greben jgreben on 1/13/17.
 * Stanford University Libraries, DLSS
 */
public class MarcToXMLTest {

    private String[] marcfile = new String[1];

    @Before
    public void setUp() {
        marcfile[0] = "/sample_marc.mrc";
    }


    @Test
    public void main() throws Exception {

        String marc = getClass().getResource(marcfile[0]).getFile();
        assertTrue(new MarcStreamReader(new FileInputStream(marc)).hasNext());
    }
}