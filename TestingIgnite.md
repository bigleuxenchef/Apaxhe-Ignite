Working with Spark-Shell
---
Now that you have your cluster up and running, you can run spark-shell and check the integration.

Start spark shell:
--

Either by providing Maven coordinates to Ignite artifacts (you can use --repositories if you need, but it may be omitted):

Shell
-
```
./bin/spark-shell 
	--packages org.apache.ignite:ignite-spark:1.8.0
  --master spark://master_host:master_port
  --repositories http://repo.maven.apache.org/maven2/org/apache/ignite
  ```

Or by providing paths to Ignite jar file paths using --jars parameter

Shell
-
    
```
./bin/spark-shell --jars path/to/ignite-core.jar,path/to/ignite-spark.jar,path/to/cache-api.jar,path/to/ignite-log4j.jar,path/to/log4j.jar --master spark://master_host:master_port
```
You should see Spark shell started up.

Note that if you are planning to use spring configuration loading, you will need to add the ignite-spring dependency as well:

    Shell
```
./bin/spark-shell 
	--packages org.apache.ignite:ignite-spark:1.8.0,org.apache.ignite:ignite-spring:1.8.0
  --master spark://master_host:master_port
```
    Let's create an instance of Ignite context using default configuration:

Scala
--
```
import org.apache.ignite.spark._
import org.apache.ignite.configuration._

val ic = new IgniteContext(sc, () => new IgniteConfiguration())
```
You should see something like

Text
--
```
ic: org.apache.ignite.spark.IgniteContext = org.apache.ignite.spark.IgniteContext@62be2836
```


An alternative way to create an instance of IgniteContext is to use a configuration file. Note that if path to configuration is specified in a relative form, then the IGNITE_HOME environment variable should be globally set in the system as the path is resolved relative to IGNITE_HOME

Scala
--
```
import org.apache.ignite.spark._
import org.apache.ignite.configuration._

val ic = new IgniteContext(sc, "config/default-config.xml")
```
    Let's now create an instance of IgniteRDD using "partitioned" cache in default configuration:

Scala
--
```
val sharedRDD = ic.fromCache[Integer, Integer]("partitioned")
```

You should see an instance of RDD created for partitioned cache:

Text
--
    
```
shareRDD: org.apache.ignite.spark.IgniteRDD[Integer,Integer] = IgniteRDD[0] at RDD at IgniteAbstractRDD.scala:27
```
Note that creation of RDD is a local operation and will not create a cache in Ignite cluster.

   Let's now actually ask Spark to do something with our RDD, for example, get all pairs where value is less than 10:

Scala
--
```
sharedRDD.filter(_._2 < 10).collect()
```
As our cache has not been filled yet, the result will be an empty array:

Text
--
```
res0: Array[(Integer, Integer)] = Array()
```
Check the logs of remote spark workers and see how Ignite context will start clients on all remote workers in the cluster. You can also start command-line Visor and check that "partitioned" cache has been created.

   Let's now save some values into Ignite:

Scala
--
```
sharedRDD.savePairs(sc.parallelize(1 to 100000, 10).map(i => (i, i)))
```
After running this command you can check with command-line Visor that cache size is 100000 elements.

   We can now check how the state we created will survive job restart. Shut down the spark shell and repeat steps 1-3. You should again have an instance of Ignite context and RDD for "partitioned" cache. We can now check how many keys there are in our RDD which value is greater than 50000:

    Scala
    --
```
sharedRDD.filter(_._2 > 50000).count
```
Since we filled up cache with a sequence of number from 1 to 100000 inclusive, we should see 50000 as a result:

   Text
   --
```
res0: Long = 50000
```
   


## My Specific

after installing ignite in `/usr/local/Cellar/apache-ignite/1.9.0` and setting up `$IGNITE_HOME`, I have created a spark configuration file that works for me `myspark.conf` as follow :

```
spark.executor.memory 4g
spark.executor.cores 4
spark.driver.cores 1
spark.num.executors 1
spark.total.executor.cores 4
spark.cores.max 8
spark.driver.memory 4g
spark.master spark://MBP15.local:7077
```
then you can start spark-shell

```
./bin/spark-shell --packages org.apache.ignite:ignite-spark:1.9.0,org.apache.ignite:ignite-spring:1.9.0 --master spark://mbp15.local:7077 --properties-file ~/myspark.conf 
```
