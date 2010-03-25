#!/bin/sh

JPROFILER_HOME=~/applications/jprofiler5

mvn clean package
mvn -Djprofiler.home=$JPROFILER_HOME sonar:sonar
