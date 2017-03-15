#!/usr/bin/env bash
BENCHMARK_NAME="SimpleFPGrowth"
PACKAGE="skydata.spark.benchmark.others"
#optional
#DATA_DIR                              e.g "alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARK_NAME"
#CLASS                                 e.g "${PACKAGE}.${BENCHMARK_NAME}Benchmark"
#OUTPUT_DIR                            e.g "${DIR}/result"

#DATA_GEN_ARG=
MAX_LEN="20"
N_TRANSACTION="100"
DATA_GEN_ARG="${MAX_LEN} ${N_TRANSACTION}"

#ALG_ARG=
MIN_SUP="default"
ALG_ARG="${MIN_SUP}"
