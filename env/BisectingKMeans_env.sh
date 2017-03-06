#!/usr/bin/env bash
BENCHMARK_NAME="BisectingKMeans"
PACKAGE="skydata.spark.benchmark.clustering"
CLASS="${PACKAGE}.${BENCHMARK_NAME}Benchmark"

DIR=`dirname "$0"`
DIR=`cd "$DIR"/..; pwd`
DATA_DIR="${DIR}/data/$BENCHMARK_NAME"
OUTPUT_DIR="${DIR}/result"

#for data generate


NUM_OF_POINTS=200
DIMENSIONS=2000
SCALING=0.6
NUM_OF_PARTITIONS=30
DATA_GEN_ARG="${NUM_OF_POINTS} ${DIMENSIONS} ${SCALING} ${NUM_OF_PARTITIONS}"


#ALG_ARG=
MAX_ITERATION="30"
ALG_ARG="${MAX_ITERATION}"
