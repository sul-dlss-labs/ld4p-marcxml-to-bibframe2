#!/bin/bash

# See the README.md file about how to obtain Oracle credentials and then
# use them to configure the following environment variables on travis.ci at
# https://travis-ci.org/sul-dlss/ld4p-marc21-to-xml/settings
#
# MAVEN_MASTER_password is an arbitrary password
# ORACLE_OJDBC_username belongs to an Oracle user account
# ORACLE_OJDBC_password (unencrypted) belongs to the Oracle user account

mvn --version

# Check for a maven user directory
if [ ! -d $HOME/.m2 ]; then
    mkdir -p $HOME/.m2
fi

export SECURITY_FILE=$HOME/.m2/settings-security.xml
export SETTINGS_FILE=$HOME/.m2/settings.xml

MAVEN_MASTER_encpass=$(mvn --encrypt-master-password $MAVEN_MASTER_password)
cat > $SECURITY_FILE <<SECURITY_XML
<settingsSecurity>
	<master>$MAVEN_MASTER_encpass</master>
</settingsSecurity>
SECURITY_XML

ORACLE_OJDBC_encpass=$(mvn --encrypt-password $ORACLE_OJDBC_password)
cat > $SETTINGS_FILE <<SETTINGS_XML
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
	https://maven.apache.org/xsd/settings-1.0.0.xsd">
	<servers>
	  <server>
	    <id>maven.oracle.com</id>
	    <username>$ORACLE_OJDBC_username</username>
	    <password>$ORACLE_OJDBC_encpass</password>
	    <configuration>
	      <basicAuthScope>
		<host>ANY</host>
		<port>ANY</port>
		<realm>OAM 11g</realm>
	      </basicAuthScope>
	      <httpConfiguration>
		<all>
		  <params>
		    <property>
		      <name>http.protocol.allow-circular-redirects</name>
		      <value>%b,true</value>
		    </property>
		  </params>
		</all>
	      </httpConfiguration>
	    </configuration>
	  </server>
	</servers>
</settings>
SETTINGS_XML
