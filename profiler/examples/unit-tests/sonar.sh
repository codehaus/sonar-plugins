#!/bin/sh

JPROFILER_HOME=~/applications/jprofiler5

mvn clean package
LD_LIBRARY_PATH=$JPROFILER_HOME/bin/linux-x86 mvn -Djprofiler.home=$JPROFILER_HOME sonar:sonar
