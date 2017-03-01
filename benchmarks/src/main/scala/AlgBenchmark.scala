
package skydata.spark.benchmark

import java.io.{File, FileWriter, PrintWriter}
import java.util.Scanner

import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by Darnell on 2017/2/21.
  */

object Util{

}


abstract class AlgBenchmark[D, M](){
  def main(args : Array[String]): Unit ={
    run(args)
  }

  val conf = new SparkConf
  val sc = new SparkContext(conf)

  val commonArgTable: ArgTable = new ArgTable()
  val dataGenArgTable: ArgTable = new ArgTable()
  val algArgTable: ArgTable = new ArgTable()





  type ArgTable = collection.mutable.HashMap[Key, String]

  object ArgKey extends Enumeration{
    def Key(name : String) = Value(name)
    def Key = Value
  }
  def Key(name : String) = ArgKey.Key(name)
  def Key = ArgKey.Key
  val DATA_DIR_KEY = Key
  val OUTPUT_DIR_KEY = Key
  val BENCHMARK_NAME = Key
  type Key = ArgKey.Value



  import util.Random.nextString
  commonArgTable.put(BENCHMARK_NAME, nextString(20))
  commonArgTable.put(DATA_DIR_KEY, makePath(Array("./data", commonArgTable(BENCHMARK_NAME))))
  commonArgTable.put(OUTPUT_DIR_KEY, makePath(Array("./result", commonArgTable(BENCHMARK_NAME))))




  def makePath(nameList : Array[String]) = nameList mkString File.separatorChar.toString
  def checkAndCreatDir(path : String) = {
    val file = new File(path)
    if(!file.exists())
      file.mkdir() match {
        case true => true
        case false => throw new RuntimeException("could create dir " + path)
      }
  }

  def run(args : Array[String]) : Unit = {
    parseArgs(args)
    def extract(key : Key) = {
      commonArgTable.get(key).get match {
        case str : String => str
        case _ => throw new RuntimeException("No " + key.toString + " argument")
      }
    }
    val dataDir = extract(DATA_DIR_KEY)
    val outputDir = extract(OUTPUT_DIR_KEY)
    val benchmarklName = extract(BENCHMARK_NAME)
    checkAndCreatDir(dataDir)
    checkAndCreatDir(outputDir)






    val statFile = new File(makePath(Array(outputDir, "stat.csv")))
    val singleFile = new File(makePath(Array(outputDir, benchmarklName + ".csv")))



    val timeHead = Array("loadTime", "trainTime", "testTime")
    val argumentList = algArgTable.keySet.toArray.sorted
    val singleHead = argumentList.map(_.toString) ++ timeHead
    val statHead = "modelName" +: timeHead


    val manifestFile = new File(makePath(Array(dataDir, "manifest.csv")))
    val rddDir = makePath(Array(dataDir, "data"))

    def checkManifest() : Boolean = {
      if(!manifestFile.exists())
        return false
      val scanner = new Scanner(manifestFile)
      val keys = scanner.nextLine().split(",")
      val values = scanner.nextLine().split(",")
      keys zip values forall((pair) => dataGenArgTable(ArgKey.withName(pair._1)) == pair._2)
    }
    if(! checkManifest()){
      if(manifestFile.exists()) {
        val rddDirFile = new File(rddDir)
        for (file <- rddDirFile.listFiles)
          file.delete()
        rddDirFile.delete()
      }
      if(statFile.exists())
        statFile.delete()
      if(singleFile.exists())
        singleFile.delete()



      genData(rddDir)
      val pt = new PrintWriter(manifestFile)
      val dataArgKey = dataGenArgTable.keySet.toArray.sorted
      pt.println(dataArgKey.map(_.toString).mkString(","))
      pt.println(dataArgKey.map(dataGenArgTable(_)).mkString(","))
      pt.close()
    }


    val b1 = statFile.exists()
    val b2 = singleFile.exists()
    val statPrinter = new PrintWriter(new FileWriter(statFile, true), true)
    val singlePrinter = new PrintWriter(new FileWriter(singleFile, true), true)
    if(! b1)
      statPrinter.println(statHead mkString ",")
    if(! b2)
      singlePrinter.println(singleHead mkString ",")

        
          
    val (loadTime, (trainData, testData)) = recordTime_1(load)(rddDir)
    val (trainTime, model) = recordTime_1(train)(trainData)
    val (testTime, _) = recordTime_2(test)(model,testData)

    val times = Array(loadTime, trainTime, testTime).map(_.toString)
    statPrinter.println((benchmarklName +: times).mkString(","))
    singlePrinter.println((argumentList.map(algArgTable(_)) ++ times) mkString ",")
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
  def genData(path : String) : Unit
  def parseArgs(args : Array[String]) : Unit
  def load(dataPath : String) : (D, D)
  def train(trainData : D) : M
  def test(model : M, testData : D) : Unit
}
