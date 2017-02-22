#!/bin/bash
DIR=`dirname "$0"`
DIR=`cd "$DIR"; pwd`
. "${DIR}/config.sh"

function kmeans_env() {
  JAR="${DIR}/../benchmarks/target/spark.benchmarks-${BENCH_VERSION}.jar"

  NUM_RUN=1
  NUM_OF_CLUSTERS=200
  MAX_ITERATION=300

  DATA_FOLDER="${DIR}/../data"
  NUM_OF_POINTS=100000
  DIMENSIONS=2000
  SCALING=0.6
  NUM_OF_PARTITIONS=20
  CLASS="KmeansApp"
  OPTION=" ${NUM_OF_CLUSTERS} ${MAX_ITERATION} ${NUM_RUN} ${DATA_FOLDER} ${NUM_OF_POINTS} ${DIMENSIONS} ${SCALING} ${NUM_OF_PARTITIONS}"
}
function run_benchmark() {
# set algorithm enviroment variable
case $1 in
"Kmeans")
  kmeans_env
  ;;
*)
  echo "Unknown Algorithm"
  ;;
esac
#
#DU ${INPUT_HDFS} SIZE
# prepare spark opt
set_gendata_opt
set_run_opt
set_MKL $1
set_Java_OPT
setup
RM ${OUTPUT_HDFS}
# (Optional procedure): free page cache, dentries and inodes.
# purge_data "${MC_LIST}"
echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --name $CLASS --class $CLASS --master ${SPARK_MASTER} ${YARN_OPT} ${SPARK_OPT} $JAR ${OPTION}
}

run_benchmark "Kmeans"