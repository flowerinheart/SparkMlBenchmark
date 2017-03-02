package skydata.spark.benchmark
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-3-2.
  */
object RandomForestRegressionBenchmark extends MllibSupervisalBenchmark[RandomForestModel]{
  //subtype  method


  val N_TREES = Key("numTrees")
  val F_S_STRATEGY = Key("featureSubsetStrategy")
  val IMPURITY = Key("impurity")
  val MAXDEPTH = Key("maxDepth")
  val MAXBINS = Key("maxBins")
  override lazy val algArgNames : Array[Key] = Array(N_TREES, F_S_STRATEGY, IMPURITY, MAXDEPTH, MAXBINS)

  override def train(trainData: RDD[LabeledPoint]): RandomForestModel = {
    val categoricalFeaturesInfo = Map[Int, Int]()
    RandomForest.trainRegressor(trainData, categoricalFeaturesInfo, algArgTable(N_TREES).toInt,
      algArgTable(F_S_STRATEGY), algArgTable(IMPURITY), algArgTable(MAXDEPTH).toInt, algArgTable(MAXBINS).toInt)
  }

  override def test(model: RandomForestModel, testData: RDD[LabeledPoint]): Unit = predictorTest(model, testData)
}
