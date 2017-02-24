[![Build Status](https://travis-ci.org/sul-dlss/ld4p-marcxml-to-bibframe1
.svg?branch=master)](https://travis-ci.org/sul-dlss/ld4p-marcxml-to-bibframe1
)
[![Coverage Status](https://coveralls.io/repos/github/sul-dlss/ld4p-marcxml-to-bibframe1
/badge.svg?branch=master)](https://coveralls.io/github/sul-dlss/ld4p-marcxml-to-bibframe1?branch=master)
[![Dependency Status](https://gemnasium.com/badges/github.com/sul-dlss/ld4p-marcxml-to-bibframe1.svg)](https://gemnasium.com/github.com/sul-dlss/ld4p-marcxml-to-bibframe1)

# ld4p-marc21-to-xml
Convert marcxml data into bibframe1 rdfxml, using converter written by Library of Congress, available at https://github.com/lcnetdev/marc2bibframe

## Development

### Dependencies / Prerequisites

- Java 8
- Maven 3
- Library of Congress Marc to Bibframe1 converter, installed at loc_marc2bibframe directory:

  `git clone https://github.com/lcnetdev/marc2bibframe.git loc_marc2bibframe`

### Compiling and Executing

To compile and package the maven project:

  `mvn clean package`

The resulting packaged JAR at `java/target/xform-marcxml-to-bf1-jar-with-dependencies.jar` includes all dependencies.

(Actually, the java code isn't currently used, and we really only need `saxon9he.jar` to run the converter.)

### Code Coverage Reports

To run the tests and view a coverage report from the command line:
```
mvn clean cobertura:cobertura
ls -l target/site/cobertura/
firefox target/site/cobertura/index.html
```

The [Travis CI](https://travis-ci.org/sul-dlss/ld4p-marcxml-to-bibframe1) builds run tests and submit
a coverage report to [Coveralls](https://coveralls.io/github/sul-dlss/ld4p-marcxml-to-bibframe1).
To update Coveralls from the command line, try:

  `mvn clean test cobertura:cobertura coveralls:report -DrepoToken=yourcoverallsprojectrepositorytoken`

## Deployment

Capistrano is used for deployment.

1. On your laptop, run

    `bundle`

  to install the Ruby capistrano gems and other dependencies for deployment.

2. Set up shared directories on the remote VM:

    ```
    cd ld4p-marcxml-to-bibframe1
    mkdir shared
    mkdir shared/log
    git clone https://github.com/lcnetdev/marc2bibframe.git shared/loc_marc2bibframe
    ```

3. Deploy code to remote VM:

    `cap dev deploy`

  This will also build and package the code on the remote VM with Maven.

4. Run a test marcxml file through the converter to ensure it works on remote VM:

    `cap dev deploy:run_test`
