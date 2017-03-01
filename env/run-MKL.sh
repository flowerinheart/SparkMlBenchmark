#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
DIR=`cd $bin/../; pwd`
. "${DIR}/../bin/config.sh"
. "${DIR}/bin/config.sh"
echo "========== running ${APP} workload =========="
set_MKL $1 $2

# path check

DU ${INPUT_HDFS} SIZE

REPORTDIR="${DIR}/../report/LinearRegression/$IMPL"
if [[ ! -d $REPORTDIR ]]; then
    echo REPORTDIR=$REPORTDIR
    mkdir -p $REPORTDIR
fi
CLASS="org.apache.spark.benchmark.linerregression.LinearRegressionApp"
OPTION=" ${INOUT_SCHEME}${INPUT_HDFS} ${INOUT_SCHEME}${OUTPUT_HDFS} ${MAX_ITERATION} ${STORAGE_LEVEL} 'Spark LinearRegressionApp $BLAS' $REPORTDIR" 


set_Java_OPT
setup
for((i=0;i<${NUM_TRIALS};i++)); do
RM  ${OUTPUT_HDFS}
	purge_data "${MC_LIST}"
	echo_and_run sh -c "${SPARK_HOME}/bin/spark-submit --class $CLASS --master ${APP_MASTER} ${YARN_OPT} ${SPARK_OPT} ${SPARK_RUN_OPT} ${SPARK_MKL_OPT} $JAR ${OPTION} 2>&1|tee ${BENCH_NUM}/LinearRegression_run_MKL_${START_TS}.dat"
    res=$?
done
teardown

# generate report
#if [ -z "${REAL_TIME_PARSE}" ]
#then
#    sh ${DIR}/../common/bin/backup-on-hdfs.sh ${EVENT_LOG_DIR_ON_HDFS} "${EVENT_LOG_BACKUP_DIR_ON_HDFS}/${MODULE_NAME}-${BLAS}-${SPARK_EXECUTOR_MEMORY}"
#else
#    sh ${DIR}/../common/bin/parse-and-get-report.sh "${MODULE_NAME}-${BLAS}-${SPARK_EXECUTOR_MEMORY}" ${SPARK_EXECUTOR_MEMORY}
#fi


exit 0
