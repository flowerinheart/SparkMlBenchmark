package skydata.spark.benchmark.supervisal

import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithSGD}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.LogisticRegressionDataGenerator
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-3-1.
  */
object LogisticRegressionBenchmark extends MllibSupervisalBenchmark[LogisticRegressionModel]{
  val PROBONE = Key("probOne")
  val N_ITERATION = Key("num_iteration")
  override lazy val dataGenArgNames : Array[Key] = Array(N_EXAMPLES, N_FEATURES, EPS, N_PARTITIONS, PROBONE)
  override lazy val algArgNames : Array[Key] = Array(N_ITERATION)



  //subtype  method
  override def genData(path: String): Unit =
    LogisticRegressionDataGenerator.generateLogisticRDD(sc, dataGenArgTable(N_EXAMPLES).toInt,
      dataGenArgTable(N_FEATURES).toInt, dataGenArgTable(EPS).toDouble, dataGenArgTable(N_PARTITIONS).toInt,
      dataGenArgTable(PROBONE).toDouble).saveAsTextFile(path)

  override def load(dataPath: String): (RDD[LabeledPoint], RDD[LabeledPoint]) =
    loadLabelPoint(dataPath)

  override def train(trainData: RDD[LabeledPoint]): LogisticRegressionModel =
    LogisticRegressionWithSGD.train(trainData, algArgTable(N_ITERATION).toInt)

  override def test(model: LogisticRegressionModel, testData: RDD[LabeledPoint]): Unit =
    predictorTest(model, testData)

}
