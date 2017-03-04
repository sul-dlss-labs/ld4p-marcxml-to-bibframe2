[![Build Status](https://travis-ci.org/sul-dlss/ld4p-marcxml-to-bibframe1
.svg?branch=master)](https://travis-ci.org/sul-dlss/ld4p-marcxml-to-bibframe1
)
[![Coverage Status](https://coveralls.io/repos/github/sul-dlss/ld4p-marcxml-to-bibframe1
/badge.svg?branch=master)](https://coveralls.io/github/sul-dlss/ld4p-marcxml-to-bibframe1?branch=master)
[![Dependency Status](https://gemnasium.com/badges/github.com/sul-dlss/ld4p-marcxml-to-bibframe1.svg)](https://gemnasium.com/github.com/sul-dlss/ld4p-marcxml-to-bibframe1)

# ld4p-marcxml-to-bibframe1
Convert marcxml data into bibframe1 rdfxml, using converter written by Library of Congress, available at https://github.com/lcnetdev/marc2bibframe

## Development

### Dependencies / Prerequisites

- Library of Congress Marc to Bibframe1 converter, installed at loc_marc2bibframe directory:

  `git clone https://github.com/lcnetdev/marc2bibframe.git loc_marc2bibframe`

### Compiling and Executing

### Code Coverage Reports

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
