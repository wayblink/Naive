Configuring YARN container execution

Yarn Linux Container Executor配置
================================

概述：
-------------------------

Yarn支持两种容器实现方式，一种是yarn容器，一种是Linux容器，Linux容器较比Yarn容器具有更好的扩展性和隔离性。本文将讲述Linux容器的配置。

需要配置的文件有三：

$HADOOP_HOME/etc/hadoop/yarn-site.xml  
$HADOOP_HOME/etc/hadoop/container-executor.cfg  
$HADOOP_HOME/bin/container-executor  

yarn-site.xml
-----------------------

为了运行Linux Container需要增加以下配置：

```
<property>
  <name>yarn.nodemanager.container-executor.class</name>
  <value>org.apache.hadoop.yarn.server.nodemanager.LinuxContainerExecutor</value>
  <description>
    This is the container executor setting that ensures that all applications
    are started with the LinuxContainerExecutor.
  </description>
</property>

<property>
  <name>yarn.nodemanager.linux-container-executor.group</name>
  <value>hadoop</value>
  <description>
    The POSIX group of the NodeManager. It should match the setting in
    "container-executor.cfg". This configuration is required for validating
    the secure access of the container-executor binary.
  </description>
</property>

<property>
  <name>yarn.nodemanager.linux-container-executor.nonsecure-mode.limit-users</name>
  <value>false</value>
  <description>
    Whether all applications should be run as the NodeManager process' owner.
    When false, applications are launched instead as the application owner.
  </description>
</property>
```

分别设置容器类为LinuxContainerExecutor;允许使用的用户组为：hadoop；第三个为可选项，配置是否限制NodeManager用户作为应用用户。

container-executor.cfg
----------------------------

container-executor.cfg是启动container需要使用的配置属性。

需要保证有如下内容：

```
yarn.nodemanager.linux-container-executor.group=hadoop
yarn.nodemanager.local-dirs=$HADOOP_HOME/yarn/local  
yarn.nodemanager.log-dirs=$HADOOP_HOME/yarn/log 
banned.users=hdfs,yarn,mapred,bin 
min.user.id=1000
```
分别是确保容器运行组是hadoop；设置local和log路径，禁用用户；禁用其他系统用户。

相应的需要创建这两个目录：

```
mkdir -p $HADOOP_HOME/yarn/local
mkdir -p $HADOOP_HOME/yarn/log
```

修改后需要修改container-executor.cfg权限：

```
chown root:hadoop $HADOOP_HOME/etc/hadoop/container-executor.cfg

```
container-executor.cfg所有者必须是root:hadoop,如果没有这一用户或用户组，需要创建。

container-executor
---------------------------

container-executor是在$HADOOP_HOME/bin下的可执行文件，是运行Linux Container的最终执行入口。需修改其权限：

```
chown root:hadoop $HADOOP_HOME/bin/container-executor 
chmod 6050 $HADOOP_HOME/bin/container-executor
```

有一种说法是container executor需要重新编译，如果配置完成发现yarn不能正常启动（最常见的是NodeManager没启动），可以尝试重新编译。

推荐的做法是，找到Hadoop源代码，在$HADOOP_SRC/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-nodemanager路径下，有一个pom.xml文件和src目录，直接使用maven编译，执行以下命令：

```
mvn package -Pdist,native -DskipTests -Dtar -Dcontainer-executor.conf.dir=$HADOOP_HOME/etc/hadoop
```
在编译好的target/native/usr/local/路径下可以找到编译好的container-executor,替换即可。

替换之后可以执行$HADOOP_HOME/bin/container-executor -checksetup,如果没有错误信息，基本上问题就解决了。


