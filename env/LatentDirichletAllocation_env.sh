#!/usr/bin/env bash
BENCHMARK_NAME="LatentDirichletAllocation"
PACKAGE="skydata.spark.benchmark.clustering"
CLASS="${PACKAGE}.${BENCHMARK_NAME}Benchmark"

DIR=`dirname "$0"`
DIR=`cd "$DIR"/..; pwd`
DATA_DIR="${DIR}/data/$BENCHMARK_NAME"
OUTPUT_DIR="${DIR}/result"

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
