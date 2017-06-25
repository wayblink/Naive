Hadoop集群单机配置
=========================

#流程
-------------

1，安装JDK  
2，设置SSH无密码登录  
3，Hadoop的下载安装和环境变量配置  
4，Hadoop配置文件的设置  
5，启动Hadoop  
6，浏览Hadoop Web页面  

PS:所有操作在Linux-Ubuntu 14.04 64bit系统云服务器下完成

#安装JDK
-------------

1，下载JDK的压缩包，复制到要放置的位置 

可以在windows电脑上下载好然后使用WinSCP从Wndows计算机上传到服务器上，或者在Linux上使用wget命令获取
```
wget --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u111-b14/jdk-8u111-linux-x64.tar.gz
```

2 ，解压JDK

我将JDK安装在/usr/java/目录下：
```
cd /usr/
mkdir java
mv ***JDK安装包所在路径***/jdk-8u111-linux-x64.tar.gz /usr/java
cd java/
tar -zxvf jdk-80111-linux-x64.tar.gz
```
/usr目录下会产生jdk-1.8.0.111的文件夹。

我习惯于不要版本号，比较方便,因此对其重命名，这一步可省略
```
mv jdk-1.8.0.111 jdk
```

3 ，设置Java环境变量：

使用VIM编辑环境变量，VIM操作在这里不说了，不会的需要查一下。
```
vim /etc/profile
```
在文件尾加入：

```
JAVA_HOME=jdk文件夹所在位置/jdk1.8.0_101
PATH=$JAVA_HOME/bin:$PATH:.
CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export JAVA_HOME PATH CLASSPATH
```

保存并关闭，在终端输入`source /etc/profile`刷新配置表。 

4，验证JDK安装成功

在终端 输入`Java -version`，如果打印出版本信息，说明JDK安装成功。

#设置SSH免密码登录
-------------

首先说明Linux远程控制使用ssh，命令格式如下：
```
ssh username@hostIP
```
如果不设置免密码设置，输入这条命令后需要输入密码验证。

连接localhost同样需要验证。

###1，尝试ssh连接localhost
`ssh localhost`
如果本机没有ssh，先按提示安装ssh,记得是`sudo apt-get install openssh`

初始情况，ssh连接localhost会要求输入密码，我们需要设置免密码，这一步非常重要，因为Hadoop集群相互之间通信不可能每次都要输入密码，因此要设置彼此间的免密码通信，这样才能发挥出分布式存储计算的优势。

2，设置免密码登录：
```
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys
```
三句指令的意思：
第一句是生成本机的秘钥；
第二步是将生成的秘钥传递到本机秘钥管理的文件夹下，如果是跨主机设置免密码通信，就要将秘钥文件传到目标主机的对应目录下；
第三步，是对该秘钥授权，如果是跨主机则在目标主机进行操作

3，验证
再次键入ssh localhost，如果能够直接进入，说明配置成功。

#Hadoop的下载安装和环境变量配置
-------------

1，下载安装Hadoop
到Hadoop官网http://hadoop.apache.org/选择想用版本的Hadoop安装包(目前的最新稳定版是2.7.3) Ubuntu64bit操作系统选择.tar.gz后缀的压缩包。使用winscp上传到服务器，移动到想要放的位置，我存放在了/opt/目录下，解压缩：

```
tar zxvf hadoop-2.7.3.tar.gz
```
依然，按照我的习惯，去掉版本号：

```
mv hadoop-2.7.3 hadoop
```
就算安装好了

2，环境变量设置：

```
vim /etc/profile
```
文件尾键入
```
export HADOOP_HOME=/opt/hadoop
export PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar
```
键入`source /etc/profile`更新环境变量在任意目录下键入`hadoop`，会出现hadoop命令相关提示，说明设置成功。

#Hadoop配置文件的设置
-------------

我们要修改的文件都在hadoop安装目录下的etc目录下。

1,编辑hadoop-env.sh
```
vim $HADOOP_HOME/etc/hadoop/hadoop-env.sh
```
修改JAVA_HOME，原本文本中是：

```
export JAVE_HOME=${JAVA_HOME}
```
修改为：

```
export JAVA_HOME=/usr/java/jdk
```

2,修改core-site.xml

```
vim $HADOOP_HOME/etc/hadoop/core-site.xml
```
在configuration键值对中添加新内容，结果如下：

```
<configuration>
	<property>
		<name>fs.default.name</name>
		<value>hdfs://localhost:9000</value>
    </property>
</configuration>
```

3,设置yarn-site.xml

```
vim $HADOOP_HOME/etc/hadoop/yarn-site.xml
```
在configuration键值对中添加新内容，结果如下：

```
<configuration>
	<property>
		<name>yarn.nodemanager.aux-services</name>
		<value>mapreduce_shuffle</value>
    </property>
	<property>
		<name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
		<value>org.apache.hadoop.mapred.ShuffleHandler</value>
    </property>
</configuration>
```

3,修改hdfs-site.xml

```
vim $HADOOP_HOME/etc/hadoop/hdfs-site.xml
```
在configuration键值对中添加新内容，结果如下：
```
<configuration>
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

三个属性分别设置Blocks备份的份数，Namenode数据存储目录和DataNode数据存储目录，可以自行修改，目录需要新建

```
mkdir $HADOOP_HOME/hadoop_data/hdfs/namenode
mkdir $HADOOP_HOME/hadoop_data/hdfs/datanode
```

4，编辑mapred-site.xml

这个文件没有现成的，但有一个模板文件mapred-site.xml.template，复制一份

```
cp $HADOOP_HOME/etc/hadoop/mapred-site.xml.template $HADOOP_HOME/etc/hadoop/mapred-site.xml
```

修改如下：

```
<configuration>
	<property>
		<name>mapreduce.framework.name</name>
		<value>yarn</value>
    </property>
</configuration>
```
设置mapreduce的框架是yarn。

四个配置文件分别针对hadoop核心core和hadoop的三大组件：yarn，hdfs和mapreduce

#启动Hadoop
-------------

首先进行格式化：

```
hadoop namenode -format
```

启动hdfs

```
sh $HADOOP_HOME/sbin/start-dfs.sh
```
启动Yarn

```
sh $HADOOP_HOME/sbin/start-yarn.sh
```
或者两个同时启动：

```
sh $HADOOP_HOME/sbin/start-all.sh
```
在终端可以看到一个个组件启动起来。

#Hadoop Web界面
-------------

启动起来Hadoop之后，可以通过网页端浏览管理hadoop

地址是：

Resource Manager
```
http://localhost:8088
```

NameNode:

```
http://localhost:50070
```

细节不表，至此Hadoop单机配置成功完成。


