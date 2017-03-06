#!/usr/bin/env bash
BENCHMARK_NAME="PrefixSpan"
PACKAGE="skydata.spark.benchmark.others"
CLASS="${PACKAGE}.${BENCHMARK_NAME}Benchmark"

DIR=`dirname "$0"`
DIR=`cd "$DIR"/..; pwd`
DATA_DIR="${DIR}/data/$BENCHMARK_NAME"
OUTPUT_DIR="${DIR}/result"

N_POINTS="40"
MAX_ROW="5"
MAX_COL="6"
DATA_GEN_ARG="${N_POINTS} ${MAX_ROW} ${MAX_COL}"


MIN_SUP="default"
MAX_PATTERN_LENGTH="default"
ALG_ARG="${MIN_SUP} ${MAX_PATTERN_LENGTH}"
#ALG_ARG=
