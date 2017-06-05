# Apache-Ignite 


## Prerequesites and configuration


spark-env.sh

this needs to be added in the file to make it work!

```shell

# Optionally set IGNITE_HOME here.
# IGNITE_HOME=/path/to/ignite

export IGNITE_VER=2.0.0
export IGNITE_HOME=/usr/local/Cellar/apache-ignite/$IGNITE_VER

IGNITE_LIBS="${IGNITE_HOME}/libs/*"

for file in ${IGNITE_HOME}/libs/*
do
    if [ -d ${file} ] && [ "${file}" != "${IGNITE_HOME}"/libs/optional ]; then
        IGNITE_LIBS=${IGNITE_LIBS}:${file}/*
    fi
done

export SPARK_CLASSPATH=$SPARK_CLASSPATH:$IGNITE_LIBS

```
#### note on eclipse
- as some libraries are duplicated between ignite and hadoop, it is important that either in eclipse or classpath that ignite libraries come first in order.
- when including library in eclipse, make sure to eliminates teh library not complie with the right scala version like here under

```
Description	Resource	Path	Location	Type
spark-unsafe_2.10-2.1.0.jar of MyFirstIgnite build path is cross-compiled with an incompatible version of Scala (2.10.0). In case this report is mistaken, this check can be disabled in the compiler preference page.	MyFirstIgnite		Unknown	Scala Version Problem
```

### note on ignite & spark

as mentioned in already, I like to est different os, different versions and I like to push the boundaries to make the environment very heterogeneous, which in reality is something that may happen quite frequently (even with cloud technology that creates lots of optics on how easy is the world today), migration will always be a challenge, some will look at the latest features and others will look for stability and no change. In that respect ignite 2.0.0 is made to work well with spark 2.1.0 while I have tested spark 2.1.1 and it works well with some adjustment as described above.




## Use cases

