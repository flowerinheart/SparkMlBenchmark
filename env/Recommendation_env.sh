#!/usr/bin/env bash
BENCHMARK_NAME=Recommendation
PACKAGE="skydata.spark.benchmark"
CLASS="${PACKAGE}.${BENCHMARK_NAME}Benchmark"

DIR=`dirname "$0"`
DIR=`cd "$DIR"/..; pwd`
DATA_DIR="${DIR}/data/$BENCHMARK_NAME"
OUTPUT_DIR="${DIR}/result"

#for data generate
NUM_OF_EXAMPLES=200
EPS=6
NUM_OF_PARTITIONS=10
INTERCEPT=0.0
DATA_GEN_ARG="${NUM_OF_EXAMPLES} ${EPS} ${NUM_OF_PARTITIONS} ${INTERCEPT}"



#ALG_ARG=
RANK="10"
N_ITER="10"
LAMBDA="0.01"
ALG_ARG="${RANK} ${N_ITER} ${LAMBDA}"
