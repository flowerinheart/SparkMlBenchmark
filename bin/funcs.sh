#!/usr/bin/env bash
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

############### common functions ################
function check_dir() {
  case $1 in
    alluxio:*)
      $ALLUXIO_HOME/bin/alluxio fs mkdir $1
      ;;
    hdfs:*)
      $HADOOP_HOME/bin/hadoop mkdir -p $1
      ;;
    *)
      if [ ! -d $1  ]; then
        echo "dir = $1"
        mkdir -p $1
      fi
      ;;
  esac
}

function delete_dir() {
  case $1 in
    alluxio:*)
      $ALLUXIO_HOME/bin/alluxio fs rm -R $1
      ;;
    hdfs:*)
		echo "delete hdfs"
      $HADOOP_HOME/bin/hadoop fs -rm -r $1
      ;;
    *)
      rm -r $1
      ;;
  esac
}
 
function upload_jar() {
  case $2 in
    hdfs*)
      dir=`dirname $2`
      ${HADOOP_HOME}/bin/hadoop fs -ls hdfs://
      ${HADOOP_HOME}/bin/hadoop fs -mkdir -p $dir
      ${HADOOP_HOME}/bin/hadoop fs -rm  "$dir/*"
      ${HADOOP_HOME}/bin/hadoop fs -copyFromLocal $1 $2
      echo "${HADOOP_HOME} $dir  $2" && exit 0
      ;;
    alluxio:*)
      $ALLUXIO_HOME/bin/alluxio fs copyFromLocal $1 $2
      ;;
    *)
     IPS=`cat $SPARK_HOME/conf/slaves`
     for ip in $IPS; do
        echo $ip
        scp "${1}" "${ip}:${2}"
     done
     exit 0
      ;;
  esac
}

function setup() {
  if [ "${MASTER}" = "spark" ] && [ "${RESTART}" = "TRUE" ] ; then
    "${SPARK_HOME}/sbin/stop-all.sh"
    "${SPARK_HOME}/sbin/start-all.sh"
  fi
}

function teardown() {
  if [ "${MASTER}" = "spark" ] && [ "${RESTART}" = "TRUE" ] ; then
    "${SPARK_HOME}/sbin/stop-all.sh"
  fi
}


function set_Java_OPT(){
    SPARK_OPT="${SPARK_OPT} --conf "'"'"spark.executor.extraJavaOptions=${SPARK_EXECUTOR_JVM_OPT}"'"'
    SPARK_OPT="${SPARK_OPT} --conf "'"'"spark.driver.extraJavaOptions=${SPARK_DRIVER_JVM_OPT}"'"'
}


function set_gendata_opt() {
  if [ $BLAS == "native" ];then
    if [ $WRAPPER == "true" ];then
      SPARK_EXECUTOR_JVM_OPT="$WrapperOPT $WrapperNativeOPT $SPARK_EXECUTOR_JVM_OPT"
      SPARK_DRIVER_JVM_OPT="$WrapperOPT $WrapperNativeOPT $SPARK_EXECUTOR_JVM_OPT"
    else
      SPARK_EXECUTOR_JVM_OPT="$NativeOPT $SPARK_EXECUTOR_JVM_OPT"
      SPARK_DRIVER_JVM_OPT="$NativeOPT $SPARK_EXECUTOR_JVM_OPT"
    fi
  else
    if [ $WRAPPER == "true" ];then
      SPARK_EXECUTOR_JVM_OPT="$WrapperOPT $WrapperF2jOPT $SPARK_EXECUTOR_JVM_OPT"
      SPARK_DRIVER_JVM_OPT="$WrapperOPT $WrapperF2jOPT $SPARK_EXECUTOR_JVM_OPT"
    else
      SPARK_EXECUTOR_JVM_OPT="$F2jOPT $SPARK_EXECUTOR_JVM_OPT"
      SPARK_DRIVER_JVM_OPT="$F2jOPT $SPARK_EXECUTOR_JVM_OPT"
    fi
  fi


  if [ ! -z "$SPARK_EVENTLOG_DIR" ]; then
    SPARK_OPT="${SPARK_OPT} --conf spark.eventLog.dir=${SPARK_EVENTLOG_DIR} --conf spark.eventLog.enabled=true"
  fi


  if [ ! -z "$SPARK_EXECUTOR_MEMORY" ]; then
    SPARK_OPT="${SPARK_OPT} --conf spark.executor.memory=${SPARK_EXECUTOR_MEMORY}"
    echo "memory=$SPARK_EXECUTOR_MEMORY"
  fi
  if [ ! -z "$SPARK_SERIALIZER" ]; then
    SPARK_OPT="${SPARK_OPT} --conf spark.serializer=${SPARK_SERIALIZER}"
  fi
  if [ ! -z "$SPARK_RDD_COMPRESS" ]; then
    SPARK_OPT="${SPARK_OPT} --conf spark.rdd.compress=${SPARK_RDD_COMPRESS}"
  fi
  if [ ! -z "$SPARK_IO_COMPRESSION_CODEC" ]; then
    SPARK_OPT="${SPARK_OPT} --conf spark.io.compression.codec=${SPARK_IO_COMPRESSION_CODEC}"
  fi
  if [ ! -z "$SPARK_DEFAULT_PARALLELISM" ]; then
    SPARK_OPT="${SPARK_OPT} --conf spark.default.parallelism=${SPARK_DEFAULT_PARALLELISM}"
  fi
  if [ ! -z "$SPARK_DEFAULT_PARALLELISM" ]; then
    SPARK_OPT="${SPARK_OPT} --conf spark.default.parallelism=${SPARK_DEFAULT_PARALLELISM}"
  fi
  SPARK_OPT="${SPARK_OPT} --conf spark.hadoop.dfs.blocksize=536870912 --conf spark.hadoop.fs.local.block.size=512"

  YARN_OPT=
  if [ "$MASTER" = "yarn" ]; then
    if [ ! -z "$SPARK_EXECUTOR_INSTANCES" ]; then
      YARN_OPT="${YARN_OPT} --num-executors ${SPARK_EXECUTOR_INSTANCES}"
    fi
    if [ ! -z "$SPARK_EXECUTOR_CORES" ]; then
      YARN_OPT="${YARN_OPT} --executor-cores ${SPARK_EXECUTOR_CORES}"
    fi
    if [ ! -z "$SPARK_DRIVER_MEMORY" ]; then
      YARN_OPT="${YARN_OPT} --driver-memory ${SPARK_DRIVER_MEMORY}"
    fi
  fi
}

