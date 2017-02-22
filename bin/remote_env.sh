#!/usr/bin/env bash
# global settings

master=10.0.2.22
#A list of machines where the spark cluster is running
MC_LIST="10.0.2.21"

[ -z "$HADOOP_HOME" ] && export HADOOP_HOME=/home/skydiscovery/hadoop-2.7.3
# base dir for DataSet
HDFS_URL="hdfs://10.0.2.21:9000"
SPARK_HADOOP_FS_LOCAL_BLOCK_SIZE=536870912

# DATA_HDFS="hdfs://${master}:9000/SparkBench", "file:///home/`whoami`/SparkBench"
DATA_HDFS="hdfs://10.0.2.21:9000/SparkBenchMark"

#Local dataset optional
DATASET_DIR=/disk/sata/sdi/skydiscovery/SparkBenchMark/dataset

SPARK_VERSION=2.0.1
[ -z "$SPARK_HOME" ] && export SPARK_HOME=/home/skydiscovery/SkyDiscovery/share/spark

SPARK_MASTER="spark://${master}:7077"

# Spark config in environment variable or aruments of spark-submit
# - SPARK_SERIALIZER, --conf spark.serializer
# - SPARK_RDD_COMPRESS, --conf spark.rdd.compress
# - SPARK_IO_COMPRESSION_CODEC, --conf spark.io.compression.codec
# - SPARK_DEFAULT_PARALLELISM, --conf spark.default.parallelism
SPARK_SERIALIZER=org.apache.spark.serializer.KryoSerializer
SPARK_RDD_COMPRESS=false
#SPARK_IO_COMPRESSION_CODEC=lzf

# Spark options in system.property or arguments of spark-submit
# - SPARK_EXECUTOR_MEMORY, --conf spark.executor.memory
# - SPARK_STORAGE_MEMORYFRACTION, --conf spark.storage.memoryfraction
# SPARK_STORAGE_MEMORYFRACTION=0.5
SPARK_EXECUTOR_MEMORY=40G


# Storage levels, see http://spark.apache.org/docs/latest/api/java/org/apache/spark/api/java/StorageLevels.html
# - STORAGE_LEVEL, set MEMORY_AND_DISK, MEMORY_AND_DISK_SER, MEMORY_ONLY, MEMORY_ONLY_SER, or DISK_ONLY
STORAGE_LEVEL=MEMORY_AND_DISK

# for data generation
NUM_OF_PARTITIONS=20
# for running
NUM_TRIALS=7

ALLUXIO_MASTER="alluxio://${master}:19998"
DATA_ALLUXIO="${ALLUXIO_MASTER}/SparkBenchMark"
ALLUXIO_FILE_BLOCK_SIZE=64 #16MB
ALLUXIO_FILE_BUFFER_SIZE=4
ALLUXIO_OPT="--conf spark.io.compression.codec=org.apache.spark.compress.LZ4CompressionCodec --conf spark.alluxio.should.compress=true --conf spark.alluxio.fetcher.thread.pool.mode=true --conf spark.alluxio.fetcher.blocking.queue.size=8 --conf spark.alluxio.login.user=skydiscovery --conf spark.alluxio.read.all.by.remote.reader=true --conf spark.alluxio.file.buffer.bytes=${ALLUXIO_FILE_BUFFER_SIZE}MB --conf spark.alluxio.read.without.cache=true --conf spark.alluxio.memory.only=true --conf spark.shuffle.manager=org.apache.spark.shuffle.alluxio.AlluxioShuffleManager --conf spark.alluxio.block.size=${ALLUXIO_FILE_BLOCK_SIZE} --conf spark.alluxio.master.host=${ALLUXIO_MASTER}"

EVENT_LOG_DIR_ON_HDFS="/spark/event"
EVENT_LOG_BACKUP_DIR_ON_HDFS="/spark/backup/event"
REPORT_DIR_ON_HDFS="/SparkBenchMark/report"
EVENT_LOG_LOCAL_DIR_TMP="/disk/sata/sdi/skydiscovery/spark/event/tmp"



BLAS="F2J"
NativeBLASOPT="-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.NativeSystemBLAS -Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.NativeSystemLAPACK -Dcom.github.fommil.netlib.ARPACK=com.github.fommil.netlib.NativeSystemARPACK"
F2jBLASOPT="-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.F2jBLAS -Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.F2jLAPACK -Dcom.github.fommil.netlib.ARPACK=com.github.fommil.netlib.F2jARPACK"

SPARK_EXECUTOR_JVM_OPT="-Xms40g -XX:+UseG1GC -XX:G1HeapRegionSize=4m"
SPARK_DRIVER_JVM_OPT=""
