!/usr/bin/env bash
BENCHMARK_NAME="Word2Vec"
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
W_SIZE="default"
V_SIZE="default"
MIN_COUNT="50"
MAX_SEN_LEN="default"
ALG_ARG="${W_SIZE} ${V_SIZE} ${MIN_COUNT} ${MAX_SEN_LEN}"
