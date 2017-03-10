package skydata.spark.benchmark.others

import org.apache.spark.mllib.fpm.{FPGrowth, FPGrowthModel}
import org.apache.spark.rdd.RDD
import skydata.spark.benchmark.SparkMlBenchmark

/**
  * Created by darnell on 17-3-6.
  */
object SimpleFPGrowthBenchmark extends SparkMlBenchmark[Array[String], FPGrowthModel[String]]{
  //subtype  method
  val MAX_LEN = Key("max_len")
  val N_TRAN = Key("num_transaction")
  override lazy val dataGenArgNames = Array(MAX_LEN, N_TRAN)


  val MIN_SUP = Key("min_support")
  override lazy val algArgNames = Array(MIN_SUP)

  override def genData(path: String): Unit = {
    val random = scala.util.Random
    val low = 65
    val high = 90
    sc.parallelize(0 to dataGenArgTable(N_TRAN).toInt map{i => {
      val str = new StringBuilder
      var rand = util.Random.alphanumeric.filter((c) => c.isLetter && str.indexOf(c) == -1)
      0 to random.nextInt(dataGenArgTable(MAX_LEN).toInt + 1) map { j =>
        str.append(rand.head)
        rand = rand.tail
      }
      str.toString()
    }
    }).saveAsTextFile(path)
  }

  override def load(dataPath: String): (RDD[Array[String]], RDD[Array[String]]) = {
    val data = sc.textFile(dataPath).map(s =>
      s.split(" ")
    )
    val splits = data.randomSplit(Array(0.7, 0.3))
    (splits(0), splits(1))
  }

  override def train(trainData: RDD[Array[String]]): FPGrowthModel[String] ={
    val model = new FPGrowth()
    setAlgArg((s) => model.setMinSupport(s.toInt), MIN_SUP)
    model.run(trainData)
  }

  override def test(model: FPGrowthModel[String], testData: RDD[Array[String]]): Unit ={
  }
}
