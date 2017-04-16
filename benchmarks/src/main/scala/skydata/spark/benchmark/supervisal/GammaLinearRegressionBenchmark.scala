package skydata.spark.benchmark.supervisal

/**
  * Created by Darnell on 2017/4/17.
  */

import org.apache.spark.gammamllib.regression._
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionModel}
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-3-1.
  */
object GammaLinearRegressionBenchmark extends MllibSupervisalBenchmark[LinearRegressionModel] {



  override def genData(path: String): Unit =
    generateLinearData.saveAsTextFile(path)

  override def load(dataPath: String): (RDD[LabeledPoint], RDD[LabeledPoint]) =
    loadLabelPoint(dataPath)

  override def train(trainData: RDD[LabeledPoint]): LinearRegressionModel =
    GammaLinearRegression.train(trainData)

  override def test(model: LinearRegressionModel, testData: RDD[LabeledPoint]): Unit =
    predictorTest(model, testData)
}
