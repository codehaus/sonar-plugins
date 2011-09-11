
rem run this script from w3c-markup-validation folder

set SONAR_FLAGS=-Dsonar.host.url=http://ec2-50-16-168-138.compute-1.amazonaws.com:9000 -Dsonar.dynamicAnalysis=false -Dsonar.jdbc.url=jdbc:mysql://ec2-50-16-168-138.compute-1.amazonaws.com:3306/sonar?useUnicode=true -Dsonar.jdbc.driverClassName=com.mysql.jdbc.Driver -Dsonar.jdbc.username=sonar -Dsonar.jdbc.password=sonar -Dsonar.w3cmarkup.url=http://ec2-50-16-168-138.compute-1.amazonaws.com/w3c-validator/check
           
set DEBUG=-X 

set mvncommand=mvn sonar:sonar

call %mvncommand% -f tests/html-pom.xml %SONAR_FLAGS% %DEBUG% > sonar-html.log
