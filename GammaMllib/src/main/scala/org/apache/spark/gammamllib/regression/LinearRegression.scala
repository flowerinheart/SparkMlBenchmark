package org.apache.spark.gammamllib.regression

import org.apache.spark.gammamllib.linalg.distributed.GammaOp
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionModel}
import org.apache.spark.rdd.RDD

/**
  * Created by Darnell on 2017/4/15.
  */
class GammaLinearRegression {

//  def train(input: RDD[LabeledPoint]): LinearRegressionModel = {
//    new LinearRegressionWithSGD(stepSize, numIterations, 0.0, miniBatchFraction)
//      .run(input, initialWeights)
//  }
}


object GammaLinearRegression{
  def train(
             input: RDD[LabeledPoint]): LinearRegressionModel = {
    val data = GammaOp.convertToZ(input, true)
    val gamma = GammaOp.computeGrammianMatrix(data)



    val d = gamma.cols - 1
    val q = new breeze.linalg.DenseMatrix[Double](d, d)
    val t = new breeze.linalg.DenseMatrix[Double](d, 1)
    for(i <- 0 to d){
      for(j <- 0 to d + 1){
        if(j < d)
          q(i, j) = gamma(i, j)
        else
          t(i, 0) = gamma(i , j)
      }
    }

    val inversedQ = breeze.linalg.inv(q)
    val weight = inversedQ * t

    assert(weight.cols == 1)
    val intercept = weight(0, 0)
    val weights = Vectors.dense(weight.toArray.drop(0))



    new LinearRegressionModel(weights, intercept)
  }
}
