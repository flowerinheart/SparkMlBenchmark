#!/usr/bin/env bash
BENCHMARK_NAME="LatentDirichletAllocation"
PACKAGE="skydata.spark.benchmark.clustering"

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
DOCCon="3.5"
TOPCON="1.3"
MAX_ITER="30"
ALG_ARG="${DOCCon} ${TOPCON} ${MAX_ITER}"
