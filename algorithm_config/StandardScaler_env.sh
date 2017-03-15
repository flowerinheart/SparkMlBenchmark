#!/usr/bin/env bash
BENCHMARK_NAME="StandardScaler"
PACKAGE="skydata.spark.benchmark.others"

#optional
#DATA_DIR                              e.g "alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARK_NAME"
#CLASS                                 e.g "${PACKAGE}.${BENCHMARK_NAME}Benchmark"
#OUTPUT_DIR                            e.g "${DIR}/result"

#DATA_GEN_ARG=
N_POINTS="40"
N_CLUSTERS="5"
DIMENSTION="10"
SCALING="0.6"
NUMPAR="5"
DATA_GEN_ARG="${N_POINTS} ${N_CLUSTERS} ${DIMENSTION} ${SCALING} ${NUMPAR}"

#ALG_ARG=
WITH_MEAN="true"
WITH_STD="true"
ALG_ARG="${WITH_MEAN} ${WITH_STD}"
