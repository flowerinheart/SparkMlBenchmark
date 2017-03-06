package skydata.spark.benchmark.others

import org.apache.spark.mllib.feature.{IDF, IDFModel}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD
import skydata.spark.benchmark.clustering.MllibUnsupervisalBenchmark

/**
  * Created by darnell on 17-3-6.
  */
object TFIDFBenchmark extends MllibUnsupervisalBenchmark[IDFModel]{

  override def test(model: IDFModel, testData: RDD[Vector]): Unit =
    model.transform(testData)

  override def train(trainData: RDD[Vector]): IDFModel =
    new IDF().fit(trainData)
}
