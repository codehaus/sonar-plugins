Sonar-Jlint Plugin: Release Notes

Author: Christopher Moraes (xircles user id: cmoraes)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Version 1.0:  Dec 2009

Updated for Sonar 1.10 compatibilty + bug fixes.



Version 1.0:  Jul 2009  (not released)

- Supports maven-jlint-plugin version 1.1.  The jar for this plugin is available from SVN.  The source for the plugin will be uploaded to an appropriate location shortly.  (I will update this readme document with the link.)

- Has been tested on Sonar v 1.9.2


Install Notes:

1. Download and install JLint 3.1 from http://jlint.sourceforge.net.  Ensure that the Jlint executable is present in your path before running the maven-jlint-plugin.

2. Install the maven-jlint-plugin jar into your maven repository.
  <groupId>com.symcor.jlint.plugin</groupId>
  <artifactId>maven-jlint-plugin</artifactId>
  <version>1.1</version>

3. Compile the jlint plugin for Sonar from the downloaded sources with the following command
	mvn clean install

4. Copy the sonar-plugin-jlint-1.9.2.jar file from the target directory to the <SONAR_HOME>/extensions/plugins directory and restart the Sonar server.

5. The Jlint rules can be viewed and configured from the Quality profile.

6. Jlint rules belong to categories and these categories can be enabled or disabled.  A sample Jlint config file ("my-jlint-config.xml") is uploaded to SVN.  This file can be uploaded in the quality profile manager UI in Sonar.

7. Each Jlint category maps to one or many rules.  If you can configuring rules from Quality Profile in Sonar, ensure that all rules from a category are either enabled or disabled.  You can identify the category to which a rule belong to from the name of the rule - each rule starts with the category name.  If you enable/disable a subset of rules in a category, the sonar plugin will fail with an appropriate error message.
