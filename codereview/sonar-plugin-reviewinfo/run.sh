cd target/classes

java -cp .:/mnt/share/sync/MavenRepo/Repo1/com/thoughtworks/xstream/xstream/1.3.1/xstream-1.3.1.jar:\
/mnt/share/sync/MavenRepo/Repo1/org/codehaus/sonar/sonar-plugin-api/1.10.1/sonar-plugin-api-1.10.1.jar:\
/mnt/share/sync/MavenRepo/Repo1/commons-lang/commons-lang/2.4/commons-lang-2.4.jar:\
/mnt/share/sync/MavenRepo/Repo1/commons-io/commons-io/1.4/commons-io-1.4.jar:\
/mnt/share/sync/MavenRepo/Repo1/com/symcor/sonar/reviewstats/1.0/reviewstats-1.0.jar  \
     org.sonar.plugins.svninfo.ReviewStatsXMLHandler
#     org.sonar.plugins.svninfo.SvnInfoXMLHandler
