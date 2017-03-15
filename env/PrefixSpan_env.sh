#!/usr/bin/env bash
BENCHMARK_NAME="PrefixSpan"
PACKAGE="skydata.spark.benchmark.others"

#optional
#DATA_DIR                              e.g "alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARK_NAME"
#CLASS                                 e.g "${PACKAGE}.${BENCHMARK_NAME}Benchmark"
#OUTPUT_DIR                            e.g "${DIR}/result"

N_POINTS="40"
MAX_ROW="5"
MAX_COL="6"
DATA_GEN_ARG="${N_POINTS} ${MAX_ROW} ${MAX_COL}"


MIN_SUP="default"
MAX_PATTERN_LENGTH="default"
ALG_ARG="${MIN_SUP} ${MAX_PATTERN_LENGTH}"
#ALG_ARG=
