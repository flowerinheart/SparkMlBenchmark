master="127.0.0.1"


#[ -z "$HADOOP_HOME" ] &&     export HADOOP_HOME=/YOUR/HADOOP
# base dir for DataSet
#HDFS_URL="hdfs://${master}:9000"
#SPARK_HADOOP_FS_LOCAL_BLOCK_SIZE=536870912
# DATA_HDFS="file:///home/whoami/SparkBench" SPARK_MASTER=local[2] MC_List=""
#DATA_HDFS="file:///tmp"
#Local dataset optional
#DATASET_DIR=/home/darnell/spark-benchmark/dataset



SPARK_VERSION=2.0.2  #1.5.1
[ -z "$SPARK_HOME" ] &&     export SPARK_HOME=$HOME/spark-2.0.1-bin-hadoop2.7



#[ -z "$HADOOP_HOME" ] &&     export HADOOP_HOME=$HOME/hadoop-2.7.3
#SPARK_MASTER=local
#SPARK_MASTER=local[K]
#SPARK_MASTER=local[*]
#SPARK_MASTER=spark://HOST:PORT
##SPARK_MASTER=mesos://HOST:PORT
##SPARK_MASTER=yarn-client
##SPARK_MASTER=yarn-cluster
SPARK_MASTER=local[2]



# Spark config in environment variable or aruments of spark-submit 
# - SPARK_SERIALIZER, --conf spark.serializer
# - SPARK_RDD_COMPRESS, --conf spark.rdd.compress
# - SPARK_IO_COMPRESSION_CODEC, --conf spark.io.compression.codec
# - SPARK_DEFAULT_PARALLELISM, --conf spark.default.parallelism
SPARK_SERIALIZER=org.apache.spark.serializer.KryoSerializer
SPARK_RDD_COMPRESS=false
SPARK_IO_COMPRESSION_CODEC=lzf

# Spark options in system.property or arguments of spark-submit 
# - SPARK_EXECUTOR_MEMORY, --conf spark.executor.memory
# - SPARK_STORAGE_MEMORYFRACTION, --conf spark.storage.memoryfraction
SPARK_STORAGE_MEMORYFRACTION=0.5
SPARK_EXECUTOR_MEMORY=5g
#export MEM_FRACTION_GLOBAL=0.005DOOP_HOME" ] && export HADOOP_HOME=/home/skydiscovery/hadoop-2.7.3

# Spark options in YARN client mode
# - SPARK_DRIVER_MEMORY, --driver-memory
# - SPARK_EXECUTOR_INSTANCES, --num-executors
# - SPARK_EXECUTOR_CORES, --executor-cores
# - SPARK_DRIVER_MEMORY, --driver-memory
export EXECUTOR_GLOBAL_MEM=6g
export executor_cores=1

# Storage levels, see http://spark.apache.org/docs/latest/api/java/org/apache/spark/api/java/StorageLevels.html
# - STORAGE_LEVEL, set MEMORY_AND_DISK, MEMORY_AND_DISK_SER, MEMORY_ONLY, MEMORY_ONLY_SER, or DISK_ONLY
STORAGE_LEVEL=MEMORY_AND_DISK

# for data generation
NUM_OF_PARTITIONS=10
# for running
NUM_TRIALS=1
SPARK_EXECUTOR_MEMORY=20G




NativeBLASOPT="-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.NativeSystemBLASWrapper -Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.NativeSystemLAPACKWrapper -Dcom.github.fommil.netlib.ARPACK=com.github.fommil.netlib.NativeSystemARPACKWrapper"
#NativeBLASOPT="-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.NativeSystemBLAS -Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.NativeSystemLAPACK -Dcom.github.fommil.netlib.ARPACK=com.github.fomil.netlib.NativeSystemARPACK"
F2jBLASOPT="-Dcom.github.fommil.netlib.BLAS=com.github.fommil.netlib.F2jSystemBLAS -Dcom.github.fommil.netlib.LAPACK=com.github.fommil.netlib.F2jSystemLAPACK -Dcom.github.fommil.netlib.ARPACK=com.github.fommil.netlib.F2jSystemARPACK"



EVENT_LOG_DIR_ON_HDFS="/spark/event"
EVENT_LOG_BACKUP_DIR_ON_HDFS="/spark/backup/event"
REPORT_DIR_ON_HDFS="/SparkBenchMark/report"
EVENT_LOG_LOCAL_DIR_TMP="/disk/sata/sdi/skydiscovery/spark/event/tmp"

SPARK_EXECUTOR_JVM_OPT=""
SPARK_DRIVER_JVM_OPT=""
SPARK_OPT=""



