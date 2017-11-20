Spark架构和原理
	Spark组成：
		Spark Core
		Spark SQL
		Spark Streaming
		Spark MLib
		Spark GraphX
	
	框架：
	Client——Master——Driver——Worker——Executor
	Application——Job——stage——task	
	
	调度：
	a) Fifo schedular 默认的调度器  先进先出
	b) Capacity schedular  计算能力调度器  选择占用内存小  优先级高的
	c) Fair schedular 公平调度器  所有job 占用相同资源
	优缺点
	
	SparkHA：
		ZooKeeper

	Spark通信：Akka
	
	Spark文件格式：parquet
		速度快，压缩支持好，IO减少
		本质是列式存储和行式存储的差别
		
	压缩方式
	
	恢复：Zookeeper，FileSystem，NONE
	

	Unified Memory Management内存管理模型的理解？


	
Spark程序运行流程

简答说一下hadoop的map-reduce编程模型？ 

split-map-sort-partition-combine-shuffle-reduce-output

首先map task会从本地文件系统读取数据，转换成key-value形式的键值对集合。 
将键值对集合输入mapper进行业务处理过程，将其转换成需要的key-value在输出。 
之后会进行一个partition分区操作，默认使用的是hashpartitioner，可以通过重写hashpartitioner的getpartition方法来自定义分区规则。 
之后会对key进行进行sort排序，grouping分组操作将相同key的value合并分组输出。 
在这里可以使用自定义的数据类型，重写WritableComparator的Comparator方法来自定义排序规则，重写RawComparator的compara方法来自定义分组规则。 
之后进行一个combiner归约操作，其实就是一个本地段的reduce预处理，以减小后面shufle和reducer的工作量。 
reduce task会通过网络将各个数据收集进行reduce处理，最后将数据保存或者显示，结束整个job。


Client——Master——Driver——Worker——Executor

1）客户端client向ResouceManager提交Application，ResouceManager接受Application
并根据集群资源状况选取一个node来启动Application的任务调度器driver（ApplicationMaster）
2）ResouceManager找到那个node，命令其该node上的nodeManager来启动一个新的
JVM进程运行程序的driver（ApplicationMaster）部分，driver（ApplicationMaster）启动时会首先向ResourceManager注册，说明由自己来负责当前程序的运行
3）driver（ApplicationMaster）开始下载相关jar包等各种资源，基于下载的jar等信息决定向ResourceManager申请具体的资源内容。
4）ResouceManager接受到driver（ApplicationMaster）提出的申请后，会最大化的满足
资源分配请求，并发送资源的元数据信息给driver（ApplicationMaster）；
5）driver（ApplicationMaster）收到发过来的资源元数据信息后会根据元数据信息发指令给具体
机器上的NodeManager，让其启动具体的container。
6）NodeManager收到driver发来的指令，启动container，container启动后必须向driver（ApplicationMaster）注册。
7）driver（ApplicationMaster）收到container的注册，开始进行任务的调度和计算，直到
任务完成。
补充：如果ResourceManager第一次没有能够满足driver（ApplicationMaster）的资源请求
，后续发现有空闲的资源，会主动向driver（ApplicationMaster）发送可用资源的元数据信息
以提供更多的资源用于当前程序的运行。

	程序编写步骤：初始化，资源，数据源，并行化，RDD转化，action输出，持久化。
	
	import org.apache.spark.{SparkContext,SparkConf}
	import org.apache.spark,SparkContext._
	
	val conf = new SparkConf().setAppName(appName).setMaster(master_url)
	val sc = new SparkContext(conf)
	
	task个数：
		1）因为输入数据有很多task，尤其是有很多小文件的时候，有多少个输入block就会有多少个task启动；2）spark中有partition的概念，每个partition都会对应一个task，task越多，在处理大规模数据的时候，就会越有效率。不过task并不是越多越好，如果平时测试，或者数据量没有那么大，则没有必要task数量太多。
		3）参数可以通过spark_home/conf/spark-default.conf配置文件设置:
		spark.sql.shuffle.partitions 50 spark.default.parallelism 10
		第一个是针对spark sql的task数量
		第二个是非spark sql程序设置生效
	
	资源申请和task调度是异步的，因此不需要完全分配资源即可开始运行。如果想等待申请完所有的资源再执行job的：需要将spark.scheduler.maxRegisteredResourcesWaitingTime设置的很大；spark.scheduler.minRegisteredResourcesRatio设置为1，但是应该结合实际考虑否则很容易出现长时间分配不到资源，job一直不能运行的情况。
	

Spark分布式：
	Local
	Standalone
	Yarn
	Mesos  粗/细粒度
	
计算原理：
	Map-Reduce框架：
	
	Spark和MR的区别：
		高层级：都是Map-Reduce框架计算
			mapper——partition——reducer——shuffle/aggregate
		低层级：MR是Sort-based Spark是Hash-based
		实现角度：MR流程比较规范：map，spill，merge，shuffle，sort，reduce
				  可以按照面向过程编程的流程实现；
				  Spark相对灵活，都包含在不同的stage，transformation，action中
				  
		MR：job：map task，reduce task
		Spark：application——jobs——stages——tasks

		
Spark优化：

