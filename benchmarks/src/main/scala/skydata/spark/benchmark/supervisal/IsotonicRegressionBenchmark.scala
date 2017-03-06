package skydata.spark.benchmark.supervisal

import org.apache.spark.mllib.regression.IsotonicRegression
import org.apache.spark.mllib.regression.{IsotonicRegressionModel, LabeledPoint}
import org.apache.spark.rdd.RDD

/**
  * Created by darnell on 17-3-3.
  */
object IsotonicRegressionBenchmark extends MllibSupervisalBenchmark[IsotonicRegressionModel]{


  val ISOTONIC = Key("Isotonic")
  override lazy val algArgNames = Array(ISOTONIC)

  override def train(trainData: RDD[LabeledPoint]): IsotonicRegressionModel =
    new IsotonicRegression().setIsotonic(algArgTable(ISOTONIC).toBoolean).run(trainData.map{lp =>
      (lp.label, lp.features(0), 1.0)
    })

  override def test(model: IsotonicRegressionModel, testData: RDD[LabeledPoint]): Unit =
    testData.map({lp =>
      model.predict(lp.features(0))
    })
}
