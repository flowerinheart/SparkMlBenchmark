#!/usr/bin/env bash


BENCHMARK_NAME="Kmeans"
PACKAGE="skydata.spark.benchmark.clustering"
#optional
#DATA_DIR                              e.g "alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARK_NAME"
#CLASS                                 e.g "${PACKAGE}.${BENCHMARK_NAME}Benchmark"
#OUTPUT_DIR                            e.g "${DIR}/result"

NUM_OF_POINTS=50
NUM_OF_CLUSTERS=5
DIMENSIONS=8
SCALING=0.6
NUM_OF_PARTITIONS=10
DATA_GEN_ARG="${NUM_OF_POINTS} ${DIMENSIONS} ${SCALING} ${NUM_OF_PARTITIONS}"




MAX_ITERATION=20
RUNS=1
ALG_ARG="${NUM_OF_CLUSTERS} ${MAX_ITERATION} ${RUNS}"
