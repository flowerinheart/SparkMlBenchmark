package skydata.spark.benchmark.others

import org.apache.spark.mllib.feature.{StandardScaler, StandardScalerModel}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD
import skydata.spark.benchmark.clustering.MllibUnsupervisalBenchmark

/**
  * Created by darnell on 17-3-6.
  */
object StandardScalerBenchmark extends MllibUnsupervisalBenchmark[StandardScalerModel]{


  val WITH_MEAN = Key("with_mean")
  val WITH_STD = Key("with_std")
  override lazy val algArgNames = Array(WITH_MEAN, WITH_STD)

  override def test(model: StandardScalerModel, testData: RDD[Vector]): Unit =
    model.transform(testData)

  override def train(trainData: RDD[Vector]): StandardScalerModel =
      new StandardScaler(algArgTable(WITH_MEAN).toBoolean,
        algArgTable(WITH_STD).toBoolean
      ).fit(trainData)
}
