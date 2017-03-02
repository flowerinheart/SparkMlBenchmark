#!/usr/bin/env bash
BENCHMARK_NAME="NaiveBayes"
PACKAGE="skydata.spark.benchmark"
CLASS="${PACKAGE}.${BENCHMARK_NAME}Benchmark"
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
DATA_GEN_ARG="${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${NUM_OF_PARTITIONS} ${INTERCEPT}"


LAMBDA="1.0"
TYPE="multinomial"
#for algorithm
ALG_ARG="${LAMBDA} ${TYPE}"
