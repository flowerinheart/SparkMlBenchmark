package org.apache.spark.gammamllib.feature

import breeze.linalg.{DenseMatrix => BDM, DenseVector => BDV, SparseVector => BSV, axpy => brzAxpy, svd => brzSvd}
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.gammamllib.linalg.distributed.GammaOp
import org.apache.spark.mllib.feature.PCAModel
import org.apache.spark.mllib.linalg.{Matrices, SparseMatrix, SparseVector, Vector, Vectors}
import org.apache.spark.rdd.RDD

class PCA  (val k: Int) {
  require(k > 0,
    s"Number of principal components must be positive but got ${k}")

  /**
    * Computes a [[PCAModel]] that contains the principal components of the input vectors.
    *
    * @param sources source vectors
    */
  def fit(sources: RDD[Vector]): PCAModel = {
    val data = GammaOp.convertToZ(sources, true)
    val numFeatures = data.first().size - 1
    require(k <= numFeatures,
      s"source vector size $numFeatures must be no less than k=$k")
//    val data = sources.map(v =>{
//      val a = (v.toArray :+ 1).asInstanceOf[Array[Double]]
//      Vectors.dense(a)
//    })

    val gamma = GammaOp.computeGrammianMatrix(data)
    val relations = new breeze.linalg.DenseMatrix[Double](numFeatures, numFeatures)
    val m = gamma(0, 0)
    for(i <- 1 until numFeatures){
      for(j <- i until numFeatures){
        val la = gamma(0, i)
        val lb = gamma(0, j)
        val temp = (m * gamma(i, j)  - la * lb) / Math.sqrt(
          (m * gamma(i, i) - la * la) *
          (m * gamma(j, j) - lb * lb)
        )
        relations(i - 1, j - 1) = temp
        relations(j - 1, i - 1) = temp
      }
    }




    val brzSvd.SVD(u: BDM[Double], s: BDV[Double], _) = brzSvd(relations)
    val eigenSum = s.data.sum
    val temp = s.data.map(_ / eigenSum)

    val n = numFeatures
    val (pc, explainedVariance) = if (k == n) {
      (Matrices.dense(n, k, u.data), Vectors.dense(temp))
    } else {
      (Matrices.dense(n, k, java.util.Arrays.copyOfRange(u.data, 0, n * k)),
        Vectors.dense(java.util.Arrays.copyOfRange(temp, 0, k)))
    }

    val densePC = pc match {
      case dm: org.apache.spark.mllib.linalg.DenseMatrix =>
        dm
      case sm: SparseMatrix =>
        /* Convert a sparse matrix to dense.
         *
         * RowMatrix.computePrincipalComponents always returns a dense matrix.
         * The following code is a safeguard.
         */
        sm.toDense
      case m =>
        throw new IllegalArgumentException("Unsupported matrix format. Expected " +
          s"SparseMatrix or DenseMatrix. Instead got: ${m.getClass}")
    }
    val denseExplainedVariance = explainedVariance match {
      case dv: org.apache.spark.mllib.linalg.DenseVector =>
        dv
      case sv: SparseVector =>
        sv.toDense
    }
    new PCAModel(k, densePC, denseExplainedVariance)
  }

  /**
    * Java-friendly version of `fit()`.
    */
  def fit(sources: JavaRDD[Vector]): PCAModel = fit(sources.rdd)
}

