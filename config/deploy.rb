set :application, 'ld4p-marcxml-to-bibframe2'
set :repo_url, 'https://github.com/sul-dlss/ld4p-marcxml-to-bibframe2.git'
set :deploy_host, "sul-ld4p-converter-#{fetch(:stage)}.stanford.edu"
set :user, 'ld4p'

ask :branch, `git rev-parse --abbrev-ref HEAD`.chomp

set :deploy_to, "/opt/app/#{fetch(:user)}/#{fetch(:application)}"

server fetch(:deploy_host), user: fetch(:user), roles: 'app'

# allow ssh to host
Capistrano::OneTimeKey.generate_one_time_key!

# Default value for :format is :airbrussh.
# set :format, :airbrussh

# You can configure the Airbrussh format using :format_options.
# These are the defaults.
# set :format_options, command_output: true, log_file: "log/capistrano.log", color: :auto, truncate: :auto

# Default value for :pty is false
# set :pty, true

# Default value for :linked_files is []
# append :linked_files, "config/database.yml", "config/secrets.yml"

# Default value for linked_dirs is []
append :linked_dirs, 'log', 'loc_marc2bibframe2'

# Default value for default_env is {}
# set :default_env, { path: "/opt/ruby/bin:$PATH" }

# Default value for keep_releases is 5
# set :keep_releases, 5

# update shared_configs before restarting app
#before 'deploy:restart', 'shared_configs:update'

namespace :loc do
  desc 'git clone LOC marc2bibframe2 (if it does not exist already)'
  task :clone_marc2bibframe2 do
    on roles(:app) do
      cmd  = "cd #{shared_path} && "
      cmd += "if [ ! -d loc_marc2bibframe2 ]; then "
      cmd += "  git clone https://github.com/lcnetdev/marc2bibframe2.git loc_marc2bibframe2; "
      cmd += "fi"
      execute cmd
    end
  end

  desc 'git pull master for LOC marc2bibframe2'
  task :update_marc2bibframe2 do
    on roles(:app) do
      execute "cd #{shared_path}/loc_marc2bibframe2 && git pull origin master"
    end
  end
end

before 'loc:update_marc2bibframe2', 'loc:clone_marc2bibframe2'
after 'deploy:finished', 'loc:clone_marc2bibframe2'

namespace :deploy do
  # needs to be in deploy namespace so deploy_host is defined properly (part of current_path)
  desc 'convert test file of one record'
  task :test_one_record do
    on roles(:app) do
      execute "cd #{current_path} && ./bin/marcxml_to_bf2_test.sh ./data/MarcXML/one_record.xml"
    end
  end
end
