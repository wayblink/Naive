Hadoop集群配置
================

#准备
------------

众所周知Hadoop是分布式存储和计算框架，单机配置的Hadoop不叫Hadoop，集群配置的Hadoop才是真正的Hadoop。

所谓集群就是不能少于三台主机，在正式配置之前首先应该明确我想要配置多大的集群，每个主机用来做什么。一般来说最开始都是使用一台master，三台slave的集群，master作为NameNode和ResourceManager，slaves作为DataNode和NodeManager。

鉴于我是使用云服务器做实验，过多的服务器会造成浪费，所以我决定构建1Master2Slaves的集群。

可以画一张表或者拓扑图，将各个主机的名称（Master/Slave），IP，承担职责，表示出来。

#集群免密码登录设置
------------------

1，hosts修改
因为之后要在Linux服务器之间传递数据和远程操控，所以首先先将所有服务器的地址记录在本地hosts里。

登录一台服务器`vim /etc/hosts`,向其中添加形如下所示的文本

```
xxx.xxx.xxx.xxx  master
xxx.xxx.xxx.xxx  node1
xxx.xxx.xxx.xxx  node2
```

xxx.xxx.xxx.xxx代表服务器的公网ip，给它们起上别名master，node1和node2，之后就可以使用别名进行访问了。

分别在所有服务器上都做以上修改。

2，配置免密码登录

