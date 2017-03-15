#!/usr/bin/env bash
BENCHMARK_NAME=Recommendation
PACKAGE="skydata.spark.benchmark.supervisal"
#optional
#DATA_DIR                              e.g "alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARK_NAME"
#CLASS                                 e.g "${PACKAGE}.${BENCHMARK_NAME}Benchmark"
#OUTPUT_DIR                            e.g "${DIR}/result"

#for data generate
NUM_OF_EXAMPLES="200"
EPS="0.6"
NUM_OF_PARTITIONS="10"
INTERCEPT="0.0"
DATA_GEN_ARG="${NUM_OF_EXAMPLES} ${EPS} ${NUM_OF_PARTITIONS} ${INTERCEPT}"



#ALG_ARG=
RANK="10"
N_ITER="10"
LAMBDA="0.01"
ALG_ARG="${RANK} ${N_ITER} ${LAMBDA}"
