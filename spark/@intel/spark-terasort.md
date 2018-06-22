TeraSort
=================

TeraSort is a classical benchmark for computation frameworks.

[spark-terasort](https://github.com/ehiggs/spark-terasort) is a proved tool for implement terasort on spark.


Run on Spark on Yarn
```
spark-submit --master yarn \
--class com.github.ehiggs.spark.terasort.TeraGen \
/path/to/spark-terasort/target/spark-terasort-1.1-SNAPSHOT-jar-with-dependencies.jar \
1g \
hdfs://namenode_server:9000/user/hadoop/teradata/in 
```

```
spark-submit --master yarn \
--class com.github.ehiggs.spark.terasort.TeraSort \
/path/to/spark-terasort/target/spark-terasort-1.1-SNAPSHOT-jar-with-dependencies.jar \ hdfs://namenode_server:9000/user/hadoop/teradata/in \
hdfs://namenode_server:9000/user/hadoop/teradata/out
```

```
spark-submit --master yarn \
--class com.github.ehiggs.spark.terasort.TeraValidate \
/path/to/spark-terasort/target/spark-terasort-1.1-SNAPSHOT-jar-with-dependencies.jar \
hdfs://namenode_server:9000/user/hadoop/teradata/out \
hdfs://namenode_server:9000/user/hadoop/teradata/validate 
```

Run on Spark on Docker on Yarn

```
spark-submit --master yarn \
--class com.github.ehiggs.spark.terasort.TeraGen \
--conf spark.executorEnv.YARN_CONTAINER_RUNTIME_TYPE=docker \
--conf spark.executorEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark \
--conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark \
--conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_TYPE=docker \
/path/to/spark-terasort/target/spark-terasort-1.1-SNAPSHOT-jar-with-dependencies.jar \
1g \
hdfs://namenode_server:9000/user/hadoop/teradata/in
```

```
spark-submit --master yarn \
--class com.github.ehiggs.spark.terasort.TeraSort \
--conf spark.executorEnv.YARN_CONTAINER_RUNTIME_TYPE=docker \
--conf spark.executorEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark \
--conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark \
--conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_TYPE=docker \
/path/to/spark-terasort/target/spark-terasort-1.1-SNAPSHOT-jar-with-dependencies.jar \
hdfs://namenode_server:9000/user/hadoop/teradata/in \
hdfs://namenode_server:9000/user/hadoop/teradata/out
```

```
spark-submit --master yarn \
--class com.github.ehiggs.spark.terasort.TeraValidate \
--conf spark.executorEnv.YARN_CONTAINER_RUNTIME_TYPE=docker \
--conf spark.executorEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark \
--conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark \
--conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_TYPE=docker \
/path/to/spark-terasort/target/spark-terasort-1.1-SNAPSHOT-jar-with-dependencies.jar \
hdfs://namenode_server:9000/user/hadoop/teradata/out \
hdfs://namenode_server:9000/user/hadoop/teradata/validate
```

We can use hdfs command to see the files in cluster:

```
hdfs dfs -ls /user/hadoop/teradata/
```
