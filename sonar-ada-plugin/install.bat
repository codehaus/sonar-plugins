
set PLUGINS_HOME=d:\java\sonar-2.3\extensions\plugins
set PLUGIN_VERSION=0.1-SNAPSHOT

call mvn package -DskipTests=true
copy target\sonar-ada-plugin-%PLUGIN_VERSION%.jar %PLUGINS_HOME%

pause
