

import java.io.{File, FileWriter, PrintWriter}

import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by Darnell on 2017/2/21.
  */
abstract class AlgBenchmark[D, M](){
  val conf = new SparkConf
  val sc = new SparkContext(conf)

  val commonArgTable: ArgTable = new ArgTable()
  val dataGenArgTable: ArgTable = new ArgTable()
  val algArgTable: ArgTable = new ArgTable()



  type ArgTable = collection.mutable.HashMap[Key, String]
  class ArgKey extends Enumeration{
    def Key = ArgKey.Value
  }
  object ArgKey extends ArgKey{
  }
  def Key = ArgKey.Key
  type Key = ArgKey.Value
  val DATA_DIR_KEY, OUTPUT_DIR_KEY, MODEL_NAME = Key

  def run(args : Array[String]) = {
    parseArgs(args)
    def extract(key : Key) = {
      commonArgTable.get(key).get match {
        case str : String => str
        case _ => throw new RuntimeException("No " + key.toString + " argument")
      }
    }
    val dataDir = extract(DATA_DIR_KEY)
    val outputDir = extract(OUTPUT_DIR_KEY)
    val modelName = extract(MODEL_NAME)

    if(! (new File(dataDir)).exists())
      genData()
    val (loadTime, (trainData, testData)) = recordTime_1(load)(dataDir)
    val (trainTime, model) = recordTime_1(train)(trainData)
    val (testTime, _) = recordTime_2(test)(model,testData)

    val statFile = new File(Array(outputDir, "stat.csv") mkString File.separator)
    val singleFile = new File(Array(outputDir, modelName + ".csv") mkString File.separatorChar.toString)
    val statPrinter = new PrintWriter(new FileWriter(statFile, true))
    val singlePrinter = new PrintWriter(new FileWriter(singleFile, true))


    val timeHead = Array("loadTime", "trainTime", "testTime")
    val argumentList = algArgTable.keySet.toArray.sorted
    val singleHead = argumentList ++ timeHead
    val statHead = "modelName" +: timeHead

    if(! statFile.exists())
      statPrinter.println(statHead mkString ",")
    if(! singleFile.exists())
      singlePrinter.println(singleHead mkString ",")

    statPrinter.println(Array(modelName, loadTime, trainTime, testTime).mkString(","))
    singlePrinter.println(argumentList.map(algArgTable(_)) ++
      Array(loadTime, trainTime, testTime).map(_.toString) mkString ",")
    statPrinter.close()
    singlePrinter.close()
    sc.stop()
  }

  def recordTime_1[A, R](f : A => R)(arg : A) ={
    val startTime = System.currentTimeMillis()
    val res = f(arg)
    ((System.currentTimeMillis() - startTime) / 1000, res)
  }
  def recordTime_2[A1, A2, R](f : (A1, A2) => R)(arg1 : A1, arg2 : A2) ={
    val startTime = System.currentTimeMillis()
    val res = f(arg1, arg2)
    ((System.currentTimeMillis() - startTime) / 1000, res)
  }
  def output(outputDir : String, commonTable : ArgTable, algTable : ArgTable, dataGenTable : ArgTable): Unit ={

  }
  abstract def genData() : Unit
  abstract def parseArgs(args : Array[String]) : Unit
  abstract def load(dataPath : String) : (D, D)
  abstract def train(trainData : D) : M
  abstract def test(model : M, testData : D) : Unit
}
