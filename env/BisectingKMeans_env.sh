#!/usr/bin/env bash
BENCHMARK_NAME="BisectingKMeans"
PACKAGE="skydata.spark.benchmark.clustering"
CLASS="${PACKAGE}.${BENCHMARK_NAME}Benchmark"

DIR=`dirname "$0"`
DIR=`cd "$DIR"/..; pwd`
DATA_DIR="alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARvK_NAME"
OUTPUT_DIR="${DIR}/result"

#for data generate


NUM_OF_POINTS=200
N_CLUSTERS=10
DIMENSIONS=10
SCALING=0.6
NUM_OF_PARTITIONS=30
DATA_GEN_ARG="${NUM_OF_POINTS} ${N_CLUSTERS} ${DIMENSIONS} ${SCALING} ${NUM_OF_PARTITIONS}"


#ALG_ARG=
MAX_ITERATION="30"
ALG_ARG="${MAX_ITERATION}"
