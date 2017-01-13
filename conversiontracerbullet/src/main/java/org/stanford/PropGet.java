package org.stanford;

import java.io.*;
import java.util.Properties;

class PropGet {

    static Properties getProps(String propFile) throws NullPointerException, IOException {

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
