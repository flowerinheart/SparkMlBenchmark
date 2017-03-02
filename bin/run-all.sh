#!/bin/bash
DIR=`dirname "$0"`
DIR=`cd "$DIR"; pwd`
. "${DIR}/global_config.sh"

for file in ${DIR}/../env/*; do
  if [ ! `basename "$file"` == "env.template" ]; then
    run_benchmark $file
  fi
done