1）平台层面的调优：防止不必要的jar包分发，提高数据的本地性，选择高效的存储格式如parquet，
2）应用程序层面的调优：过滤操作符的优化降低过多小任务，降低单条记录的资源开销，处理数据倾斜，复用RDD进行缓存，作业并行化执行等等，
3）JVM层面的调优：设置合适的资源量，设置合理的JVM，启用高效的序列化方法如kyro，增大off head内存等等

Spark中的内存使用分为两部分：执行（execution）与存储（storage）。执行内存主要用于shuffles、joins、sorts和aggregations，存储内存则用于缓存或者跨节点的内部数据传输。1.6之前，对于一个Executor,内存都有哪些部分构成：
1）ExecutionMemory。这片内存区域是为了解决 shuffles,joins, sorts and aggregations 过程中为了避免频繁IO需要的buffer。 通过spark.shuffle.memoryFraction(默认 0.2) 配置。
2）StorageMemory。这片内存区域是为了解决 block cache(就是你显示调用rdd.cache, rdd.persist等方法), 还有就是broadcasts,以及task results的存储。可以通过参数 spark.storage.memoryFraction(默认0.6)。设置
3）OtherMemory。给系统预留的，因为程序本身运行也是需要内存的。 (默认为0.2).

spark.storage.memoryFraction参数的含义,实际生产中如何调优？
1）用于设置RDD持久化数据在Executor内存中能占的比例，默认是0.6,，默认Executor 60%的内存，可以用来保存持久化的RDD数据。根据你选择的不同的持久化策略，如果内存不够时，可能数据就不会持久化，或者数据会写入磁盘。2）如果持久化操作比较多，可以提高spark.storage.memoryFraction参数，使得更多的持久化数据保存在内存中，提高数据的读取性能，如果shuffle的操作比较多，有很多的数据读写操作到JVM中，那么应该调小一点，节约出更多的内存给JVM，避免过多的JVM gc发生。在web ui中观察如果发现gc时间很长，可以设置spark.storage.memoryFraction更小一点。


数据partition：
HashPartitioner
RangePartitioner
自定义Partitioner
https://www.iteblog.com/archives/1522.html
http://blog.csdn.net/high2011/article/details/68491115

水塘抽样：
def determineBounds[K: Ordering : ClassTag](
                                               candidates: ArrayBuffer[(K, Float)],
                                               partitions: Int): Array[K] = {
    val ordering = implicitly[Ordering[K]]
    // 按照数据进行数据排序，默认升序排列
    val ordered = candidates.sortBy(_._1)
    // 获取总的样本数量大小
    val numCandidates = ordered.size
    // 计算总的权重大小
    val sumWeights = ordered.map(_._2.toDouble).sum
    // 计算步长
    val step = sumWeights / partitions
    var cumWeight = 0.0
    var target = step
    val bounds = ArrayBuffer.empty[K]
    var i = 0
    var j = 0
    var previousBound = Option.empty[K]
    while ((i < numCandidates) && (j < partitions - 1)) {
      // 获取排序后的第i个数据及权重
      val (key, weight) = ordered(i)
      // 累计权重
      cumWeight += weight
      if (cumWeight >= target) {
        // Skip duplicate values.
        // 权重已经达到一个步长的范围，计算出一个分区id的值
        if (previousBound.isEmpty || ordering.gt(key, previousBound.get)) {
          // 上一个边界值为空，或者当前边界key数据大于上一个边界的值，那么当前key有效，进行计算
          // 添加当前key到边界集合中
          bounds += key
          // 累计target步长界限
          target += step
          // 分区数量加1
          j += 1
          // 上一个边界的值重置为当前边界的值
          previousBound = Some(key)
        }
      }
      i += 1
    }
    // 返回结果
    bounds.toArray
  }


数据倾斜处理：

1）前提是定位数据倾斜，是OOM了，还是任务执行缓慢，看日志，看WebUI
2)解决方法，有多个方面
· 避免不必要的shuffle，如使用广播小表的方式，将reduce-side-join提升为map-side-join
·分拆发生数据倾斜的记录，分成几个部分进行，然后合并join后的结果
·改变并行度，可能并行度太少了，导致个别task数据压力大
·两阶段聚合，先局部聚合，再全局聚合
·自定义paritioner，分散key的分布，使其更加均匀

排序：

二次排序：就是考虑2个维度的排序，key相同的情况下如何排序
http://blog.csdn.net/sundujing/article/details/51399606

TopN问题：
http://www.cnblogs.com/yurunmiao/p/4898672.html

	
Map-side join 和 Reduce-side join
http://blog.csdn.net/lsshlsw/article/details/50834858

Shuffle过程：
http://www.cnblogs.com/jxhd1/p/6528540.html

SparkSQL：

SparkSQL默认使用derby数据库存储元数据，但derby存在使用限制，不推荐

应用：
怎么使用Spark做数据清洗

WordCount代码：
package com.hq

/**
 * User: hadoop
 * Date: 2014/10/10 0010
 * Time: 18:59
 */
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

/**
 * 统计字符出现次数
 */
object WordCount {
  def main(args: Array[String]) {
    if (args.length < 1) {
      System.err.println("Usage: <file>")
      System.exit(1)
    }

    val conf = new SparkConf()
    val sc = new SparkContext(conf)
    val line = sc.textFile(args(0))

    line.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_+_).collect().foreach(println)

    sc.stop()
  }
}

yarn application -kill applicationId

spark写数据流程

spark sql又为什么比hive快呢？

BlockManager怎么管理硬盘和内存的？
