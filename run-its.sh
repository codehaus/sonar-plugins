#!/bin/sh
mvn clean verify -Prun-its -Djava.io.tmpdir=/tmp -Dsonar.runtimeVersion=2.7