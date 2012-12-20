#!/bin/bash
mvn clean verify -Pintegration-tests -Djava.io.tmpdir=/tmp -Dsonar.runtimeVersion=3.0
