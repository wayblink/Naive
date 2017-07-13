HDFS-源码分析-序列化
==================

为了满足通信需求，需要对通信对象进行序列化。

Hadoop使用了一套自己的序列化体系。

org.apache.hadoop.io包中定义了很多可序列化对象。（顺便提及，io包中还包含压缩compress，纠删码erasurecoding，方法重试retry等功能模块）

所有的可序列化对象均实现Writable接口：

Writable接口
-----------------

```
public interface Writable {
  /** 
   * Serialize the fields of this object to <code>out</code>.
   * 
   * @param out <code>DataOuput</code> to serialize this object into.
   * @throws IOException
   */
  void write(DataOutput out) throws IOException;

  /** 
   * Deserialize the fields of this object from <code>in</code>.  
   * 
   * <p>For efficiency, implementations should attempt to re-use storage in the 
   * existing object where possible.</p>
   * 
   * @param in <code>DataInput</code> to deseriablize this object from.
   * @throws IOException
   */
  void readFields(DataInput in) throws IOException;
}
```

DataOutput和DataInput是JDK中的接口，定义了对象和字符串相互转化的读写方法，方法均可抛出IOException。

WritableComparable接口
---------------------

WritableComparable接口继承Writable接口和JDK的Comparable接口，即在Writable接口基础上增加了compareTo(T o)方法。

xxxWritable类
----------------------

XXXWritable指一系列的IntWritable，FloatWritable等类，实现WritableComparable。

所有的基本数据类型和常用数据结构都有一个对应XXXWritable类。

XXXWritable都持有着对应数据类型的私有变量，实现自Writable的write和readFields方法



