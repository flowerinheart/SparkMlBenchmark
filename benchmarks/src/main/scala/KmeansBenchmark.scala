/*
 * (C) Copyright IBM Corp. 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object KmeansDataGen {
  def run(args: Array[String]) {
    if (args.length < 5) {
      println("usage: <datadir> <numPoints> <numClusters> <dimenstion> <scaling factor> [numpar]")
      System.exit(0)
    }
    val conf = new SparkConf
    conf.setAppName("Spark KMeans DataGen")
    val sc = new SparkContext(conf)

    val output = args(0)
    val numCluster = args(1).toInt
    val numPoint = args(2).toInt
    val numDim = args(3).toInt
    val scaling = args(4).toDouble
    val defPar = if (System.getProperty("spark.default.parallelism") == null) 2 else System.getProperty("spark.default.parallelism").toInt
    val numPar = if (args.length > 5) args(5).toInt else defPar

    val data = KMeansDataGenerator.generateKMeansRDD(sc, numPoint, numCluster, numDim, scaling, numPar)
    data.map(_.mkString(" ")).saveAsTextFile(output)

    sc.stop();
  }

}

object KmeansApp {
  def main(args: Array[String]) {
    if (args.length < 8) {
      println("usage: <numClusters> <maxIterations> <runs> <data_folder> <num_of_points> <DIMENSIONS> <SCALING> <NUM_OFPARATIONS>")
      System.exit(0)
    }
    KmeansDataGen.run(Array(args(3), args(0), args(4), args(5), args(6), args(7)))

    val metrics = new SparkMetrics[RDD[Vector], KMeansModel]("KMneas")
    //generate data


    //parse arguments
    metrics.onParseArg({
      val map = collection.mutable.HashMap[String, String]()
      map.put("K", args(2))
      map.put("maxIterations", args(3))
      map.put("runs", calculateRuns(args))
      map
    })
    val conf = new SparkConf
    val sc = new SparkContext(conf)


    // Load and parse the data
    // val parsedData = sc.textFile(input)
    val parsedData = metrics.onLoad({
      val data = sc.textFile("")
      val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()
      parsedData.count()
      parsedData
    })

    // Cluster the data into two classes using KMeans
    val clusters = metrics.onTrain({
      KMeans.train(parsedData, metrics("K").toInt,
        metrics("maxIterations").toInt,
        metrics("runs").toInt, KMeans.K_MEANS_PARALLEL, seed = 127L)
    })

    val vectorsAndClusterIdx = parsedData.map { point =>
      val prediction = clusters.predict(point)
      (point.toString, prediction)
    }

    // Evaluate clustering by computing Within Set Sum of Squared Errors
    metrics.onTest({
      val WSSSE = clusters.computeCost(parsedData)
    })

    metrics.onExit()

    sc.stop()
  }

  def calculateRuns(args: Array[String]): String = {
    if (args.length > 4) args(4)
    else "1"
  }
}
