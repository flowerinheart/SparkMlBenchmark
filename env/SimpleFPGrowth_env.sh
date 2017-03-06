#!/usr/bin/env bash
BENCHMARK_NAME="SimpleFPGrowth"
PACKAGE="skydata.spark.benchmark.others"
CLASS="${PACKAGE}.${BENCHMARK_NAME}Benchmark"

DIR=`dirname "$0"`
DIR=`cd "$DIR"/..; pwd`
DATA_DIR="${DIR}/data/$BENCHMARK_NAME"
OUTPUT_DIR="${DIR}/result"

#DATA_GEN_ARG=
MAX_LEN="20"
N_TRANSACTION="100"
DATA_GEN_ARG="${MAX_LEN} ${N_TRANSACTION}"

#ALG_ARG=
MIN_SUP="default"
ALG_ARG="${MIN_SUP}"
