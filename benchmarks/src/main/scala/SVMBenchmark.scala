package skydata.spark.benchmark
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.util.LinearDataGenerator
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-2-28.
  */
object SVMBenchmark extends AlgBenchmark[RDD[LabeledPoint], SVMModel]{

  //  val STORAGE_LEVEL = Key("storage_level")
  val N_EXAMPLES = Key("num_of_examples")
  val N_FEATURES = Key("num_of_features")
  val N_PARTITIONS = Key("num_of_partitions")
  val EPS = Key("eps")

  val N_ITERATION = Key("num_iteration")


  override def parseArgs(args: Array[String]): Unit = {
    var index = -1
    def parse = (keys : Array[Key], table : ArgTable) =>
      keys.foreach(table.put(_, { index += 1; args(index)} ))
    parse(Array(DATA_DIR_KEY, OUTPUT_DIR_KEY, BENCHMARK_NAME), commonArgTable)
    parse(Array(N_EXAMPLES, N_FEATURES, N_PARTITIONS, EPS), dataGenArgTable)
    parse(Array(N_ITERATION), algArgTable)
  }

  override def genData(path: String): Unit = {
      LinearDataGenerator.generateLinearRDD(sc, dataGenArgTable(N_EXAMPLES).toInt,
        dataGenArgTable(N_FEATURES).toInt, dataGenArgTable(EPS).toInt,
        dataGenArgTable(N_PARTITIONS).toInt).saveAsTextFile(commonArgTable(DATA_DIR_KEY))
  }

  override def load(dataPath: String): (RDD[LabeledPoint], RDD[LabeledPoint]) = {
    val data = sc.textFile(dataPath).map(LabeledPoint.parse)
    var training = data.sample(withReplacement = false, 0.7, 11L)
    val test = data.subtract(training)
    training.cache()
    test.cache()
    (training, test)
  }

  override def train(trainData: RDD[LabeledPoint]): SVMModel = {
    SVMWithSGD.train(trainData, algArgTable(N_ITERATION).toInt)
  }
  override def test(model: SVMModel, testData: RDD[LabeledPoint]): Unit = {
    testData.foreach({ points =>
      model.predict(points.features)
    })
  }
}
