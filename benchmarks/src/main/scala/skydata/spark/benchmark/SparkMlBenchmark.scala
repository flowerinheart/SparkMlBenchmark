
package skydata.spark.benchmark

import java.io.{File, FileWriter, PrintWriter}
import java.util.Scanner

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Darnell on 2017/2/21.
  */




abstract class SparkMlBenchmark[T, M]() {
  def main(args : Array[String]): Unit ={
    run(args)
  }



  private val timeHead = Array("loadTime", "trainTime", "testTime")
  private def makePath(nameList : Array[String]) = nameList mkString File.separatorChar.toString

  val conf = new SparkConf
  val sc = new SparkContext(conf)

  type Key = String
  private[benchmark] def Key(name : String) = name
  val DATA_DIR_KEY = "data_dir"
  val OUTPUT_DIR_KEY = "output_dir"
  val BENCHMARK_NAME = "benchmark"
  val TIME_FORMAT = "time_format"
  val LOAD_PATTERN = "load_pattern"

  type ArgTable = collection.mutable.HashMap[Key, String]
  protected val commonArgTable: ArgTable = new ArgTable()
  protected val dataGenArgTable: ArgTable = new ArgTable()
  protected val algArgTable: ArgTable = new ArgTable()

  private[benchmark] lazy val commonArgNames : Array[Key] = Array(DATA_DIR_KEY, OUTPUT_DIR_KEY, BENCHMARK_NAME, TIME_FORMAT, LOAD_PATTERN)
  private[benchmark] lazy val algArgNames : Array[Key] = Array()
  private[benchmark] lazy val dataGenArgNames : Array[Key] = Array()

  import util.Random.nextString
  commonArgTable.put(BENCHMARK_NAME, nextString(20))
  commonArgTable.put(DATA_DIR_KEY, makePath(Array("./data", commonArgTable(BENCHMARK_NAME))))
  commonArgTable.put(OUTPUT_DIR_KEY, makePath(Array("./result", commonArgTable(BENCHMARK_NAME))))

  /** for configuring report */
  private var extraAlgHead = Array[String]()
  private var extraAlgResult = Array[String]()



  /**
    * add extra metrics to singleHead and singleResult
    * @param tuple2 list compose of (argName, argStr)
    */
  private [benchmark] def addMetrics(tuple2 : (String, String)*) = tuple2.foreach({t2 =>
    extraAlgHead = extraAlgHead :+ t2._1
    extraAlgResult = extraAlgResult :+ t2._2
  })

  private[benchmark] def setAlgArg = (fun : (String) => AnyRef, key : Key) => {
    val str = algArgTable(key)
    if(str != "default")
      fun(str)
  }



  /** retrun arg from ArgTable acording to key*/
  private[benchmark] def extract(key : Key, table : ArgTable) = {
    table(key) match {
      case str : String => str
      case _ => throw new RuntimeException("No " + key.toString + " argument")
    }
  }
  def extractCommon(key : Key) = extract(key, commonArgTable)
  def extractAlg(key : Key) = extract(key, algArgTable)
  def extractData(key : Key) = extract(key,dataGenArgTable)





  /** prepare file environment for running benchmark */
  private def prepareEnvironment(dataDir : String, outputDir : String, benchmarkName : String) = {
    val checkAndCreateDir = (path : String) => {
      val file = new File(path)
      if(!file.exists())
        file.mkdirs() match {
          case true => true
          case false => throw new RuntimeException("could not create dir: " + path)
        }
    }
    checkAndCreateDir(dataDir)
    checkAndCreateDir(outputDir)
    /** check data dir*/
    val manifestFile = new File(makePath(Array(dataDir, "manifest.csv")))
    val checkManifest = () => {/** return is data arg change compare to manifest file in data_dir*/
      if(!manifestFile.exists())
        false
      else {
        val scanner = new Scanner(manifestFile)
        val keys = scanner.nextLine().split(",")
        val values = scanner.nextLine().split(",")
        keys zip values forall ((pair) => dataGenArgTable(// check is data gen arguments same as before
          dataGenArgNames.find(_.toString == pair._1).get) == pair._2)
      }
    }
    if(! checkManifest()){
      // delete rdd data in $DATA_DIR/data and generate new data
      val deleteDir = (file : File) => {
        file.listFiles.foreach({subFile =>
          if(subFile.isDirectory)
            deleteDir(subFile)
          subFile.delete()
        })
      }
      deleteDir(new File(dataDir))
      val pt = new PrintWriter(manifestFile)
      val dataArgKey = dataGenArgTable.keySet.toArray.sorted
      pt.println(dataArgKey.map(_.toString).mkString(","))
      pt.println(dataArgKey.map(dataGenArgTable(_)).mkString(","))
      pt.close()
      genData(makePath(Array(dataDir, "data")))
    }
    //  check result files
    val checkAndSetHead = (f : File, head : Array[String]) => {
      val flag = if (!f.exists()) false
      else if(f.getName == "stat.csv") true
      else {
        val scanner = new Scanner(f)
        val preHead = scanner.nextLine().split(",")
        preHead.length == head.length &&
          preHead.zip(head).forall((t) => t._1 == t._2)
      }
      if(!flag) {
        val pt = new PrintWriter(new FileWriter(f))
        pt.println(head mkString ",")
        pt.close()
      }
    }
    checkAndSetHead(new File(makePath(Array(outputDir, benchmarkName + ".csv"))), "modelName" +: timeHead)
    checkAndSetHead(new File(makePath(Array(outputDir, "stat.csv"))), algArgNames ++ timeHead ++ extraAlgHead)
  }

