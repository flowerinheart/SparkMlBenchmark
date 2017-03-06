## ml benchmark

### feature
* easy to expand
* automation


### Getting Started

1. Environment prepare and build

    Setup JDK, Apache Spark runtime environment properly
    
    Download/checkout Spark-Bench
    
    Run <SPARK_BENCH_HOME>/bin/build.sh to build Spark-Bench


2. Configurations

    Modify <SPARK_BENCH_HOME>/bin/global_env.sh to make sure below variables has been set: 
    
        * SPARK_HOME The Spark installation location
        
        * SPARK_MASTER Spark master  #see [spark doc](https://spark.apache.org/docs/latest/submitting-applications.html)


3. Run benchmark
    
    run single benchmark by
    
       ./bin/run-single.sh env/<ALG_ENV>

    run all benchmarks by
    
       ./bin/run-all.sh

    
    



### how to add new algorithm to this benchmark
1. algorithm script file

    This file should generate data and do a journey of loading, training and predicting, SparkMetrics will help you record  arguments and metrics.
    SparkMetrics with two type argument D and M, which respresent Data Type(eg.RDD[VECTOR]) and Model type(eg.KMeansModel)
    it mainly stand for four method onParseArg, onTrain, onLoad, onTest:
    * onParseArg, this method require a hashtable that record your algorithm arguments. This arguments will be used when output
    * onTrain, this function help to measure train time.It require code block that train your Model which type is M, this method return trained model.
    * onLoad, this function just like onTrain
    * onTest, this function just like onTrain

    Finally, you should put it under benchmarks/src/main/scala


2. envirment file

    This file have to contain three necessary variable:
    * CLASS  which stand for your APP class name(eg. KmeansApp)
    * OPTION which represent your APP arguments(include data generation arguments and algorithm arguments), your scala script should parse arguments according to
    it.
