Sonar Build Tools
--------------------

To use these build tools, include the following sections in your pom.xml.
Currently it includes definitions for Sonar checkstyle and the Sonar license header.

<project>
  ...
  <properties>
    <sonar-build-tools.version>1.0-SNAPSHOT</sonar-build-tools.version>
  </properties>
  ...
  <build>
    ...
    <pluginManagement>
      <plugins>
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-checkstyle-plugin</artifactId>
          <version>2.5</version>
          <dependencies>
            <dependency>
              <groupId>org.codehaus.sonar-plugins</groupId>
              <artifactId>sonar-build-tools</artifactId>
              <version>${sonar-build-tools.version}</version>
            </dependency>
          </dependencies>
        </plugin>
        <plugin>
          <groupId>com.mycila.maven-license-plugin</groupId>
          <artifactId>maven-license-plugin</artifactId>
          <version>1.5.1</version>
          <dependencies>
            <dependency>
              <groupId>org.codehaus.sonar-plugins</groupId>
              <artifactId>sonar-build-tools</artifactId>
              <version>${sonar-build-tools.version}</version>
            </dependency>
          </dependencies>
          <configuration>
            <header>sonar/license-header.txt</header>
            ...
          </configuration>
        </plugin>
      </plugins>
    </pluginManagement>

    <plugins>
      ...
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-checkstyle-plugin</artifactId>
        <executions>
          <execution>
            <id>enforce-sonar-codestyle</id>
            <phase>process-sources</phase>
            <goals>
              <goal>checkstyle</goal>
            </goals>
            <!-- Configure these details as part of the execution so
                 it doesn't affect checkstyle reporting.  This checkstyle
                 is only meant for validation of a small set of required
                 coding style practices.  -->
            <configuration>
              <configLocation>sonar/checkstyle.xml</configLocation>
              <linkXRef>false</linkXRef>
              <failsOnError>true</failsOnError>
              <consoleOutput>true</consoleOutput>
            </configuration>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>com.mycila.maven-license-plugin</groupId>
        <artifactId>maven-license-plugin</artifactId>
        <executions>
          <execution>
            <id>enforce-sonar-license</id>
            <goals>
              <goal>check</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
    ...
  </build>
  ...
</project>