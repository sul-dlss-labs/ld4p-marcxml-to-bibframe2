server 'sul-ld4p-converter-dev.stanford.edu', user: 'ld4p', roles: 'app'

# allow ssh to host
Capistrano::OneTimeKey.generate_one_time_key!
