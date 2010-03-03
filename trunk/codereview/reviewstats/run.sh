cd target
cd classes
java -cp .:/mnt/share/sync/MavenRepo/Repo1/commons-io/commons-io/1.4/commons-io-1.4.jar:/mnt/share/sync/MavenRepo/Repo1/com/thoughtworks/xstream/xstream/1.3.1/xstream-1.3.1.jar   com.symcor.sonar.reviewstats.CodeReviewStats ~/code/dev/sonar/test-project-sonar ~/code/dev/sonar/reviewstats/target
