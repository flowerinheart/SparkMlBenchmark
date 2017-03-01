package skydata.spark.benchmark
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-2-28.
  */
object SVMBenchmark extends AlgBenchmark[RDD[LabeledPoint], SVMModel]{
//  val STORAGE_LEVEL = Key("storage_level")
  override lazy val dataGenArgNames : Array[Key] =  Array(N_EXAMPLES, N_FEATURES, EPS, N_PARTITIONS, INTERCEPT)
  override lazy val algArgNames : Array[Key] = Array(N_ITERATION)

  override def genData(path: String): Unit = {
      generateLinearData.map({point =>
        new LabeledPoint(if(point.label > 0) 1 else 0, point.features)}
        ).saveAsTextFile(path)
  }
  override def load(dataPath: String): (RDD[LabeledPoint], RDD[LabeledPoint]) = loadLabelPoint(dataPath)
  override def train(trainData: RDD[LabeledPoint]): SVMModel = SVMWithSGD.train(trainData, algArgTable(N_ITERATION).toInt)
  override def test(model: SVMModel, testData: RDD[LabeledPoint]): Unit = predictorTest(model, testData)
}
