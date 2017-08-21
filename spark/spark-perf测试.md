测试了spark-perf的spark-test下七个测试，运行成功。
结果显示无docker比有docker速度稍快

测试	有docker运行时间/sec	无docker运行时间/sec
aggregate-by-key	11	8
aggregate-by-key-int	9	10
aggregate-by-key-naive	13	10
sort-by-key	11	8
sort-by-key-int	12	10
count	12	10
count-with-filter	10	8

PS：

命令：

有Docker：

spark-submit --master yarn --class spark.perf.TestRunner --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_TYPE=docker --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_TYPE=docker  /home/spark-perf_2.10-0.1.jar "aggregate-by-key"
spark-submit --master yarn --class spark.perf.TestRunner --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_TYPE=docker --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_TYPE=docker  /home/spark-perf_2.10-0.1.jar "aggregate-by-key-int"
spark-submit --master yarn --class spark.perf.TestRunner --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_TYPE=docker --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_TYPE=docker  /home/spark-perf_2.10-0.1.jar "aggregate-by-key-naive"
spark-submit --master yarn --class spark.perf.TestRunner --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_TYPE=docker --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_TYPE=docker  /home/spark-perf_2.10-0.1.jar "sort-by-key"
spark-submit --master yarn --class spark.perf.TestRunner --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_TYPE=docker --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_TYPE=docker  /home/spark-perf_2.10-0.1.jar "sort-by-key-int"
spark-submit --master yarn --class spark.perf.TestRunner --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_TYPE=docker --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_TYPE=docker  /home/spark-perf_2.10-0.1.jar "count"
spark-submit --master yarn --class spark.perf.TestRunner --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_TYPE=docker --conf spark.executorEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_DOCKER_IMAGE=wayblink/hadoop:spark --conf spark.yarn.AppMasterEnv.YARN_CONTAINER_RUNTIME_TYPE=docker  /home/spark-perf_2.10-0.1.jar "count-with-filter"

无Docker：
spark-submit --master yarn --class spark.perf.TestRunner /home/spark-perf_2.10-0.1.jar "aggregate-by-key"
spark-submit --master yarn --class spark.perf.TestRunner /home/spark-perf_2.10-0.1.jar "aggregate-by-key-int"
spark-submit --master yarn --class spark.perf.TestRunner /home/spark-perf_2.10-0.1.jar "aggregate-by-key-naive"
spark-submit --master yarn --class spark.perf.TestRunner /home/spark-perf_2.10-0.1.jar "sort-by-key"
spark-submit --master yarn --class spark.perf.TestRunner /home/spark-perf_2.10-0.1.jar "sort-by-key-int"
spark-submit --master yarn --class spark.perf.TestRunner /home/spark-perf_2.10-0.1.jar "count"
spark-submit --master yarn --class spark.perf.TestRunner /home/spark-perf_2.10-0.1.jar "count-with-filter"
