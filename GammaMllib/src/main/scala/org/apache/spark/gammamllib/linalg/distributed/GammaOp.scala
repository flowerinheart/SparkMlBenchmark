package org.apache.spark.gammamllib.linalg.distributed

/**
  * Created by Darnell on 2017/4/11.
  */

import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.mllib.linalg.{DenseMatrix, Matrix, SparseMatrix, Vector, Vectors}
import org.apache.spark.rdd.RDD
import breeze.linalg.{CSCMatrix => BSM, DenseMatrix => BDM, Matrix => BM}
import org.apache.spark.mllib.regression.LabeledPoint





import reflect.runtime.universe._
import com.github.fommil.netlib.LAPACK.{getInstance => LAPACK}



object GammaOp {
  def inverse(matrix : breeze.linalg.DenseMatrix[Double]) = {

  }
  def computeGrammianMatrix(data: RDD[Vector]) = asBreeze(new RowMatrix(data).computeGramianMatrix().asInstanceOf[DenseMatrix])



  def convertToZ[T : TypeTag](data : RDD[T], prefix : Boolean) = data match {
    case d if typeOf[T] <:< typeOf[RDD[Vector]] => d.asInstanceOf[RDD[Vector]].map(v => {
      if(prefix)
        Vectors.dense(1.0 +: v.toArray)
      else
        v
    })
    case d if typeOf[T] <:< typeOf[RDD[LabeledPoint]] => d.asInstanceOf[RDD[LabeledPoint]].map(p => {
      if(prefix)
          Vectors.dense(1.0 +: p.features.toArray :+ p.label)
      else
        Vectors.dense(p.features.toArray :+ p.label)
    })
  }


  def asBreeze(matrix: DenseMatrix): BM[Double] = {
    new BDM[Double](matrix.numRows, matrix.numCols, matrix.values)
  }


  def fromBreeze(breeze: BM[Double]): Matrix = {
    breeze match {
      case dm: BDM[Double] =>
        new DenseMatrix(dm.rows, dm.cols, dm.data, dm.isTranspose)
      case sm: BSM[Double] =>
        // Spark-11507. work around breeze issue 479.
        val mat = if (sm.colPtrs.last != sm.data.length) {
          val matCopy = sm.copy
          matCopy.compact()
          matCopy
        } else {
          sm
        }
        // There is no isTranspose flag for sparse matrices in Breeze
        new SparseMatrix(mat.rows, mat.cols, mat.colPtrs, mat.rowIndices, mat.data)
      case _ =>
        throw new UnsupportedOperationException(
          s"Do not support conversion from type ${breeze.getClass.getName}.")
    }
  }
}

