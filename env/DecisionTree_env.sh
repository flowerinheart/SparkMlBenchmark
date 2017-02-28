#!/usr/bin/env bash

CLASS="skydata.spark.benchmark.DecisionTreeBenchmark"
BENCHMARK_NAME="DecisionTree"
DIR=`dirname "$0"`
DIR=`cd "$DIR"/..; pwd`
DATA_DIR="${DIR}/data/$BENCHMARK_NAME"
OUTPUT_DIR="${DIR}/result"


# for gen_data.sh;  200M data size = 1 million points
NUM_OF_EXAMPLES=500
NUM_OF_FEATURES=10
NUM_OF_PARTITIONS=30 
EPS=6

# for model train
#${NUM_OF_CLASS_C} ${impurityC} ${maxDepthC} ${maxBinsC} ${modeC}
NUM_OF_CLASS=10
impurity="gini"
maxDepth=8 
maxBins=50
mode="Classification"



OPTION="${DATA_DIR} ${OUTPUT_DIR} ${BENCHMARK_NAME} ${NUM_OF_EXAMPLES} ${NUM_OF_FEATURES} ${NUM_OF_PARTITIONS} ${EPS} ${mode} ${NUM_OF_CLASS} ${impurity} ${maxDepth} ${maxBins}"

#NUM_OF_CLASS_R=10
#impurityR="variance"
#maxDepthR=8
#maxBinsR=100
#modeR="Regression"
#${NUM_OF_CLASS_R} ${impurityR} ${maxDepthR} ${maxBinsR} ${modeR}


#MAX_ITERATION=30

#SPARK_STORAGE_MEMORYFRACTION=0.79
