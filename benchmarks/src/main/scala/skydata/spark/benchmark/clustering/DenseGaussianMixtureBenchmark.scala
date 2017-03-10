package skydata.spark.benchmark.clustering

import org.apache.spark.mllib.clustering.{GaussianMixture, GaussianMixtureModel}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-3-3.
  */
object DenseGaussianMixtureBenchmark extends MllibUnsupervisalBenchmark[GaussianMixtureModel]{


  val BLAS = "blas"
  addMetrics(("blas", "BLAS"))
  override lazy val algArgNames = Array(BLAS)

  val CONTOL = Key("convergenceTol")
  override lazy val algArgNames = Array(CONTOL, MAX_ITER)

  override def train(trainData: RDD[Vector]): GaussianMixtureModel =
    new GaussianMixture().setK(dataGenArgTable(N_CLUSTERS).toInt).
      setConvergenceTol(algArgTable(CONTOL).toDouble).
      setMaxIterations(algArgTable(MAX_ITER).toInt).
      run(trainData)

  override def test(model: GaussianMixtureModel, testData: RDD[Vector]): Unit = model.predict(testData)
}
