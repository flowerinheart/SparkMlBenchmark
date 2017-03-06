package skydata.spark.benchmark.clustering

import org.apache.spark.mllib.clustering.{BisectingKMeans, BisectingKMeansModel}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-3-2.
  */
object BisectingKMeansBenchmark extends MllibUnsupervisalBenchmark[BisectingKMeansModel]{
  val ITERATION = Key("max_iteration")
  override lazy val algArgNames : Array[Key] = Array(ITERATION)


  override def train(trainData: RDD[Vector]): BisectingKMeansModel = {
    val bkm = new BisectingKMeans().
      setK(dataGenArgTable(NUM_CLUSTERS).toInt).
      setMaxIterations(algArgTable(ITERATION).toInt)
    bkm.run(trainData)
  }

  override def test(model: BisectingKMeansModel, testData: RDD[Vector]): Unit = model.predict(testData)
}
