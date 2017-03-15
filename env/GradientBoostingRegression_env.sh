#!/usr/bin/env bash
BENCHMARK_NAME="GradientBoostingRegression"
PACKAGE="skydata.spark.benchmark.supervisal"

#optional
#DATA_DIR                              e.g "alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARK_NAME"
#CLASS                                 e.g "${PACKAGE}.${BENCHMARK_NAME}Benchmark"
#OUTPUT_DIR                            e.g "${DIR}/result"




NUM_OF_EXAMPLES=200
NUM_OF_FEATURES=20
EPS=6
NUM_OF_PARTITIONS=10
INTERCEPT=0.0
DATA_GEN_ARG="${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${NUM_OF_PARTITIONS} ${INTERCEPT}"


MAX_ITER="20"
MAXDEPTH="10"
MAXBINS="5"
ALG_ARG="${MAXDEPTH} ${MAXBINS} ${MAX_ITER}"
