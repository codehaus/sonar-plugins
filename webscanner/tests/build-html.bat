
rem run this script from webscanner folder

set SONAR_FLAGS=-Dsonar.host.url=http://localhost:9000 -Dsonar.jdbc.url=jdbc:postgresql://localhost/sonar -Dsonar.jdbc.driver=org.postgresql.Driver
set DEBUG=-X 

call C:\bin\sonar-2.3\bin\windows-x86-32\StopNTService.bat
call mvn install  -Dmaven.test.skip
call xcopy /Y sonar-webscanner-plugin\target\*.jar c:\bin\sonar-2.3\extensions\plugins
rem call xcopy /Y toetstool\sonar-toetstool-plugin\target\*.jar c:\bin\sonar-2.3\extensions\plugins
rem call xcopy /Y w3cmarkup\sonar-w3cmarkup-plugin\target\*.jar c:\bin\sonar-2.3\extensions\plugins
call C:\bin\sonar-2.3\bin\windows-x86-32\StartNTService.bat

set mvncommand=mvn sonar:sonar

:mvn
rem 'ping' in order to wait a few seconds
ping 127.0.0.1 -n 10 -w 1000 > nul
rem try mvn sonar
call %mvncommand% -f tests/html-pom.xml %SONAR_FLAGS% %DEBUG% > sonar-html.log
rem check if sonar was available
find "[INFO] Sonar server can not be reached" *.log
rem errorlevel = 0 if the log contained "sonar can not be reached"
IF %ERRORLEVEL% == 0 GOTO mvn

echo Error Level  %ERRORLEVEL%