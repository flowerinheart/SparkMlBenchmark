#!/usr/bin/env bash
BENCHMARK_NAME="DenseGaussianMixture"
PACKAGE="skydata.spark.benchmark.clustering"

#optional
#DATA_DIR                              e.g "alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARK_NAME"
#CLASS                                 e.g "${PACKAGE}.${BENCHMARK_NAME}Benchmark"
#OUTPUT_DIR                            e.g "${DIR}/result"

#DATA_GEN_ARG=
NUM_OF_POINTS=200
NUM_OF_CLUSTERS=20
DIMENSIONS=20
SCALING=0.6
NUM_OF_PARTITIONS=30
DATA_GEN_ARG="${NUM_OF_POINTS} ${NUM_OF_CLUSTERS} ${DIMENSIONS} ${SCALING} ${NUM_OF_PARTITIONS}"



#ALG_ARG=
CONVERGENCE_TOL="0.1"
MAX_ITERATION="20"
ALG_ARG="${CONVERGENCE_TOL} ${MAX_ITERATION}"
