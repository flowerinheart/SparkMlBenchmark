/**
  * Created by darnell on 17-3-2.
  */
package skydata.spark.benchmark.supervisal

import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.LinearDataGenerator
import org.apache.spark.mllib.util.MLUtils.loadLabeledPoints
import org.apache.spark.rdd.RDD
import skydata.spark.benchmark.SparkMlBenchmark

import scala.util.Random
import com.github.fommil.netlib.BLAS.{getInstance => blas}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.annotation.{DeveloperApi, Since}



abstract class MllibSupervisalBenchmark[M] extends SparkMlBenchmark[LabeledPoint, M]{
  //help data and func
  val N_EXAMPLES = Key("num_of_examples")
  val N_FEATURES = Key("num_of_features")
  val EPS = Key("eps")
  val N_PARTITIONS = Key("num_of_partitions")
  val INTERCEPT = Key("intercept")
  override lazy val dataGenArgNames : Array[Key] = Array(N_EXAMPLES, N_FEATURES, EPS, N_PARTITIONS, INTERCEPT)


  def loadLabelPoint(dataPath : String) = {
    val data = loadLabeledPoints(sc, dataPath)
    val splits = data.randomSplit(Array(0.7, 0.3))
    val (trainingData, testData) = (splits(0), splits(1))
    trainingData.cache()
    testData.cache()
    (trainingData, testData)
  }



  def generateLinearData() =
    LinearDataGenerator.generateLinearRDD(sc, dataGenArgTable(N_EXAMPLES).toInt,
      dataGenArgTable(N_FEATURES).toInt, dataGenArgTable(EPS).toDouble,
      dataGenArgTable(N_PARTITIONS).toInt, dataGenArgTable(INTERCEPT).toDouble)

  def generateClassficationData() = {

      val nexamples: Int = dataGenArgTable(N_EXAMPLES).toInt
      val nfeatures: Int = dataGenArgTable(N_FEATURES).toInt
      val parts: Int = dataGenArgTable(N_PARTITIONS).toInt
      val globalRnd = new Random(94720)
      //val trueWeights = Array.fill[Double](nfeatures + 1)(globalRnd.nextGaussian())
      val trueWeights = Array.fill[Double](nfeatures )(globalRnd.nextGaussian())

      val data: RDD[LabeledPoint] = sc.parallelize(0 until nexamples, parts).map { idx =>
        val rnd = new Random(42 + idx)

        val x = Array.fill[Double](nfeatures) {
        rnd.nextDouble() * 2.0 - 1.0
        }
        val yD = blas.ddot(trueWeights.length, x, 1, trueWeights, 1) + rnd.nextGaussian() * 0.1
        val y = if (yD < 0) 0.0 else 1.0
        LabeledPoint(y, Vectors.dense(x))
      }
      data
  }

  type PredictModel = { def predict(value : Vector) : Double }
  def predictorTest(model : PredictModel, testData : RDD[LabeledPoint]) = {
    testData.map({ point =>
      model.predict(point.features)
    })
  }


  override def genData(path : String) : Unit = generateLinearData().saveAsTextFile(path)
  override def load(dataPath : String) : (RDD[LabeledPoint], RDD[LabeledPoint]) = loadLabelPoint(dataPath)
}
