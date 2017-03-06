package skydata.spark.benchmark.others

import org.apache.spark.mllib.fpm.{PrefixSpan, PrefixSpanModel}
import org.apache.spark.rdd.RDD
import skydata.spark.benchmark.SparkMlBenchmark

/**
  * Created by darnell on 17-3-6.
  */
object PrefixSpanBenchmark extends SparkMlBenchmark[RDD[Array[Array[Int]]], PrefixSpanModel[Int]]{
  //subtype  method
  val N_EXAMPLES = Key("N_EXAMPLES")
  val MAX_COL = Key("max_col")
  val MAX_ROW = Key("max_row")


  val MIN_SUP = Key("min_support")
  val MAX_PATTERN_LENGTH = Key("max_pattern_length")

  override lazy val dataGenArgNames = Array(N_EXAMPLES, MAX_ROW, MAX_COL)
  override lazy val algArgNames = Array(MIN_SUP, MAX_PATTERN_LENGTH)

  override def genData(path: String): Unit = {
    val random = scala.util.Random

    val data = sc.parallelize(0.to(dataGenArgTable(N_EXAMPLES).toInt).map { i =>
      0.to(random.nextInt(dataGenArgTable(MAX_ROW).toInt - 1) + 1).map { j =>
        0.to(random.nextInt(dataGenArgTable(MAX_COL).toInt - 1) + 1).map {k =>
          random.nextInt()
        }.mkString(",")
      }.mkString(".")
    }) saveAsTextFile(path)
  }

  override def load(dataPath: String): (RDD[Array[Array[Int]]], RDD[Array[Array[Int]]]) = {
    val data = sc.textFile(dataPath).map({s =>
      s.split(".").map({s =>
        s.split(",").map(_.toInt)
      })
    })
    val splits = data.randomSplit(Array(0.7, 0.3))
    (splits(0), splits(1))
  }

  override def train(trainData: RDD[Array[Array[Int]]]): PrefixSpanModel[Int] = {
    val set_arg = (fun : (String) => AnyRef, key : Key) => {
      val str = algArgTable(key)
      if(str != "default")
        fun(str)
    }
    val model = new PrefixSpan()
    set_arg((s : String) =>{model.setMinSupport(s.toDouble)}, MIN_SUP)
    set_arg((s : String) => {model.setMaxPatternLength(s.toInt)}, MAX_PATTERN_LENGTH)
    model.run(trainData)
  }


  override def test(model: PrefixSpanModel[Int], testData: RDD[Array[Array[Int]]]): Unit ={
  }
}
