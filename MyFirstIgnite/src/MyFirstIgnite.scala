
// (c) rumi 2017
// Important setup
// in order for any spark node to work, the next parameters should be setup
// in spark-env.sh
// export SPARK_CLASSPATH=$SPARK_CLASSPATH:<directory where the scala class will be installed, 


// to add either in spark-env.sh or in .bashrc or any file picked up before running spark
// Optionally set IGNITE_HOME here.
// IGNITE_HOME=/path/to/ignite
// export IGNITE_HOME=/usr/local/Cellar/apache-ignite/$IGNITE_VER
// IGNITE_LIBS="${IGNITE_HOME}/libs/*"

// for file in ${IGNITE_HOME}/libs/*
// do
//    if [ -d ${file} ] && [ "${file}" != "${IGNITE_HOME}"/libs/optional ]; then
//        IGNITE_LIBS=${IGNITE_LIBS}:${file}/*
//    fi
// done

// export SPARK_CLASSPATH=$IGNITE_LIBS



import org.apache.ignite.spark.{ IgniteContext, IgniteRDD }
import org.apache.log4j.{ Level, Logger }
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.ignite.configuration._

import scala.math

object MyFirstIgnite {

  def main(args: Array[String]) {
    println("Hello, world!")

    val conf = new SparkConf()
      .setAppName("IgniteRDDExample")
      //.setMaster("spark://rumi-mini.local:7077")
      .setMaster("spark://mbp15.local:7077")
      .set("spark.executor.memory", "1g")
      .set("spark.executor.cores", "8")
      .set("spark.driver.cores", "1")
      .set("spark.num.executors", "1")
      .set("spark.total.executor.cores", "8")
      .set("spark.cores.max", "8")
      .set("spark.executor.instances", "4")
      
    val CONFIG = "/usr/local/Cellar/apache-ignite/2.0.0/examples/config/spark/example-shared-rdd.xml"

    // Spark context.
    val sparkContext = new SparkContext(conf)

    // Adjust the logger to exclude the logs of no interest.
    Logger.getRootLogger.setLevel(Level.INFO)
    Logger.getLogger("org.apache.ignite").setLevel(Level.INFO)

    // Defines spring cache Configuration path.

    // Creates Ignite context with above configuration.
    val igniteContext = new IgniteContext(sparkContext, CONFIG, false)

    // Creates an Ignite Shared RDD of Type (Int,Int) Integer Pair.
    val sharedRDD: IgniteRDD[Int, Int] = igniteContext.fromCache[Int, Int]("sharedRDD")
    val sharedRDD2: IgniteRDD[Long, Long] = igniteContext.fromCache[Long, Long]("sharedRDD2")

    // Fill the Ignite Shared RDD in with Int pairs.
    sharedRDD.savePairs(sparkContext.parallelize(1 to 100000, 10).map(i => (i, i)))
    sharedRDD2.savePairs(sparkContext.parallelize(1 to 2000000, 10).map(i => (i, i)))

    // Transforming Pairs to contain their Squared value.
    sharedRDD.mapValues(x => (x * x))
    sharedRDD2.mapValues(x => (x * x))

    // Retrieve sharedRDD back from the Cache.
    val transformedValues: IgniteRDD[Int, Int] = igniteContext.fromCache("sharedRDD")

    // Perform some transformations on IgniteRDD and print.
    val squareAndRootPair = transformedValues.map { case (x, y) => (x, Math.sqrt(y.toDouble)) }

    println(">>> Transforming values stored in Ignite Shared RDD...")

    // Filter out pairs which square roots are less than 100 and
    // take the first five elements from the transformed IgniteRDD and print them.
    squareAndRootPair.filter(_._2 < 100.0).take(10).foreach(println)

    println(">>> Executing SQL query over Ignite Shared RDD...")

    // Execute a SQL query over the Ignite Shared RDD.
    val df = transformedValues.sql("select _val from Integer where _val < 100 and _val > 9 ")

    // Show ten rows from the result set.
    df.show(10)

    val sharedRDDDouble: IgniteRDD[Double, Double] = igniteContext.fromCache[Double, Double]("sharedRDD")

    sharedRDDDouble.mapValues(x => Math.sqrt(x))

    // Close IgniteContext on all workers.
    igniteContext.close(true)

    // Stop SparkContext.
    sparkContext.stop()

  }
}