如[上一篇博客： Hadoop单机配置](https://github.com/wayblink/Naive/blob/master/Hadoop%E5%8D%95%E6%9C%BA%E9%85%8D%E7%BD%AE.md)所述，需要进行免密码登录配置，集群中的服务器，两两间通信都需要免密码。

首先在所有服务器上执行`ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa`产生id_rsa.pub文件，将这个文件追加到本机的信任目录中：`cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys`，然后对信任目录附以权限：`chmod 0600 ~/.ssh/authorized_keys`

之后使用scp命令将id_rsa.pub文件传输到集群中的其他服务器上：

**scp命令的格式是：**

```
scp [-r] file username@hostIP:存储路径
```
    [-r]:是可选值，如果传输的是文件夹，则需要-r属性传递整个文件夹
    file：文件名
    username：和ssh命令一样是目标主机用户
    hostIP：目标主机IP
    存储路径：文件要存放到目标主机的位置

scp命令和ssh命令一样需要密码验证。

例如，我要将node1的id_rsa.pub传输到master服务器，因为我们之前配置过hosts表，所以可以这样传输：
```
scp /root/.ssh/id_rsa.pub root@master:/home
```
然后登陆master服务器或者通过ssh连接master服务器，将id_rsa.pub加入到信任目录中，即：

```
cat /home/id_rsa.pub >> /root/.ssh/authorized_keys
```
这时node1就可以无密码访问master了，scp和ssh命令都不再需要密码验证。如此将集群中服务器都相互配置，就完成了。

在实际操作中，这是一个比较繁琐又容易出错的工作，相对简单的方式是将所有的id_rsa.pub先集中到一个服务器上，配置好这个服务器的authorized_keys，然后将authorized_keys复制到所有其他服务器上，取代原先的文件。

#单机配置和文件复制
---------------------

集群需要多次的重复配置，为了减少重复劳动，可以先在Master主机上将主要配置按[单机配置](https://github.com/wayblink/Naive/blob/master/Hadoop%E5%8D%95%E6%9C%BA%E9%85%8D%E7%BD%AE.md)都配好，然后使用scp命令将包括jdk，hadoop文件夹传输到slaves服务器。

#Master节点配置
----------------------

Master节点和Slave节点的配置略有不同，主要体现在HDFS和YARN的配置上，在这里我们将HDFS的NameNode和YARN的ResourceManager放在了同一节点，实际上可以分开，而且在真是工程建议分开。

**0，修改hadoop-env.sh**

添加：

```
export JAVA_HOME=/usr/java/jdk
```

**1，修改core-site.xml**

```
<configuration>
    <property>
        <name>fs.default.name</name>
        <value>hdfs://master:9000</value>
    </property>
    <property>
        <name>io.file.buffer.size</name>
        <value>131072</value>
    </property>
    <property>
        <name>hadoop.tmp.dir</name>
        <value>file:/opt/hadoop/tmp</value>
        <description>Abasefor other temporary directories.</description>
    </property>
</configuration>
```

**2，修改hdfs-site.xml**

因为是Slave节点，所以删除和NameNode有关配置，配置结果如下：

```
<configuration>
    <property>
         <name>dfs.namenode.secondary.http-address</name>
         <value>master:9001</value>
    </property>

    <property>
         <name>dfs.webhdfs.enabled</name>
        <value>true</value>
    </property>

    <property>
        <name>dfs.replication</name>
        <value>3</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:/opt/hadoop/hadoop_data/hdfs/namenode</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:/opt/hadoop/hadoop_data/hdfs/datanode</value>
    </property>

</configuration>
```

这里设置了临时文件存储目录为/opt/hadoop/tmp 因此要创建这个目录

```
mkdir /opt/hadoop/tmp
```

**3，修改yarn-site.xml**

```
<configuration>
	<property>
	    <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.manager.aux-services.mapreduce.shuffle.class</name>
        <value>org.apache.hadoop.mapred.ShuffleHandler</value>
    </property>

    <property>
	    <name>yarn.resourcemanager.address</name>
	    <value>master:8032</value>
    </property>
    <property>
	    <name>yarn.resourcemanager.scheduler.address</name>
	    <value>master:8030</value>
    </property>
    <property>
	    <name>yarn.resourcemanager.resource-tracker.address</name>
	    <value>master:8035</value>
    </property>
    <property>
	    <name>yarn.resourcemanager.admin.address</name>
	    <value>master:8033</value>
    </property>
    <property>
	    <name>yarn.resourcemanager.webapp.address</name>
	    <value>master:8088</value>
    </property>
</configuration>


```

**4，修改mapred-site.xml**

修改如下
```
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
         <name>mapreduce.jobhistory.address</name>
         <value>master:10020</value>
    </property>
    <property>
        <name>mapreduce.jobhistory.webapp.address</name>
        <value>master:19888</value>
    </property>
</configuration>
```
**5，修改masters和slaves文件**

在$HADOOP_HOME/etc/hadoop/文件夹下创建masters文件，键入`master`;
在$HADOOP_HOME/etc/hadoop/文件夹下创建slaves文件，隔行键入

```
node1
node2
node3
```

PS：新的3.0.0版本貌似改为了workers


#Slaves节点配置
--------------------

Slaves节点和Master节点配置可以基本相同，在HDFS上将namenode改成datanode即可

**修改hdfs-site.xml**

因为是Slave节点，所以删除和NameNode有关配置，配置结果如下：

```
<configuration>
    <property>
         <name>dfs.namenode.secondary.http-address</name>
         <value>master:9001</value>
    </property>

    <property>
         <name>dfs.webhdfs.enabled</name>
        <value>true</value>
    </property>

    <property>
        <name>dfs.replication</name>
        <value>3</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:/opt/hadoop/hadoop_data/hdfs/datanode</value>
    </property>
</configuration>
```

#启动集群
-------------------------------

**1，最后的准备工作**
至此配置基本完成，在启动之前，现将各个节点的namenode和datanode的存储文件夹清空或删除（master删掉datanode，slave删掉namenode）。

然后使用jps命令查看java进程，将在调试过程中启动的hadoop相关的进程全部杀掉。

PS：jps会列出进程的PID（进程号）和进程名，杀死进程命令是`kill 9 PID`

**2，启动**
准备工作做好之后，还在Master节点，执行

```
sh $HADOOP_HOME/sbin/start-all.sh
```

**3，验证**
在所有节点执行jps命令看进程有没有如我们想象的一样启动，然后访问master:8080和master:50070来查看集群状态。如图：

![这里写图片描述](http://img.blog.csdn.net/20170416233055354?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcGljd2F5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

![这里写图片描述](http://img.blog.csdn.net/20170416233103870?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvcGljd2F5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

至此，集群配置告一段落。