function set_run_opt() {
  if [ ! -z "$SPARK_HADOOP_FS_LOCAL_BLOCK_SIZE" ] && [ "$FILESYSTEM" != "hdfs" ]; then
    export SPARK_SUBMIT_OPTS="${SPARK_SUBMIT_OPTS} -Dspark.hadoop.fs.local.block.size=${SPARK_HADOOP_FS_LOCAL_BLOCK_SIZE}"
  fi
}

function echo_and_run() { 
  echo "$@"
  "$@" 
}



function RM() {
    tmpdir=$1;
    if [ $# -lt 1 ] || [ -z "$tmpdir" ]; then
        return 1;
    fi
    if [ ! -z `echo $DATA_HDFS | grep "^file://"` ]; then
       if [ ! -d "${tmpdir:7}" ]; then return 1;    fi
       /bin/rm -r ${tmpdir:7};
    else
       ${HADOOP_HOME}/bin/hdfs dfs -test -d $tmpdir;
       if [ $? == 1 ]; then  return 1; fi
      ${HADOOP_HOME}/bin/hdfs dfs -rm -R $tmpdir
    fi
}

function MKDIR() {
    tmpdir=$1;
    if [ $# -lt 1 ] || [ -z "$tmpdir" ]; then
        return 1;
    fi
    if [ ! -z `echo $DATA_HDFS | grep "^file://"` ]; then
       /bin/mkdir -p ${tmpdir:7};
    else
      ${HADOOP_HOME}/bin/hdfs dfs -mkdir -p $tmpdir
    fi
}

function DU() {
    local tmpdir=$1;
    if [ -z "$tmpdir" ] || [ $# -lt 2 ]; then
        return 1;
    fi
    local  __resultvar=$2
    if [ ! -z `echo $DATA_HDFS | grep "^file://"` ]; then
       if [ ! -d "${tmpdir:7}" ]; then return 1;    fi
       local myresult=`/usr/bin/du -b "${tmpdir:7}"|awk '{print $1}'`;
    else
       ${HADOOP_HOME}/bin/hdfs dfs -test -d $tmpdir;
       if [ $? == 1 ]; then  return 1; fi
       local myresult=`${HADOOP_HOME}/bin/hdfs dfs -du -s $tmpdir|awk '{print $1}'`;
    fi
    eval $__resultvar="'$myresult'"
}

function CPFROM() {
    if [ $# -lt 2 ]; then return 1; fi
    src=$1; dst=$2;
    if [ -z "$src" ]; then
        return 1;
    fi
    if [ ! -z `echo $DATA_HDFS | grep "^file://"` ]; then
        if [ ! -d "${src:7}" ]; then echo "src dir should start with file:///";return 1;    fi
        /bin/cp  ${src:7}/* ${dst:7}
    else
       if [ ! -d "${src:8}" ]; then return 1;    fi
      ${HADOOP_HOME}/bin/hdfs dfs -copyFromLocal  ${src:8}/* $dst
    fi
}

function  CPTO() {
    if [ $# -lt 2 ]; then return 1; fi
    src=$1; dst=$2;
    if [ -z "$src" ]; then
        return 1;
    fi
    if [ ! -z `echo $DATA_HDFS | grep "^file://"` ]; then
        /bin/cp -r $src $dst
    else
       ${HADOOP_HOME}/bin/hdfs dfs -test -d $src;
       if [ $? == 1 ]; then  return 1; fi
      ${HADOOP_HOME}/bin/hdfs dfs -copyToLocal  $src $dst
    fi
}


function init(){
    # set algorithm enviroment variable
    SPARK_EXECUTOR_JVM_OPT=""
    SPARK_DRIVER_JVM_OPT=""
    SPARK_OPT=""
    . "$1"

    CLASS="${PACKAGE}.${BENCHMARK_NAME}Benchmark"
    if [ ${GEN_DATA} == "yes" ];then
      delete_dir $DATA_DIR
    fi
    COMMON_ARG="${DATA_DIR}/${BENCHMARK_NAME} ${OUTPUT_DIR} ${BENCHMARK_NAME} ${TIME_FORMAT} ${LOAD_PATTERN} ${GEN_DATA} ${WRAPPER} ${LOG_DIR}"
    OPTION="${COMMON_ARG} ${DATA_GEN_ARG} ${ALG_ARG}"
}


function run_benchmark() {
    init $1
    check_dir $OUTPUT_DIR
    set_gendata_opt
    set_run_opt
    set_Java_OPT
    setup

    # remove data file
    #RM ${OUTPUT_HDFS}

    #echo $OUTPUT_DIR && exit 0
    echo_and_run sh -c "${SPARK_HOME}/bin/spark-submit --name ${BENCHMARK_NAME}   --class ${CLASS} --master ${SPARK_MASTER} ${YARN_OPT} ${SPARK_OPT} ${JAR_PATH} ${OPTION}"
}



