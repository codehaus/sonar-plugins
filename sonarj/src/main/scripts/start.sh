sonardir=/opt/sonar-1.11
arch=macosx-universal-32
mvn package
rm $sonardir/extensions/plugins/sonar-sonarj-plugin*
cp target/sonar-sonarj-plugin-1.0-SNAPSHOT.jar $sonardir/extensions/plugins
$sonardir/bin/$arch/sonar.sh start

