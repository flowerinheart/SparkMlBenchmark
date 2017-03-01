#!/bin/bash
CLASS="skydata.spark.benchmark.LogisticRegressionBenchmark"
BENCHMARK_NAME="LogisticRegression"
DIR=`dirname "$0"`
DIR=`cd "$DIR"/..; pwd`
DATA_DIR="${DIR}/data/$BENCHMARK_NAME"
OUTPUT_DIR="${DIR}/result"

#for data generate
NUM_OF_EXAMPLES=200
NUM_OF_FEATURES=20
EPS=0.5
NUM_OF_PARTITIONS=10
probOne=0.2

# for running
MAX_ITERATION=30


OPTION="${DATA_DIR} ${OUTPUT_DIR} ${BENCHMARK_NAME} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${NUM_OF_PARTITIONS} ${probOne}  ${MAX_ITERATION}"


