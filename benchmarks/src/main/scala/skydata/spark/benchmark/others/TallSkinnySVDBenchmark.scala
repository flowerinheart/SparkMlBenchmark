package skydata.spark.benchmark.others

import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.{Matrix, SingularValueDecomposition, Vector}
import org.apache.spark.rdd.RDD
import skydata.spark.benchmark.clustering.MllibUnsupervisalBenchmark

/**
  * Created by darnell on 17-3-6.
  */
object TallSkinnySVDBenchmark extends MllibUnsupervisalBenchmark[SingularValueDecomposition[RowMatrix, Matrix]]{

  override def test(model: SingularValueDecomposition[RowMatrix, Matrix], testData: RDD[Vector]): Unit = {
  }

  override def train(trainData: RDD[Vector]): SingularValueDecomposition[RowMatrix, Matrix] = {
      val mat = new RowMatrix(trainData)
      mat.computeSVD(mat.numCols().toInt)
  }
}
