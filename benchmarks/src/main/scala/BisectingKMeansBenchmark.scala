import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD
import skydata.spark.benchmark.MllibUnsupervisalBenchmark
import org.apache.spark.mllib.clustering.{BisectingKMeans, BisectingKMeansModel}

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

}
