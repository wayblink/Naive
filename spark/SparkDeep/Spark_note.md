Spark架构和原理
==============

Spark组成：
-----------
		Spark Core
		Spark SQL
		Spark Streaming
		Spark MLib
		Spark GraphX

框架：
-------
	Client——Master——Driver——Worker——Executor
	Application——Job——stage——task

调度：
-----------

	a) Fifo schedular 默认的调度器  先进先出
	b) Capacity schedular  计算能力调度器  选择占用内存小  优先级高的
	c) Fair schedular 公平调度器  所有job 占用相同资源
	优缺点

Spark分布式：
------------
		Local
		Standalone
		Yarn
		Mesos  粗/细粒度

Spark程序运行流程
----------

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

Spark优化：
------------

1）平台层面的调优：防止不必要的jar包分发，提高数据的本地性，选择高效的存储格式如parquet，

2）应用程序层面的调优：过滤操作符的优化降低过多小任务，降低单条记录的资源开销，处理数据倾斜，复用RDD进行缓存，作业并行化执行等等，

3）JVM层面的调优：设置合适的资源量，设置合理的JVM，启用高效的序列化方法如kyro，增大off head内存等等

Spark内存
----------------

Spark中的内存使用分为两部分：执行（execution）与存储（storage）。执行内存主要用于shuffles、joins、sorts和aggregations，存储内存则用于缓存或者跨节点的内部数据传输。1.6之前，对于一个Executor,内存都有哪些部分构成：

1）ExecutionMemory。这片内存区域是为了解决 shuffles,joins, sorts and aggregations 过程中为了避免频繁IO需要的buffer。 通过spark.shuffle.memoryFraction(默认 0.2) 配置。

2）StorageMemory。这片内存区域是为了解决 block cache(就是你显示调用rdd.cache, rdd.persist等方法), 还有就是broadcasts,以及task results的存储。可以通过参数 spark.storage.memoryFraction(默认0.6)。设置

3）OtherMemory。给系统预留的，因为程序本身运行也是需要内存的。 (默认为0.2).

spark.storage.memoryFraction参数的含义,实际生产中如何调优？

用于设置RDD持久化数据在Executor内存中能占的比例，默认是0.6,，默认Executor 60%的内存，可以用来保存持久化的RDD数据。根据你选择的不同的持久化策略，如果内存不够时，可能数据就不会持久化，或者数据会写入磁盘。2）如果持久化操作比较多，可以提高spark.storage.memoryFraction参数，使得更多的持久化数据保存在内存中，提高数据的读取性能，如果shuffle的操作比较多，有很多的数据读写操作到JVM中，那么应该调小一点，节约出更多的内存给JVM，避免过多的JVM gc发生。在web ui中观察如果发现gc时间很长，可以设置spark.storage.memoryFraction更小一点。
