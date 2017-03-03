/**
  * Created by darnell on 17-3-2.
  */
package skydata.spark.benchmark.clustering

import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import skydata.spark.benchmark.SparkMlBenchmark

abstract class MllibUnsupervisalBenchmark[M] extends SparkMlBenchmark[RDD[Vector], M] {
  val NUM_POINTS = Key("points")
  val NUM_CLUSTERS = Key("clusters")
  val DIMENSTION = Key("dimenstion")
  val SCALING = Key("scaling")
  val NUMPAR = Key("numpar")
  override lazy val dataGenArgNames : Array[Key] = Array(NUM_POINTS, NUM_CLUSTERS, DIMENSTION, SCALING, NUMPAR)



  def genUnlabeledData =
    KMeansDataGenerator.generateKMeansRDD(sc, dataGenArgTable(NUM_POINTS).toInt,
      algArgTable(NUM_CLUSTERS).toInt,
      dataGenArgTable(DIMENSTION).toInt,
      dataGenArgTable(SCALING).toDouble,
      dataGenArgTable(NUMPAR).toInt).map(_.mkString(" "))

  def loadUnlabeledData(dataPath : String) = {
    val data = sc.textFile(dataPath)
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()
    val splits = parsedData.randomSplit(Array(0.7, 0.3))
    val (trainingData, testData) = (splits(0), splits(1))
    trainingData.cache()
    testData.cache()
    (trainingData, testData)
  }
  override  def genData(path : String): Unit = genUnlabeledData.saveAsTextFile(path)
  override def load(dataPath: String): (RDD[Vector], RDD[Vector]) = loadUnlabeledData(dataPath)
  override def test(model: M, testData: RDD[Vector]): Unit
}
