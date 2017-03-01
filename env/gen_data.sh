#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"


echo "========== preparing ${APP} data =========="

CLASS="org.apache.spark.benchmark.linerregression.LinearRegressionDataGen"
OPTION="${INOUT_SCHEME}${INPUT_HDFS} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${INTERCEPTS} ${NUM_OF_PARTITIONS}"

RM ${INPUT_HDFS}

setup
START_TS=`get_start_ts`
START_TIME=`timestamp`
echo_and_run sh -c " ${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT}  $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/LinearRegression_gendata_${START_TS}.dat"
res=$?
END_TIME=`timestamp`
DU ${INPUT_HDFS} SIZE
get_config_fields >> ${BENCH_REPORT}
print_config  ${APP}-gendata ${START_TIME} ${END_TIME} ${SIZE} ${START_TS} ${res}>> ${BENCH_REPORT}
teardown
#if [ -z "${REAL_TIME_PARSE}" ]
#then
#    sh ${DIR}/../common/bin/backup-on-hdfs.sh ${EVENT_LOG_DIR_ON_HDFS} "${EVENT_LOG_BACKUP_DIR_ON_HDFS}/${MODULE_NAME}-genData"
#else
#    sh ${DIR}/../common/bin/parse-and-get-report.sh "${MODULE_NAME}-${BLAS}-${SPARK_EXECUTOR_MEMORY}" ${SPARK_EXECUTOR_MEMORY}
#fi

exit 0
