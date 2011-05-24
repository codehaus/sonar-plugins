
rem run this script from webscanner folder

set SONAR_FLAGS=-Dsonar.host.url=http://nlsivm114.sdmc.ao-srv.com:8080 -Dsonar.jdbc.url=jdbc:mysql://nlsivm114.sdmc.ao-srv.com:3306/sonar?useUnicode=true -Dsonar.jdbc.driver=com.mysql.jdbc.Driver
set DEBUG= 

call mvn sonar:sonar -X -f html-pom.xml %SONAR_FLAGS% %DEBUG% > sonar-html.log
