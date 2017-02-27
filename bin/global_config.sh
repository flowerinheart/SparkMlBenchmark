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
[ -z "$BENCH_CONF" ] && export BENCH_CONF="${this}/../conf/"
[ -f "${BENCH_HOME}/bin/funcs.sh" ] && . "${BENCH_HOME}/bin/funcs.sh"


export BENCH_NUM=${BENCH_HOME}/num;
if [ ! -d ${BENCH_NUM} ]; then
	mkdir -p ${BENCH_NUM};
	mkdir -p ${BENCH_NUM}/old;
fi

# local report
export BENCH_REPORT=${BENCH_NUM}/bench-report.dat



#if [ -z "$MllibJar" ]; then
#	export MllibJar=~/.m2/repository/org/apache/spark/spark-mllib_2.10/${SPARK_VERSION}/spark-mllib_2.10-${SPARK_VERSION}.jar
#fi

HADOOP_CONF_DIR="${HADOOP_CONF_DIR:-$HADOOP_HOME/conf}"
