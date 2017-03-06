#!/usr/bin/env bash
BENCHMARK_NAME="PowerIterationClustering"
PACKAGE="skydata.spark.benchmark.clustering"
CLASS="${PACKAGE}.${BENCHMARK_NAME}Benchmark"

DIR=`dirname "$0"`
DIR=`cd "$DIR"/..; pwd`
DATA_DIR="${DIR}/data/$BENCHMARK_NAME"
OUTPUT_DIR="${DIR}/result"

#DATA_GEN_ARG=
N_CIR="3"
N_POINTS="6"
DATA_GEN_ARG="${N_CIR} ${N_POINTS}"

#ALG_ARG=
N_CLUSTERS="10"
MAX_ITERATIONS="30"
INIT_MODE="degree"
ALG_ARG="${N_CLUSTERS} ${MAX_ITERATIONS} ${INIT_MODE}"
