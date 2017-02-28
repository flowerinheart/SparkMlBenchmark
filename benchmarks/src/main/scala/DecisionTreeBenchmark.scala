
package skydata.spark.benchmark
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.util.LinearDataGenerator
import org.apache.spark.rdd.RDD

/**
package skydata.spark.benchmark  * Created by darnell on 17-2-28.
  */
object DecisionTreeBenchmark extends AlgBenchmark[RDD[LabeledPoint], DecisionTreeModel]{

  val N_EXAMPLES = Key("num_of_examples")
  val N_FEATURES = Key("num_of_features")
  val N_PARTITIONS = Key("num_of_partitions")
  val EPS = Key("eps")
  val N_CLASS = Key("num_of_class_c")
  val IMPURITY = Key("impurity_C")
  val MAXDEPTH = Key("maxDepth_C")
  val MAXBINS = Key("maxBins_C")
  val MODE = Key("mode_c")

  override def parseArgs(args: Array[String]): Unit = {
    var index = -1
    def parse = (keys : Array[Key], table : ArgTable) => {
      keys.foreach(table.put(_, {
        index += 1
        args(index)
      }))
    }
    parse(Array(DATA_DIR_KEY, OUTPUT_DIR_KEY, BENCHMARK_NAME), commonArgTable)
    parse(Array(N_EXAMPLES, N_FEATURES, N_PARTITIONS, EPS), dataGenArgTable)
    parse(Array(MODE), algArgTable)
    algArgTable(MODE) match {
      case "Classification" =>
        parse(Array(N_CLASS, IMPURITY, MAXDEPTH, MAXBINS), algArgTable)
      case "Regression" =>
        parse(Array(IMPURITY, MAXDEPTH, MAXBINS), algArgTable)
      case _ => throw new IllegalArgumentException("Unknown mode, you must use Classification or Regression")
    }
  }




  override def genData(path: String): Unit = {
    LinearDataGenerator.generateLinearRDD(sc, dataGenArgTable(N_EXAMPLES).toInt,
      dataGenArgTable(N_FEATURES).toInt, dataGenArgTable(EPS).toInt,
      dataGenArgTable(N_PARTITIONS).toInt).saveAsTextFile(commonArgTable(DATA_DIR_KEY))
  }

  override def load(dataPath: String): (RDD[LabeledPoint], RDD[LabeledPoint]) = {
    val data = sc.textFile(commonArgTable(DATA_DIR_KEY)).map(LabeledPoint.parse(_))
    data.cache()
    (data, data)
  }

  override def train(trainData: RDD[LabeledPoint]): DecisionTreeModel = {
    val categoricalFeaturesInfo = Predef.Map[Int, Int]()
    algArgTable(MODE) match {
      case "Classification" => DecisionTree.trainClassifier(trainData, algArgTable(N_CLASS).toInt,
        categoricalFeaturesInfo, algArgTable(IMPURITY), algArgTable(MAXDEPTH).toInt, algArgTable(MAXBINS).toInt)
      case "Regression" => DecisionTree.trainRegressor(trainData, categoricalFeaturesInfo,
        algArgTable(IMPURITY), algArgTable(MAXDEPTH).toInt, algArgTable(MAXBINS).toInt)
      case _ => throw new IllegalArgumentException("Unknown mode, you must use Classification or Regression")
    }
  }



  override def test(model: DecisionTreeModel, testData: RDD[LabeledPoint]): Unit = {
    testData.map({ point =>
      model.predict(point.features)
    })
  }
}
