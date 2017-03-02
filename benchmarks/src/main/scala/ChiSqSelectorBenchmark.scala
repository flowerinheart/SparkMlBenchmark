import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import skydata.spark.benchmark.{MllibSupervisalBenchmark, MllibUnsupervisalBenchmark}
import org.apache.spark.mllib.feature.{ChiSqSelector, ChiSqSelectorModel}
import org.apache.spark.mllib.linalg.Vectors

/**
  * Created by darnell on 17-3-3.
  */
object ChiSqSelectorBenchmark extends MllibSupervisalBenchmark[ChiSqSelectorModel] {

  val TOPF = Key("top_features")
  override lazy val algArgNames = Array(TOPF)

  // Discretize data in 16 equal bins since ChiSqSelector requires categorical features
  // Even though features are doubles, the ChiSqSelector treats each unique value as a category
  override def genData(path : String) : Unit = generateLinearData.map{lp =>
    LabeledPoint(lp.label, Vectors.dense(lp.features.toArray.map { x => (x / 16).floor }))
  }.saveAsTextFile(path)

  override def train(trainData: RDD[LabeledPoint]): ChiSqSelectorModel =
    new ChiSqSelector(algArgTable(TOPF).toInt).fit(trainData)



  override def test(model: ChiSqSelectorModel, testData: RDD[LabeledPoint]): Unit = testData.map({lp =>
    LabeledPoint(lp.label, model.transform(lp.features))
  })

}
