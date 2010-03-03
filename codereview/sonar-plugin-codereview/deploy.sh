echo "Deploying sonar-plugin-codereview-1.10.1.jar ..."
cp target/sonar-plugin-codereview-1.10.1.jar /home/cmoraes/apps/sonar-1.10.1/extensions/plugins/

echo "Copying server side codes"
cp target/classes/org/sonar/plugins/codereview/codereviewviewer/server/* /home/cmoraes/apps/sonar-1.10.1/war/sonar-web/WEB-INF/classes/org/sonar/plugins/codereview/codereviewviewer/server

echo "Done."
echo "Current Time : `date +\"%H:%M\"`"
ls -l target/sonar-plugin-codereview-1.10.1.jar | grep jar
ls -l /home/cmoraes/apps/sonar-1.10.1/extensions/plugins | grep jar
ls -l /home/cmoraes/apps/sonar-1.10.1/war/sonar-web/WEB-INF/classes/org/sonar/plugins/codereview/codereviewviewer/server

