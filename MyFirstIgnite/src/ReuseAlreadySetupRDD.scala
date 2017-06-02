
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

object ReuseAlreadySetupRDD {

  def main(args: Array[String]) {
    println("Hello, world!")

    val conf = new SparkConf()
      .setAppName("IgniteRDDExample")
      //.setMaster("master")
      //.setMaster("spark://rumi-mini.local:7077")
      .setMaster("spark://mbp15.local:7077")

      .set("spark.executor.instances", "1")
    val CONFIG = "/usr/local/Cellar/apache-ignite/2.0.0/examples/config/spark/example-shared-rdd.xml"

    // Spark context.
    val sparkContext = new SparkContext(conf)

    // Adjust the logger to exclude the logs of no interest.
    Logger.getRootLogger.setLevel(Level.ERROR)
    Logger.getLogger("org.apache.ignite").setLevel(Level.INFO)

    // Defines spring cache Configuration path.

    // Creates Ignite context with above configuration.
    val igniteContext = new IgniteContext(sparkContext, CONFIG, false)

  
    
    // Retrieve sharedRDD back from the Cache.
    val sharedRDD: IgniteRDD[Int, Int] = igniteContext.fromCache[Int, Int]("sharedRDD")
       
    
   sharedRDD.sortByKey().take(10).foreach(println)
   
   sharedRDD.savePairs(sharedRDD.map { case (x, y) => (x, y + 1) },true)
   
   
   sharedRDD.sortByKey().take(10).foreach(println)

   //cacheRdd.savePairs(sparkContext.parallelize(1 to 10000, 10).map(i => (i, i)))
   
    
    // Execute a SQL query over the Ignite Shared RDD.
    //val df = transformedValues.sql("select _val from Integer where _val < 100 and _val > 9 ")
    val df = sharedRDD.sql("select _val from Integer where _val  < 9 ")

    
    
    // Show ten rows from the result set.
    df.show(10)

  
  
    // Close IgniteContext on all workers.
    igniteContext.close(false)

    // Stop SparkContext.
    sparkContext.stop()

  }
}