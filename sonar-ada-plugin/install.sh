SONAR_HOME=~/sonar/sonar-2.7/

mvn clean install
cp target/*.jar $SONAR_HOME/extensions/plugins

