#!/bin/bash
DIR=`dirname "$0"`
DIR=`cd "$DIR"/../benchmarks; pwd`
cd $DIR
mvn clean package


