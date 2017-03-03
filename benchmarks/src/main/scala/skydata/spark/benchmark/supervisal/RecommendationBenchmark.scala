/**
  * Created by darnell on 17-3-2.
  */
package skydata.spark.benchmark.supervisal

import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.LinearDataGenerator
import org.apache.spark.rdd.RDD

object RecommendationBenchmark extends MllibSupervisalBenchmark[MatrixFactorizationModel]{


  override lazy val dataGenArgNames : Array[Key] = Array(N_EXAMPLES, EPS, N_PARTITIONS, INTERCEPT)
  val RANK = Key("rank")
  val N_ITER = Key("numIterations")
  val LAMBDA = Key("lambda")
  override lazy val algArgNames = Array(RANK, N_ITER, LAMBDA)

  override def genData(path : String) : Unit =
    LinearDataGenerator.generateLinearRDD(sc, dataGenArgTable(N_EXAMPLES).toInt,
      2, dataGenArgTable(EPS).toDouble,
      dataGenArgTable(N_PARTITIONS).toInt, dataGenArgTable(INTERCEPT).toDouble).saveAsTextFile(path)

  override def train(trainData: RDD[LabeledPoint]): MatrixFactorizationModel = {
    val ratings = trainData.map({
      case LabeledPoint(label, feature) =>
        Rating(feature(0).toInt, feature(1).toInt, label)
    })
    ALS.train(ratings, algArgTable(RANK).toInt, algArgTable(N_ITER).toInt,
      algArgTable(LAMBDA).toDouble)
  }
  override def test(model : MatrixFactorizationModel, testData : RDD[LabeledPoint]) : Unit = {
    val usersProducts = testData.map({
      case LabeledPoint(label, features) => (features(0).toInt, features(1).toInt)
    })
    model.predict(usersProducts)
  }
}
