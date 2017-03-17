## Spark ML Benchmark

## Features
* easy to expand
* automation
* easy to configure output format


## Getting Started

1. Environment prepare and build

    Setup JDK, Apache Spark runtime environment properly(If you want to use this benchmark in cluster mode, also set up alluxio and hadoop).

    Download/git clone SparkMlBenchmark

    Run below command to build:

      bin/sparkbm.sh build

2. Configurations

    copy config/global_en.sh.template to config/global.env.sh

    Modify $SPARK_BENCH_HOME/config/global_env.sh to make sure below variables have been set:
    * SPARK_HOME, The Spark installation path.
    * SPARK_MASTER, Spark master, see [spark doc](https://spark.apache.org/docs/latest/submitting-applications.html)


3. Run benchmark in local

    run single benchmark by

        bin/sparkbm.sh run -f algorithm_config/$ALG_ENV

    run all benchmarks by

        bin/sparkbm.sh run

4. Run benchmark in cluster

    configure hadoop(or alluxio) and spark in cluster mode.

    set HADOOP_HOME(or ALLUXIO_HOME) in global_env.sh.

    set SPARK_MASTER to spark master's host address and set DATA_DIR to HDFS path(or ALLUXIO path) like below:

    hdfs:// &lt;namenodehost>/&lt;path>, e.g: "hdfs://sd002021.skydata.com:9000/spark-benchmark/data".

    alluxio:// &lt;master node address>:&lt;master node port>/&lt;path>, e.g: "alluxio://localhost:19998/LICENSE". Here is [alluxio ref](http://www.alluxio.org/docs/1.4/en/Command-Line-Interface.html).


    Then use `bin/sparkbm.sh run` to run benchmarks.

5. See results

    Run report will be generated in &lt;BENCH_HOME>/result, a csv file &lt;BENCHMARK_NAME>.csv and a row in stat.csv file.

## Command line
bin/sparkbm.sh
Usage:

    bin/sparkbm.sh run|build [-f $file -s]

Option -s mean skip data generation process
Run without options mean run all benchmark in algorithm_config directory.

## Basic Configuration
All setting can be done by modifying algorithm's env file.
### 1. common arguments

* PACKAGE is your scala class's package name. You must set it.

* DATA_DIR will save your data which generate by spark, default is "SparkMlBenchMark/data/$BENCHMARK_NAME".

* OUTPUT_DIR is directory path, which save your running results, default is "SparkMlBenchMark/result/$BENCHMARK_NAME".

* TIME_FORMAT is for set time measure(e.g.: ms/s/min), default is "ms".

* LOAD_PATTERN decide how to preheat rdd after loading, default is "count".

* BLAS decide use which blas implement (e.g.:mkl, openblas, f2j), default is "mkl".




### 2. data gen arguments
Your can change your data arguments in algorithm's env file by modify DATA_GEN_ARG,
actually I already split it to more small pieces for you, you only need to care a litter bit variances.

For supervisal algorithm, if you inherit MllibSupervisalBenchmark in your scala class, the data format will be
RDD[LabeledPoint], I use mllib.util.LinearDataGenerator to generate data, which use a probabilitical model.You just nned to modify three variances:
* NUM_OF_EXAMPLES    ( number of samples, e.g [[1, 2, 3], [4, 5, 6]] will be 2 )
* NUM_OF_FEATURES    ( number of feature, e.g [[1, 2, 3], [4, 5, 6]] wiil be 3 )
* NUM_OF_PARTITIONS  ( partitions of RDD )

For clustering algorithm, if you inherit MllibUnsupervisalBenchmark
in your scala calss, the data format will be RDD[Vector], I use
mllib.util.KMeansDataGenerator to generate data.You should care:
* NUM_OF_POINTS         ( like NUM_OF_EXAMPLES )
* NUM_OF_CLUSTERS       ( number of clusters, eg If you want split data to two category, it will be 2. )
* DIMENSIONS            ( just like NUM_OF_FEATURES. )
* NUM_OF_PARTITIONS     ( like above )

#### Carefully!!!!
One feature of my benchmark is that spark will re-generate data and delete this algorithm's result file(not stat file) if your change DATA_GEN_ARG,
so if you add extra metrics, you should save result file firstly.
### 3. algorithm configure
You should read spark's mllib's document in [here](https://spark.apache.org/docs/latest/api/scala/index.html#org.apache.spark.mllib.package).
And then you can modify algorithm arguments in algorithm's env file according doc.



## Advanced Configuration


### BLAS
If you want to set blas to MKL, first you should install mkl in your machine, maybe you have to see [this](https://brucebcampbell.wordpress.com/2014/12/04/setting-up-native-atlas-with-netlib-java/) to achieve that.
Next you don't need to do anything because MKL is default runtime blas in this benchmark.

For F2j blas in netlib-java, set BLAS environment variance to "F2j" in your algorithm's configuration file.

For other's native blas, you should install it like mkl ( you should choose only one ) and do nothing else.


### Spark runtime configure
Firstly, you should read [spark configuration document](http://spark.apache.org/docs/latest/configuration.html) and [spark submmit doc](http://spark.apache.org/docs/latest/submitting-applications.html)

This benchmark current only support these spark's arguments:

* SPARK_EXECUTOR_MEMORY
* SPARK_SERIALIZER
* SPARK_RDD_COMPRESS
* SPARK_IO_COMPRESSION_CODEC
* SPARK_DEFAULT_PARALLELISM


You can set them in your algorithm's env file.

Actually you also can set other arguments in algorithm's env file but a litter troublesome, eg:SPARK_OPT="${SPARK_OPT} --conf spark.exeutor.memory=4g"




### Change your result file's format
Time format can be change by set in your algorithm's env file, eg:
TIME_FORMAT="ms"   ( only support ms,s,min )

Actually you can add extra metrics in your algorithm result file.For example, I want to see different
performance between mkl and f2j blas, just try these steps:

#### 1.Add a new metrics "blas" in your algorithm's scala source code like:


```
object DenseGaussianMixtureBenchmark extends MllibUnsupervisalBenchmark[GaussianMixtureModel]{
  val BLAS = "blas"
  addMetrics(("blas", "BLAS"))
  override lazy val algArgNames = Array(BLAS, ......)
  override def train(trainData: RDD[Vector]): GaussianMixtureModel =
  ......
  ......
}
```


#### 2. run algorithm under different blas
Append "BLAS=F2j" to algorithm's env file and run this benchmark, and you will see that result file has another
col calls "blas" and a new record.

Remove "BLAS=F2j" in algorithm's env file and run this benchmark, and you will see that two result records
with different blas metrics in result file.





## How to add new algorithm to this benchmark
### Algorithm file
Mainly four steps:

1. Create algorithm scala class in benchmark/src/main/scala/skydata.spark.benchmark and name it XXXBenchmark, e.g.:KmeansBenchmark, and set BENCHMARK_NAME=Kmeans
in env file.
2. Inherit one abstract class

   Actually, this benchmark framework provides three abstract class to help you build benchmark.
   * SparkMlBenchmark
   * MllibSupervisalBenchmark(input data format is RDD[LabeledPoint])
   * MllibUnsupervisalBenchmark(input data format is RDD[Vector])

   core proccess are four phases:
   * genData(dataPath)    #generate rdd data and save it to dataPath
   * load(dataPath)       #load  data from dataPath which is generated from geneData method, and split it to trainData and testData
   * train(trainData)     #use trainData and arguments to build model, your can get algorithm's arguments from variance algArgTable
   * test(model, testData)       #use model to predict testData

   SparkMLBenchmark don't provide default implementation of them, other two provide default implementations for genData, load, test.

   Next you can choose one to inherit or read their doc and api to help your decision.

3. override argument name's list like below:

       val INIT_MODE = Key("initializationMode")
       override lazy val dataGenArgNames = Array(N_CIR, N_POINTS)
       override lazy val algArgNames = Array(N_CLUSTERS, MAX_ITER, INIT_MODE)

   Note that lazy is necessary .


4. implement abstract methods:




### Environment file
SparkMlBenchMark/algorithm_config/template has provided two template files for clustering and
supervisal algorithms, just set all necessary arguments.
