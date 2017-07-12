Spark on Yarn配置和问题解决
==========================

软件版本：
-----------------
| Nmae | Version |
|:---- |:---- |
|hadoop|2.7.3|
|Scala|2.13.0|
|Spark|2.1.0|

非常遗憾的发现，尽管是官方文档也可能不靠谱，费了好大劲，终于把Spark on Yarn配置成功了。

遇到的主要的坑是，修改yarn-site.xml配置导致NodeManager没有正常启动，以及Spark-submit时出现ClosedChannelException错误。

NodeManager没有正常启动：
-----------------------

解决方式：

yarn-site.xml的配置仍然按照[Haddop单机配置](https://github.com/wayblink/Naive/blob/master/Hadoop/Hadoop%E5%8D%95%E6%9C%BA%E9%85%8D%E7%BD%AE.md)中的进行

```
<property>
	<name>yarn.nodemanager.aux-services</name>
	<value>mapreduce_shuffle</value>
</property>
<property>
	<name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
	<value>org.apache.hadoop.mapred.ShuffleHandler</value>
</property>
```

在源代码文档中写道：

| Parameter | Value | Notes |
|:---- |:---- |:---- |
| `yarn.nodemanager.aux-services` | mapreduce\_shuffle | Shuffle service that needs to be set for Map Reduce applications. |

[Spark官方文档](http://spark.apache.org/docs/latest/running-on-yarn.html)说需要更改属性。但修改之后NodeManager不能正常启动，原因不明，暂时放弃。

Spark-submit时出现ClosedChannelException错误
--------------------------------------------

在Yarn正常启动后，使用命令
```
spark-submit --class org.apache.spark.examples.SparkPi \
--master yarn \
--deploy-mode client \
--driver-memory 1g \
--executor-memory 1g \
--executor-cores 2 \
$SPARK_HOME/examples/jars/spark-examples_2.11-2.1.0.jar \
10
```
提交spark任务到yarn集群上，出现运算失败，经常报的错误是`ClosedChannelException`。

在[stackoverflow](https://stackoverflow.com/questions/38988941/running-yarn-with-spark-not-working-with-java-8)查找到一个有效的解决方式：

首先更改yarn-env.sh和hadoop-env.sh中所有的HEAP_SIZE或类似的JAVA堆内存上限设置，默认是1000M,将它们提高到机器可以承受的相对较高的值。

其次，在yarn-site.xml添加：
```
<property>
    <name>yarn.nodemanager.pmem-check-enabled</name>
    <value>false</value>
</property>

<property>
    <name>yarn.nodemanager.vmem-check-enabled</name>
    <value>false</value>
</property>
```

其他Spark配置和Hadoop配置：
------------------------

在`$SPARK_HOME/conf/spark-env.sh`中添加以下内容
```
export JAVA_HOME=/path/to/java
export SCALA_HOME=/path/to/scala
export HADOOP_HOME=/path/to/hadoop
export HADOOP_CONF_DIR=$HADOOP_HOME/etc/hadoop
```
其他Spark和Hadoop配置如常。
