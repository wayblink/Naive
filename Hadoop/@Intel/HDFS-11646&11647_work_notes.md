HDFS-11646和HDFS-11647是我第一次负责的patch。

JIRA：  
https://issues.apache.org/jira/browse/HDFS-11646  
https://issues.apache.org/jira/browse/HDFS-11647  

patch的主要要求是为Hadoop 3.0的Erasure coding新特性增加两条命令行支持。  
分别是：  
    ls -e 显示路径下文件的ECPolicy
    count -e 显示路径下文件的ECPolicy的大小

它们沿袭自HDFS-10531，HDFS-10531将显示ECPolicy信息的命令放在du命令下，社区讨论认为不妥，因此创建这两个新的JIRA，在ls和count下加入这个功能。

因此，非常幸运，可以重用HDFS-10531中的部分代码。

从这项工作中，我收获以下内容：

1）Hadoop Command Line的代码的基本结构
2）Hadoop RPC的皮毛知识
3）Hadoop Command Line的测试框架
4）相关Git和Jira操作
5）严谨的代码风格

##1，Hadoop Command Line的代码的基本结构

Hadoop Common 下的command line相关代码存放在$HADOOP_HOME/hadoop-common-project/hadoop-common/src/main/java/org/apache/hadoop/fs/shell/  
核心的基类有三个，分别是Command.java,CommandFactory.java,CommandFormat.java

Command.java是Cli的抽象类，每一个命令都对应着一个该类的子类，例如，Ls，Count都是Command类的子类（直接或间接）。  
Command.java定义了一个命令行类的一系列域，包括命令的名字（name），使用（usage）和描述（description），一个CommandFactory域；
和一系列的待实现的方法，如processPath，processOptions，processPaths，processArgument，run等等以及一些getter，setter方法。  

ComandFactory.java是Command的一个工厂类，Command需要被注册到工厂才能被查找使用，CommandFactory持有一个
`Map<String, Class<? extends Command>> classMap`对象来存储它管理的Command。  

CommandFormat.java负责命令行格式和参数，它包含着两个map域来存储有值得参数和无值的参数，minpar，maxpar来存储命令参数的最多最少值。
getOpt，getOptValue，getOpts等管理参数的方法，以及一些参数错误的信息类，如TooManyArgumentsException，NotEnoughArgumentsException等。

一个Command也就基于这几个类来创建完整的功能：  

以Count.java为例：

第一个方法是向CommandFactory注册：
```
  public static void registerCommands(CommandFactory factory) {
    factory.addClass(Count.class, "-count");
  }
```

然后是一串可选参数的声明：
```
  private static final String OPTION_QUOTA = "q";
  private static final String OPTION_HUMAN = "h";
  ......
```

再然后是：NAME，USAGE，DESCRIPTION的具体化

再之后是，一些boolean值，用于判别一个命令是否使用了某个参数。

最后是实际的功能实现，processPath，processOption等等，值得注意的是，对于这个命令类的对象，上面的这些boolean值是通过CommandFormat将输入命令行解析，
后通过getOpt和getOptValue中获取的，然后根据这些具体值，分情况执行功能逻辑。

##2，RPC基础

在做这个工作的涉及到client和namenode之间的通信，Hadoop节点与节点之间的通信都是通过Hadoop自己的RPC实现的。  

RPC(Remote Procedure Control)远程过程调用协议，它是一种通过网络从远程计算机程序上请求服务，而不需要了解底层网络技术的协议。
RPC协议假定某些传输协议的存在，如TCP或UDP，为通信程序之间携带信息数据。在OSI网络通信模型中，RPC跨越了传输层和应用层。
RPC使得开发包括网络分布式多程序在内的应用程序更加容易。

关于RPC的讲解：有几个较好的文章：
http://www.cnblogs.com/edisonchou/p/4285817.html
http://blog.csdn.net/thomas0yang/article/details/41211259

需要注意的是其中内容略有淘汰，其中的getServer方法已经取消，转为builder-build构造模式。

http://standalone.iteye.com/blog/1727544





