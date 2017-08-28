HDFS-11975 

HDFS-11975是需要给HDFS Erasurecoding设置一个系统默认值，在setPolicy命令不指定policy时使用默认policy。

实现的基本思路是增加一个Configuration项（增加配置项需要在对应的配置项java类加一个条目，再在xml中增加一个条目）；
在setPolicy的执行中增加对policyName的判断逻辑，根据ecPolicyName传入值和读取配置值，做相应处理。

调用栈分析

ECAdmin.java:/** CLI for the erasure code encoding operations.*/
  -private class SetECPolicyCommand
    -public int run(Configuration conf, List<String> args) throws IOException
      -dfs.setErasureCodingPolicy(p, ecPolicyName);
        |
       DistributedFileSystem.java
    
DistributedFileSystem.java
/**
 * Implementation of the abstract FileSystem for the DFS system.
 * This object is the way end-user code interacts with a Hadoop
 * DistributedFileSystem.
 */
   -public void setErasureCodingPolicy(final Path path,final String ecPolicyName) throws IOException 
     -dfs.setErasureCodingPolicy(getPathName(p), ecPolicyName);
       |
      DFSClient.java
      
DFSClient.java
/**
 * DFSClient can connect to a Hadoop Filesystem and
 * perform basic file tasks.  It uses the ClientProtocol
 * to communicate with a NameNode daemon, and connects
 * directly to DataNodes to read/write block data.
 *
 * Hadoop DFS users should obtain an instance of
 * DistributedFileSystem, which uses DFSClient to handle
 * filesystem tasks.
 */
  -public void setErasureCodingPolicy(String src, String ecPolicyName)
    --namenode.setErasureCodingPolicy(src, ecPolicyName);
       |
      ClientProtocol.java
      
ClientProtocol.java
/**
 * ClientProtocol is used by user code via the DistributedFileSystem class to
 * communicate with the NameNode.  User code can manipulate the directory
 * namespace, as well as open/close file streams, etc.
 */
  -void setErasureCodingPolicy(String src, String ecPolicyName) throws IOException;
  |
  |
  RPC
  |
  |
NameNodeRpcServer.java
  -public void setErasureCodingPolicy(String src, String ecPolicyName) throws IOException


最初的实现，我直接ECAdmin层进行逻辑判断，仔细想过发现不妥，于是扒它的调用栈，发现它通过ClientProtocol，使用RPC通信交给NameNode来做处理。
因此把判断逻辑交给NameNodeRPCServer是合理的做法。

在这个过程中：

1，加深了对RPC的了解
2，熟悉了Client-Server端的代码结构
3，一些关于protobuf和configuration的细节

protobuf的required元素不能为空，在我要传递空的ecPolicyName时出现问题。  

因此，在初次解决时，在RPC前后分别加一个判断：client端判断如果为空，则给它附一个特殊的值；server端判断如果是这个特殊值则说明其实是空，按空来处理。
但这样的解决方式有些不优雅，最后的解决是将需要穿孔的元素由required改为optional，然后在RPC调用两端的辅助函数中增加对空值的判断。

configuration不是个简单的entity，是包含了文件系统的类，初始化new Configuration()可以直接加载到配置信息内容；
因此只需要在需要时初始化，然后conf.get（）获取属性值即可。

关于配置项相关的代码，比较重要的及各类是Configuration，Configured和Configurable。

Configuration如上所述是一个包含文件协同和IO，序列化操作的工具类，还有一些对配置项操作的方法，而具体的配置项，由继承它的子类来定义。
如HDFSConfiguration就继承于它并定义了和HDFS相关的系列属性。

Configured是一个超类，Configured代码非常简单，它只有一个Configuration私有变量，两个有参和无参的构造器，以及实现自Configurable的方法。

Configurable是个接口，仅定义了两个方法   
```
@Override
  public void setConf(Configuration conf) {
    this.conf = conf;
  }

  // inherit javadoc
  @Override
  public Configuration getConf() {
    return conf;
  }
```

Hadoop的配置项都存储在xml文件中，因此是通过反序列化+键值对操作实现的。

对于有值的configuration，一般会设置一个配置项，一个带默认值的配置项，带默认值的配置项应以_DEFAULT结尾；
有测试检测配置项和配置文件是否匹配，以_DEFAULT结尾的配置项不需要在xml中配置，测试方法会根据_DEFAULT尾自动识别。
 
      
 


