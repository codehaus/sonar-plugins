sonardir=/opt/sonar-1.11
arch=macosx-universal-32
mvn package
cp target/sonar-sonarj-plugin-1.0.jar $sonardir/extensions/plugins
$sonardir/bin/$arch/sonar.sh start

