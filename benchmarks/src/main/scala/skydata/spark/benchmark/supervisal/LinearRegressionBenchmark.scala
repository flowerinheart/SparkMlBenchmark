
package skydata.spark.benchmark.supervisal

import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionModel, LinearRegressionWithSGD}
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-3-1.
  */
object LinearRegressionBenchmark extends MllibSupervisalBenchmark[LinearRegressionModel] {
  val N_ITERATION = Key("num_iteration")
  override lazy val algArgNames : Array[Key] = Array(N_ITERATION)



  override def genData(path: String): Unit =
    generateLinearData.saveAsTextFile(path)

  override def load(dataPath: String): (RDD[LabeledPoint], RDD[LabeledPoint]) =
    loadLabelPoint(dataPath)

  override def train(trainData: RDD[LabeledPoint]): LinearRegressionModel =
    LinearRegressionWithSGD.train(trainData, algArgTable(N_ITERATION).toInt)

  override def test(model: LinearRegressionModel, testData: RDD[LabeledPoint]): Unit =
    predictorTest(model, testData)
}

