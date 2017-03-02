
package skydata.spark.benchmark
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.rdd.RDD

/**
*package skydata.spark.benchmark  * Created by darnell on 17-2-28.
  */
object DecisionTreeClassificationBenchmark extends MllibSupervisalBenchmark[DecisionTreeModel]{


  val N_CLASS = Key("num_of_class_c")
  val IMPURITY = Key("impurity_C")
  val MAXDEPTH = Key("maxDepth_C")
  val MAXBINS = Key("maxBins_C")
  override lazy val algArgNames : Array[Key] = Array(N_CLASS, IMPURITY, MAXDEPTH, MAXBINS)



  override def genData(path: String): Unit = generateLinearData.map({point =>
          new LabeledPoint(Math.abs(point.label) % algArgTable(N_CLASS).toInt, point.features)}).
            saveAsTextFile(path)

  override def load(dataPath: String): (RDD[LabeledPoint], RDD[LabeledPoint]) = loadLabelPoint(dataPath)

  override def train(trainData: RDD[LabeledPoint]): DecisionTreeModel =
    DecisionTree.trainClassifier(trainData, algArgTable(N_CLASS).toInt,
      Predef.Map[Int, Int](), algArgTable(IMPURITY), algArgTable(MAXDEPTH).toInt, algArgTable(MAXBINS).toInt)

  override def test(model: DecisionTreeModel, testData: RDD[LabeledPoint]): Unit = predictorTest(model, testData)
}
