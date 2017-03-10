#!/bin/bash

this=$( cd -P "$( dirname "${BASH_SOURCE[0]}" )/" && pwd -P )




if [ -f "${this}/global_env.sh" ] ; then
    . ${this}/global_env.sh
else
    echo "global_env.sh is missing"
    exit 1
fi

export BENCH_VERSION="1.0"
[ -z "$BENCH_HOME" ] && export BENCH_HOME="${this}/.."
[ -z "$BENCH_CONF" ] && export BENCH_BIN="${this}"
[ -f "${BENCH_HOME}/bin/funcs.sh" ] && . "${BENCH_HOME}/bin/funcs.sh"


#HADOOP_CONF_DIR="${HADOOP_CONF_DIR:-$HADOOP_HOME/conf}"
