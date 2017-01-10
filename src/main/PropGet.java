package main;

import java.io.*;
import java.util.Properties;

class PropGet {

    static Properties getProps() throws NullPointerException, IOException {

        String propFile;
        propFile = "main/resources/server.conf";
        props().load(in(propFile));
        in(propFile).close();
        return props();
    }

    private static Properties props() {
        return new Properties();
    }

    private static FileInputStream in (String propFile) throws FileNotFoundException {
        return new FileInputStream(propFile);
    }
}
