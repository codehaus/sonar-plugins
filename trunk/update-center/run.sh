#!/bin/sh

mvn -e clean install exec:java wagon:upload