  /** Set up environment and do four main process: genData, load, train, test.Finally output result to OUTPUT_DIR*/
  private def run(args : Array[String]) : Unit = {
    //prepare arguments and local file system's enviroment for running algorithm
    parseArgs(args)
    val dataDir = extractCommon(DATA_DIR_KEY)
    val outputDir = extractCommon(OUTPUT_DIR_KEY)
    val benchmarkName = extractCommon(BENCHMARK_NAME)
    val rddDir = makePath(Array(dataDir, "data"))
    prepareEnvironment(dataDir, outputDir, benchmarkName)
    //algorithm's main processes
    val (loadTime, (trainData, testData)) = recordTime_1((path : String) => {
      val (t1, t2) = load(path)
      commonArgTable(LOAD_PATTERN) match {
      case "count" =>
        t1.count()
        t2.count()
      }
      (t1, t2)
    })(rddDir)
    val (trainTime, model) = recordTime_1(train)(trainData)
    val (testTime, _) = recordTime_2(test)(model,testData)
    // generate algorthim self's result file and all algorithm's stat file(only algorithm name and times)
    val statPrinter = new PrintWriter(new FileWriter(makePath(Array(outputDir, benchmarkName + ".csv"))), true)
    val singlePrinter = new PrintWriter(new FileWriter(makePath(Array(outputDir, "stat.csv"))), true)
    val convertTime = (millis : Long, format : String) => format match{
      case "ms" => "%dms".format(millis)
      case "s" => "%ds %dms".format(millis / 1000, millis % 1000)
      case "min" => "%dmin %ds %dms".format(millis / 60000, millis % 60000 / 1000, millis % 60000 % 1000)
      case * => throw new IllegalArgumentException("Unknown time format" + format)
    }
    val times = Array(loadTime, trainTime, testTime).map(convertTime(_, extractCommon(TIME_FORMAT)))
    statPrinter.println((benchmarkName +: times) mkString ",")
    singlePrinter.println((algArgNames.map(algArgTable(_)) ++ times ++ extraAlgResult) mkString ",")
    statPrinter.close()
    singlePrinter.close()
    sc.stop()
  }

  private def recordTime_1[A, R](f : A => R)(arg : A) ={
    val startTime = System.currentTimeMillis()
    val res = f(arg)
    (System.currentTimeMillis() - startTime, res)
  }
  private def recordTime_2[A1, A2, R](f : (A1, A2) => R)(arg1 : A1, arg2 : A2) ={
    val startTime = System.currentTimeMillis()
    val res = f(arg1, arg2)
    (System.currentTimeMillis() - startTime, res)
  }


  private def parseArgs(args : Array[String]) : Unit = {
    var index = -1
    val addArg = (arg : String, table : ArgTable) => { table.put(arg, args({index += 1; index})) }
    commonArgNames.foreach(addArg(_, commonArgTable))
    dataGenArgNames.foreach(addArg(_, dataGenArgTable))
    algArgNames.foreach(addArg(_, algArgTable))
  }

  /**
    * Generate and save algorithm's data set, you should achieve it by these steps:
    * 1. Parse arguments from dataGenTabl.
    * 2. geenerate data rdd by youself, mllib.util may help you.
    * 3. save it by calling saveAsTextFile(path).
    * @param path  the algorithm data directory path, eg: /user/XXX/kmean/data
    */
  def genData(path : String) : Unit


  /**
    * Load data from other place, your could use sc.textFile(dataPath) and convert RDD[String] your data format
    * @param dataPath data directory path in local fs or dfs
    * @return Tuple2 compose of trainData : RDD[T] and testData : RDD[T]
    */
  def load(dataPath : String) : (RDD[T], RDD[T])

  /**
    * Train model from trainData and you should parse arguments from algArgTable to configure model.
    * @param trainData  trainData provide by load method.
    * @return model that has been trained.
    */
  def train(trainData : RDD[T]) : M

  /**
    * Predict respone of testData by model.
    * @param model  model from train method.
    * @param testData testData from load method.
    */
  def test(model : M, testData : RDD[T]) : Unit
}
