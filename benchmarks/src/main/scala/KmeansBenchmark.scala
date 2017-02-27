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


import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD



object KmeansBenchmark extends AlgBenchmark[RDD[Vector], KMeansModel](){
  def main(args: Array[String]): Unit = {
    run(args)
  }

  val NUM_CLUSTERS = Key("clusters")
  val MAX_ITERATION = Key("iteration")
  val RUNS = Key("runs")
  val NUM_POINTS = Key("points")
  val DIMENSTION = Key("dimenstion")
  val SCALING = Key("scaling")
  val NUMPAR = Key("numpar")





    override  def genData(path : String): Unit = {
    //args: <datadir> <numPoints> <numClusters> <dimenstion> <scaling factor> [numpar]
    val data = KMeansDataGenerator.generateKMeansRDD(sc, dataGenArgTable(NUM_POINTS).toInt,
                                                          algArgTable(NUM_CLUSTERS).toInt,
                                                          dataGenArgTable(DIMENSTION).toInt,
                                                          dataGenArgTable(SCALING).toDouble,
                                                          dataGenArgTable(NUMPAR).toInt)
    data.map(_.mkString(" ")).saveAsTextFile(path)
  }
  override def parseArgs(args: Array[String]): Unit = {
    var index = -1
    val increment = () => {index += 1; index}
    commonArgTable.put(DATA_DIR_KEY, args(increment()))
    commonArgTable.put(OUTPUT_DIR_KEY, args(increment()))
    commonArgTable.put(MODEL_NAME, "Kmeans")
    Array(NUM_CLUSTERS, MAX_ITERATION, RUNS).foreach(algArgTable.put(_, args(increment())))
    Array(NUM_POINTS, DIMENSTION, SCALING, NUMPAR).foreach(dataGenArgTable.put(_, args(increment())))
  }


  override def train(trainData: RDD[Vector]): KMeansModel = {
    KMeans.train(trainData, algArgTable(NUM_CLUSTERS).toInt,
      algArgTable(MAX_ITERATION).toInt,
      algArgTable(RUNS).toInt, KMeans.K_MEANS_PARALLEL, seed = 127L)
  }

  override def test(model : KMeansModel, testData : RDD[Vector]): Unit = {
    val WSSSE = model.computeCost(testData)
  }

  override def load(dataPath: String): (RDD[Vector], RDD[Vector]) = {
    val data = sc.textFile(dataPath)
    val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()
    parsedData.count()
    (parsedData, parsedData)
  }
}

