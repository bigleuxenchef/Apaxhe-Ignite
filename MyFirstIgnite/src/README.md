# Apache-Ignite 


## Prerequesites and configuration


spark-env.sh

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


## Use cases

