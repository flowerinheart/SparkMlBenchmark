#!/bin/bash

this=$( cd -P "$( dirname "${BASH_SOURCE[0]}" )/" && pwd -P )
export BENCH_VERSION="1.0"
[ -z "$BENCH_HOME" ] && export BENCH_HOME=`cd ${this}/..; pwd`
[ -f "${BENCH_HOME}/bin/funcs.sh" ] && . "${BENCH_HOME}/bin/funcs.sh"




## set env var
if [ -f "${BENCH_HOME}/config/global_env.sh" ] ; then
    . ${BENCH_HOME}/config/global_env.sh
else
    echo "global_env.sh is missing"
    exit 1
fi
[ -z "${GEN_DATA}" ] && export GEN_DATA="yes"
[ -z "${DATA_DIR}" ] && export DATA_DIR="${BENCH_HOME}/data"
[ -z "${OUTPUT_DIR}" ] && export OUTPUT_DIR="${BENCH_HOME}/result"


[ -z "$BENCH_MARK" ] && export BENCHMARK_NAME="temp"
[ -z "$TIME_FORMAT" ] && export TIME_FORMAT="s"
[ -z "$LOAD_PATTERN" ] && export LOAD_PATTERN="count"
[ -z "$BLAS" ] && export BLAS="NATIVE"
[ -z "$LOG_DIR" ] && export LOG_DIR="${BENCH_HOME}/logs"
if [ ! -f $LOG_DIR ];then
	mkdir -p $LOG_DIR
fi

[ -z "$SPARK_SERIALIZER" ] && export SPARK_SERIALIZER=org.apache.spark.serializer.KryoSerializer
[ -z "$SPARK_RDD_COMPRESS" ] && export SPARK_RDD_COMPRESS=false
[ -z "$SPARK_IO_COMPRESSION_CODEC" ] && export SPARK_IO_COMPRESSION_CODEC=lzf

[ -z "$SPARK_STORAGE_MEMORYFRACTION" ] && export SPARK_STORAGE_MEMORYFRACTION=0.5
[ -z "$SPARK_EXECUTOR_MEMORY" ] && export SPARK_EXECUTOR_MEMORY=5g
[ -z "$SPARK_DRIVER_MEMORY" ] && export SPARK_DRIVER_MEMORY=5g
[ -z "$SPARK_EXECUTOR_INSTANCES" ] && export SPARK_EXECUTOR_INSTANCES=5g
[ -z "$SPARK_EXECUTOR_CORES" ] && export SPARK_EXECUTOR_CORES=1

[ -z "$STORAGE_LEVEL" ] && export STORAGE_LEVEL=MEMORY_AND_DISK

[ -z "$NUM_OF_PARTITIONS" ] && export NUM_OF_PARTITIONS=10


[ -z "$EVENT_LOG_DIR_ON_HDFS" ] && export EVENT_LOG_DIR_ON_HDFS="/spark/event"
[ -z "$EVENT_LOG_BACKUP_DIR_ON_HDFS" ] && export EVENT_LOG_BACKUP_DIR_ON_HDFS="/spark/backup/event"
[ -z "$REPORT_DIR_ON_HDFS" ] && export REPORT_DIR_ON_HDFS="/SparkBenchMark/report"
[ -z "$EVENT_LOG_LOCAL_DIR_TMP" ] && export EVENT_LOG_LOCAL_DIR_TMP="$LOG_DIR/spark_log"
if [ ! -f $EVENT_LOG_LOCAL_DIR_TMP ];then
	mkdir -p $EVENT_LOG_LOCAL_DIR_TMP
fi






JAR_NAME="spark.benchmarks-${BENCH_VERSION}.jar"
JAR_PATH="$BENCH_HOME/benchmarks/target/$JAR_NAME"
#NativeBLASOPT="-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.NativeSystemBLASWrapper -Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.NativeSystemLAPACKWrapper -Dcom.github.fommil.netlib.ARPACK=com.github.fommil.netlib.NativeSystemARPACKWrapper"
NativeBLASOPT="-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.NativeSystemBLAS -Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.NativeSystemLAPACK -Dcom.github.fommil.netlib.ARPACK=com.github.fomil.netlib.NativeSystemARPACK"
F2jBLASOPT="-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.F2jSystemBLAS -Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.F2jSystemLAPACK -Dcom.github.fommil.netlib.ARPACK=com.github.fommil.netlib.F2jSystemARPACK"
