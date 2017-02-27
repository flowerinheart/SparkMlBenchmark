#!/bin/bash
DIR=`dirname "$0"`
DIR=`cd "$DIR"; pwd`
. "${DIR}/global_config.sh"

for file in ${DIR}/../env/*; do
  run_benchmark $file
done
