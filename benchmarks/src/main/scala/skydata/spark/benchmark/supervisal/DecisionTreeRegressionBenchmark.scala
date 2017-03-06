package skydata.spark.benchmark.supervisal

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-3-3.
  */
object DecisionTreeRegressionBenchmark extends MllibSupervisalBenchmark[DecisionTreeModel]{
  val IMPURITY = Key("impurity_C")
  val MAXDEPTH = Key("maxDepth_C")
  val MAXBINS = Key("maxBins_C")
  override lazy val algArgNames : Array[Key] = Array(IMPURITY, MAXDEPTH, MAXBINS)



  override def train(trainData: RDD[LabeledPoint]): DecisionTreeModel =
    DecisionTree.trainRegressor(trainData, Map[Int, Int](),
      algArgTable(IMPURITY), algArgTable(MAXDEPTH).toInt,
      algArgTable(MAXBINS).toInt)

  override def test(model: DecisionTreeModel, testData: RDD[LabeledPoint]): Unit = predictorTest(model, testData)
}
