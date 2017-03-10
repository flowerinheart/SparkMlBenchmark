package skydata.spark.benchmark.clustering

import org.apache.spark.mllib.clustering.{PowerIterationClustering, PowerIterationClusteringModel}
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-3-6.
  */
object PowerIterationClusteringBenchmark extends MllibUnsupervisalBenchmark[PowerIterationClusteringModel]{


  val N_CIR = Key("n_circles")
  val INIT_MODE = Key("initializationMode")
  override lazy val dataGenArgNames = Array(N_CIR, N_POINTS)
  override lazy val algArgNames = Array(N_CLUSTERS, MAX_ITER, INIT_MODE)


  def gaussianSimilarity(p1: (Double, Double), p2: (Double, Double)): Double = {
    val ssquares = (p1._1 - p2._1) * (p1._1 - p2._1) + (p1._2 - p2._2) * (p1._2 - p2._2)
    math.exp(-ssquares / 2.0)
  }


  override def load(dataPath: String): (RDD[Vector], RDD[Vector]) = {
    val data = sc.textFile(dataPath).map(s =>
      Vectors.dense(s.split(" ").map(_.toDouble))
    )
    val splits = data.randomSplit(Array(0.7, 0.3))
    (splits(0), splits(1))
  }
  override  def genData(path : String): Unit = {
    val nCircles: Int = dataGenArgTable(N_CIR).toInt
    val nPoints: Int = dataGenArgTable(N_POINTS).toInt
    val points = (1 to nCircles).flatMap { radius =>
      val n = radius * nPoints
      Seq.tabulate(n) { j =>
        val theta = 2.0 * math.Pi * j / n
        (radius * math.cos(theta), radius * math.sin(theta))
      }
    }.zipWithIndex

      val rdd = sc.parallelize(points)
      val t = rdd.cartesian(rdd).flatMap { case (((x0, y0), i0), ((x1, y1), i1)) =>
        if (i0 < i1) {
          Some(Vectors.dense(i0.toLong, i1.toLong, gaussianSimilarity((x0, y0), (x1, y1))))
        } else {
          None
        }
      }
      t.map(vector =>
        vector.toArray.mkString(" ")
      ).saveAsTextFile(path)
  }
  override def train(trainData: RDD[Vector]): PowerIterationClusteringModel =
    new PowerIterationClustering().
      setK(algArgTable(N_CLUSTERS).toInt).
      setMaxIterations(algArgTable(MAX_ITER).toInt).
      setInitializationMode(algArgTable(INIT_MODE)).
      run(trainData.map({vector =>
        (vector(0).toLong, vector(1).toLong, vector(2))
      }))
  override def test(model: PowerIterationClusteringModel, testData: RDD[Vector]): Unit = {
  }
}
