
rem run this script from webscanner folder

set SONAR_FLAGS=
set DEBUG= 

call %mvncommand% -f tests/html-pom.xml %SONAR_FLAGS% %DEBUG% > sonar-html.log
call %mvncommand% -f tests/xhtml-pom.xml %SONAR_FLAGS% %DEBUG% > sonar-xml.log
