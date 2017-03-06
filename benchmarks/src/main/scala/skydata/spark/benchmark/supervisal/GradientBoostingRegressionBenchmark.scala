package skydata.spark.benchmark.supervisal

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.GradientBoostedTrees
import org.apache.spark.mllib.tree.configuration.BoostingStrategy
import org.apache.spark.mllib.tree.model.GradientBoostedTreesModel
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-3-3.
  */
object GradientBoostingRegressionBenchmark extends MllibSupervisalBenchmark[GradientBoostedTreesModel]{
  val N_CLASS = Key("num_classes")
  val MAXDEPTH = Key("maxDepth_C")
  val MAXBINS = Key("maxBins_C")
  val MAX_ITER = Key("max_iterations")

  override lazy val algArgNames = Array(N_CLASS, MAXDEPTH, MAXBINS, MAX_ITER)


  override def train(trainData: RDD[LabeledPoint]): GradientBoostedTreesModel = {
    val boostingStrategy = BoostingStrategy.defaultParams("Regression")
    boostingStrategy.treeStrategy.setNumClasses(algArgTable(N_CLASS).toInt)
    boostingStrategy.treeStrategy.setMaxDepth(algArgTable(MAXDEPTH).toInt)
    boostingStrategy.treeStrategy.setMaxBins(algArgTable(MAXBINS).toInt)
    boostingStrategy.setNumIterations(algArgTable(MAX_ITER).toInt)
    boostingStrategy.treeStrategy.setCategoricalFeaturesInfo(Map[Int, Int]())
    GradientBoostedTrees.train(trainData, boostingStrategy)
  }


  override def test(model: GradientBoostedTreesModel, testData: RDD[LabeledPoint]): Unit = predictorTest(model, testData)
}

/**
  * Created by darnell on 17-3-3.
  */

