#!/bin/bash
DIR=`dirname "$0"`
DIR=`cd "$DIR"; pwd`
. "${DIR}/config.sh"

for file in ${DIR}/../env/*; do
  run_benchmark `basename $file`
done
