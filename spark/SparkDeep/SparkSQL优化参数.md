Spark SQL优化参数
=============

spark.sql.shuffle.partitions
----------
        ---- 默认值： 200

spark.sql.codegen
------------
        ---- 默认值：
        ----Spark SQL会把每条查询的语句在运行时编译为java的二进制代码

spark.sql.files.maxPartitionBytes
-------
        ---- 默认值： 128M
        ---- 读取文件时单个分区可容纳的最大字节数
spark.sql.files.openCostinBytes
--------
        ---- 默认值： 4M
        ---- 打开文件的估算成本，按照同一时间能够扫描的字节数来测量，当往一个分区写入多个文件时会使用，高估相对较好，这样小文件分区将会比大文件分区速度更快（优先调度）

spark.sql.autoBroadcastJoinThreshold
----------
        ---- 默认值：10M
        ---- 用于配置一个表在执行 join 操作时能够广播给所有 worker 节点的最大字节大小，通地将这个值设置为-1可以禁用广播，
        ---- 注意：当前 数据统计仅支持已经运行了 ANALYZE TABLE <tablename> COMPUTE STATISTICS noscan 命令的 Hive Metastore 表

spark.sql.inMemoryColumnarStorage.batchSize
-------
        ---- 默认值： 10000
        ---- 缓存批处理大小， 较大的批处理可以提高内存利用率和压缩率，但同时也会带来 OOM(Out Of Memory)的风险

spark.sql.inMemoryColumnarStorage.compressed
--------------
        ---- 默认值： true
        ---- Spark SQL 将会基于统计信息自动地为每一列选择一种压缩编码方式
