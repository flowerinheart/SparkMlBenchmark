#!/usr/bin/env bash
# for prepare #600M example=40G
CLASS="skydata.spark.benchmark.SVMBenchmark"
BENCHMARK_NAME="SVM"
DIR=`dirname "$0"`
DIR=`cd "$DIR"/..; pwd`
DATA_DIR="${DIR}/data/$BENCHMARK_NAME"
OUTPUT_DIR="${DIR}/result"

#for data generate
NUM_OF_EXAMPLES=200
NUM_OF_FEATURES=20
EPS=6
NUM_OF_PARTITIONS=10
INTERCEPT=0.0

# for running
MAX_ITERATION=30


OPTION="${DATA_DIR} ${OUTPUT_DIR} ${BENCHMARK_NAME} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${NUM_OF_PARTITIONS} ${INTERCEPT} ${MAX_ITERATION}"

