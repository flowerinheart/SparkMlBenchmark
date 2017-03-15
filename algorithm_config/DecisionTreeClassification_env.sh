#!/usr/bin/env bash

BENCHMARK_NAME="DecisionTreeClassification"
PACKAGE="skydata.spark.benchmark.supervisal"
#optional
#DATA_DIR                              e.g "alluxio://10.0.2.21:19998/spark-benchmark/data/$BENCHMARK_NAME"
#CLASS                                 e.g "${PACKAGE}.${BENCHMARK_NAME}Benchmark"
#OUTPUT_DIR                            e.g "${DIR}/result"





# for gen_data.sh;  200M data size = 1 million points
NUM_OF_EXAMPLES=200
NUM_OF_FEATURES=10
EPS=6
NUM_OF_PARTITIONS=10
INTERCEPT=0.0
DATA_GEN_ARG="${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${EPS} ${NUM_OF_PARTITIONS} ${INTERCEPT}"


# for model train
#${NUM_OF_CLASS_C} ${impurityC} ${maxDepthC} ${maxBinsC} ${modeC}
NUM_OF_CLASS=10
impurity="gini"
maxDepth=8 
maxBins=50
ALG_ARG="${NUM_OF_CLASS} ${impurity} ${maxDepth} ${maxBins}"




#NUM_OF_CLASS_R=10
#impurityR="variance"
#maxDepthR=8
#maxBinsR=100
#modeR="Regression"
#${NUM_OF_CLASS_R} ${impurityR} ${maxDepthR} ${maxBinsR} ${modeR}


#MAX_ITERATION=30

#SPARK_STORAGE_MEMORYFRACTION=0.79
