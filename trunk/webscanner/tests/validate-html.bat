
rem run this script from webscanner folder

set SONAR_FLAGS=-Dsonar.host.url=http://localhost:9000 -Dsonar.jdbc.url=jdbc:postgresql://localhost/sonar2.5 -Dsonar.jdbc.driver=org.postgresql.Driver
set DEBUG= 

call %mvncommand% -f tests/html-pom.xml %SONAR_FLAGS% %DEBUG% > sonar-html.log
call %mvncommand% -f tests/xhtml-pom.xml %SONAR_FLAGS% %DEBUG% > sonar-xml.log
