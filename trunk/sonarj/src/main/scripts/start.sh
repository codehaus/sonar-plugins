sonardir=/opt/sonar-1.11
arch=macosx-universal-32
mvn clean install
rm $sonardir/extensions/plugins/sonar-sonarj-plugin*
cp target/sonar-sonarj-plugin*.jar $sonardir/extensions/plugins
$sonardir/bin/$arch/sonar.sh start

