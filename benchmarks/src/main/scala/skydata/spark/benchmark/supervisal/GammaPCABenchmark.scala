package skydata.spark.benchmark.supervisal

import org.apache.spark.gammamllib.feature.PCA
import org.apache.spark.mllib.feature.PCAModel
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-3-1.
  */
object GammaPCABenchmark extends MllibSupervisalBenchmark[PCAModel]{


  val DIMENSIONS = Key("dimensions")
  override lazy val algArgNames : Array[Key] = Array(DIMENSIONS)

  //subtype  method
  override def genData(path: String): Unit = generateLinearData.saveAsTextFile(path)

  override def load(dataPath: String): (RDD[LabeledPoint], RDD[LabeledPoint]) = loadLabelPoint(dataPath)


  override def train(trainData: RDD[LabeledPoint]): PCAModel =
    new PCA(algArgTable(DIMENSIONS).toInt).fit(trainData.map(_.features))

  override def test(model: PCAModel, testData: RDD[LabeledPoint]): Unit = testData.map({point=>
    model.transform(point.features)})
}
