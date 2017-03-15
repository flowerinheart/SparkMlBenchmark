#!/usr/bin/env bash
BENCHMARK_NAME="IsotonicRegression"
PACKAGE="skydata.spark.benchmark.supervisal"
#optional
#DATA_DIR                              e.g "alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARK_NAME"
#CLASS                                 e.g "${PACKAGE}.${BENCHMARK_NAME}Benchmark"
#OUTPUT_DIR                            e.g "${DIR}/result"

#for data_gen ,this arguments are for default, if you override dataGenArgNames in scala script, your should modify them
NUM_OF_EXAMPLES=200
NUM_OF_FEATURES=20
EPS=6
NUM_OF_PARTITIONS=10
INTERCEPT=0.0
DATA_GEN_ARG="${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${NUM_OF_PARTITIONS} ${INTERCEPT}"


#for alg_gen ,this arguments are for default, if you override algArgNames in scala script, your should modify them
ISOTONIC="true"
ALG_ARG="${ISOTONIC}"
