#!/bin/bash
DIR=`dirname "$0"`
DIR=`cd "$DIR"; pwd`
. "${DIR}/config.sh"


function run_benchmark() {
# set algorithm enviroment variable
. "$DIR/../env/$1"
#DU ${INPUT_HDFS} SIZE
# prepare spark opt
set_gendata_opt
set_run_opt
set_MKL --MKL
set_Java_OPT
setup

# remove data file
#RM ${OUTPUT_HDFS}

echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --name ${CLASS} --class ${CLASS} --master ${SPARK_MASTER} ${YARN_OPT} ${SPARK_OPT} ${JAR} ${OPTION}"
}

for file in ${DIR}/../env/*; do
  run_benchmark `basename $file`
done
