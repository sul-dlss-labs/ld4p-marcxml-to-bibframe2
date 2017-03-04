
[![Dependency Status](https://gemnasium.com/badges/github.com/sul-dlss/ld4p-marcxml-to-bibframe2.svg)](https://gemnasium.com/github.com/sul-dlss/ld4p-marcxml-to-bibframe2)

# ld4p-marcxml-to-bibframe2
Convert MARC21 XML data into Bibframe2 RDF, using converter from Library of Congress,
available at https://github.com/lcnetdev/marc2bibframe2


## Development

### Dependencies / Prerequisites

- An XSLT processor, such as `xsltproc` from libxslt
- Library of Congress Marc to bibframe2 converter, installed at loc_marc2bibframe directory:

  `git clone https://github.com/lcnetdev/marc2bibframe2.git  loc_marc2bibframe2`

### Executing

`./bin/marcxml_to_bf2_test.sh {MARC XML FILE}`

e.g.

`./bin/marcxml_to_bf2_test.sh ./data/MarcXML/one_record.xml`


## Deployment

Capistrano is used for deployment.

1. On your laptop, run

    `bundle`

  to install the Ruby capistrano gems and other dependencies for deployment.

2. Deploy code to remote VM:

    `cap dev deploy`

3. Run a test conversion to ensure it works on remote VM:

    `cap dev deploy:test_one_record`
