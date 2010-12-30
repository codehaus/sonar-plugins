call mvn verify -f %1.pom.xml
call mvn sonar:sonar -Pxml,postgresql -f %1.pom.xml
call mvn sonar:sonar -Phtml,postgresql -f %1.pom.xml