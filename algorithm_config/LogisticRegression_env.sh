#!/bin/bash
BENCHMARK_NAME="LogisticRegression"
PACKAGE="skydata.spark.benchmark.supervisal"

#optional
#DATA_DIR                              e.g "alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARK_NAME"
#CLASS                                 e.g "${PACKAGE}.${BENCHMARK_NAME}Benchmark"
#OUTPUT_DIR                            e.g "${DIR}/result"

#for data generate
NUM_OF_EXAMPLES=200
NUM_OF_FEATURES=20
EPS=0.5
NUM_OF_PARTITIONS=10
probOne=0.2
DATA_GEN_ARG="${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${NUM_OF_PARTITIONS} ${probOne}"

# for running
MAX_ITERATION=30
ALG_ARG="${MAX_ITERATION}"




