package skydata.spark.benchmark
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-2-28.
  */
object SVMBenchmark extends MllibSupervisalBenchmark[SVMModel]{
//  val STORAGE_LEVEL = Key("storage_level")
  val N_ITERATION = Key("num_iteration")
  override lazy val algArgNames : Array[Key] = Array(N_ITERATION)

  override def genData(path: String): Unit = {
      generateLinearData.map({point =>
        new LabeledPoint(if(point.label > 0) 1 else 0, point.features)}
        ).saveAsTextFile(path)
  }
  override def train(trainData: RDD[LabeledPoint]): SVMModel = SVMWithSGD.train(trainData, algArgTable(N_ITERATION).toInt)

  override def test(model: SVMModel, testData: RDD[LabeledPoint]): Unit = predictorTest(model, testData)
}
