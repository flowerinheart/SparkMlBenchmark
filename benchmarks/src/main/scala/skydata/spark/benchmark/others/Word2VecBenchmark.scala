package skydata.spark.benchmark.others

import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD
import skydata.spark.benchmark.clustering.MllibUnsupervisalBenchmark

/**
  * Created by darnell on 17-3-6.
  */
object Word2VecBenchmark extends MllibUnsupervisalBenchmark[Word2VecModel] {

  val W_SIZE = Key("windows_size")
  val V_SIZE = Key("vector_size")
  val MIN_COUNT = Key("min_count")
  val MAX_SEN_LEN = Key("max_sentence_length")

  override lazy val algArgNames = Array(W_SIZE, V_SIZE, MIN_COUNT, MAX_SEN_LEN)

  override def test(model: Word2VecModel, testData: RDD[Vector]): Unit = {
    testData.map({v =>
      model.findSynonyms(v, 5)
    })
  }

  override def train(trainData: RDD[Vector]): Word2VecModel = {
    val data = trainData.map(_.toArray.map(_.toInt.toString).toIterable).cache()
    val set_arg = (fun : Int => AnyRef, t : Key) => {
      val str = algArgTable(t)
      if(str == "default")
        None
      else
        fun(str.toInt)
    }
    val word2Vec = new Word2Vec()
    set_arg((i : Int) => {word2Vec.setWindowSize(i)}, W_SIZE)
    set_arg((i : Int) => {word2Vec.setVectorSize(i)}, V_SIZE)
    set_arg((i : Int) => {word2Vec.setMinCount(i)}, MIN_COUNT)
    set_arg((i : Int) => {word2Vec.setMaxSentenceLength(i)}, MAX_SEN_LEN)
    word2Vec.fit(data)
  }
}
