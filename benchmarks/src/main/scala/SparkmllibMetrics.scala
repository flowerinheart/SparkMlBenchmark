
package skydata.spark.benchmark
import java.io.{File, PrintWriter}

/**
  * Created by Darnell on 2017/2/24.
  */


class SparkmllibMetrics[D, M](val modelName : String) {
  var trainTime = 0.0
  var testTime = 0.0
  var loadTime = 0.0
  val statPrinter = new PrintWriter(Array("..", "result", modelName).mkString(File.separatorChar.toString))
  val singerPrinter = new PrintWriter(Array("..", "result", modelName).mkString(File.separatorChar.toString))
  var modelArgs: scala.collection.mutable.HashMap[String, String] = null
  var propertyName : Array[String] = null

  def onParseArg(args : => collection.mutable.HashMap[String, String]) : Unit = {
    modelArgs = args
    propertyName = modelArgs.keySet.toArray.sorted
    singerPrinter.println(propertyName.mkString(","))
  }

  def apply(key : String) = modelArgs(key)

  def onTrain(train : => M): M ={
    val start = System.nanoTime()
    val t = train
    val end = System.nanoTime()
    trainTime = (end - start) / 1000
    t
  }

  def onTest(test : => Unit): Unit = {
    val start = System.nanoTime()
    val t = test
    val end = System.nanoTime()
    testTime = (end - start) / 1000
  }

  def onLoad(load : => D): D ={
    val start = System.nanoTime()
    val t = load
    val end = System.nanoTime()
    loadTime = (end - start) / 1000
    t
  }

  def onExit() : Unit = {
    singerPrinter.close()
    statPrinter.close()
  }

  def outputResult():Unit = {
    statPrinter.println(Array(modelName, loadTime.toString, trainTime.toString, testTime.toString).mkString(","))
    singerPrinter.println(propertyName.map(modelArgs(_)).mkString(","))
  }
}
