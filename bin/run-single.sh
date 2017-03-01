#!/bin/bash
DIR=`dirname "$0"`
DIR=`cd "$DIR"; pwd`
. "${DIR}/global_config.sh"

run_benchmark $1
