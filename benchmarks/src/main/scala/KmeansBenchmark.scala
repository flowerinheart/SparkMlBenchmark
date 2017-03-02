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


package skydata.spark.benchmark
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.{Vector, Vectors}
import org.apache.spark.mllib.util.KMeansDataGenerator
import org.apache.spark.rdd.RDD



object KmeansBenchmark extends SparkMlBenchmark[RDD[Vector], KMeansModel](){

  val NUM_CLUSTERS = Key("clusters")
  val MAX_ITERATION = Key("iteration")
  val RUNS = Key("runs")

  val NUM_POINTS = Key("points")
  val DIMENSTION = Key("dimenstion")
  val SCALING = Key("scaling")
  val NUMPAR = Key("numpar")

  override lazy val dataGenArgNames : Array[Key] = Array(NUM_POINTS, DIMENSTION, SCALING, NUMPAR)
  override lazy val algArgNames : Array[Key] = Array(NUM_CLUSTERS, MAX_ITERATION, RUNS)





    override  def genData(path : String): Unit = {
    //args: <datadir> <numPoints> <numClusters> <dimenstion> <scaling factor> [numpar]
      println("***************************" + commonArgTable(BENCHMARK_NAME))
      KMeansDataGenerator.generateKMeansRDD(sc, dataGenArgTable(NUM_POINTS).toInt,
                                                          algArgTable(NUM_CLUSTERS).toInt,
                                                          dataGenArgTable(DIMENSTION).toInt,
                                                          dataGenArgTable(SCALING).toDouble,
                                                          dataGenArgTable(NUMPAR).toInt).map(_.mkString(" ")).saveAsTextFile(path)
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

