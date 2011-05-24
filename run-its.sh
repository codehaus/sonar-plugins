#!/bin/bash
mvn clean verify -Pintegration-tests -Djava.io.tmpdir=/tmp -Dsonar.runtimeVersion=2.8-SNAPSHOT