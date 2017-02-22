## ml benchmark

### feature
* easy to expand
* automation

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


### how to run
