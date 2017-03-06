#!/usr/bin/env bash
BENCHMARK_NAME="GradientBoostingClassification"
PACKAGE="skydata.spark.benchmark.supervisal"
CLASS="${PACKAGE}.${BENCHMARK_NAME}Benchmark"

DIR=`dirname "$0"`
DIR=`cd "$DIR"/..; pwd`
DATA_DIR="${DIR}/data/$BENCHMARK_NAME"
OUTPUT_DIR="${DIR}/result"




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