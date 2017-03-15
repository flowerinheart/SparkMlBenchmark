#!/usr/bin/env bash
BENCHMARK_NAME="BisectingKMeans"
PACKAGE="skydata.spark.benchmark.clustering"

#optional
#DATA_DIR                              e.g "alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARK_NAME"
#CLASS                                 e.g "${PACKAGE}.${BENCHMARK_NAME}Benchmark"
#OUTPUT_DIR                            e.g "${DIR}/result"

#for data generate
NUM_OF_POINTS=40
N_CLUSTERS=8
DIMENSIONS=10
SCALING=0.6
NUM_OF_PARTITIONS=10
DATA_GEN_ARG="${NUM_OF_POINTS} ${N_CLUSTERS} ${DIMENSIONS} ${SCALING} ${NUM_OF_PARTITIONS}"


#ALG_ARG=
MAX_ITERATION="30"
ALG_ARG="${MAX_ITERATION}"
