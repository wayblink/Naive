/**
 * MR统计一个文件的行数
 * @author Wan Kaiming on 2016/9/9
 * @Modify Wang Anyang on 2018/4/17
 * @version 1.0
 */
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
public class LineCount {


    //Mapper主要就是每行形成一个分片，key是一行的内容，value是值1
    public static class LineMapper
            extends Mapper<Object, Text, Text, IntWritable>{

        //统计行数
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text("总行数为：");

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {

            //由于我们使用TextInputFormar，其中Key是每个数据记录在数据分片中字节偏移量 Text类型value存储的是一行的内容

            //传递给reducer的key全部固定为一个值，value就是值1,代表一行。这样reducer可以全部在一个key里面求和
            context.write(word, one);

        }
    }


    //reducer对行数进行统计求和,注意输出的key为null，我们只要计算总数即可
    public static class IntSumReducer
            extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        //使用默认配置创建一个Job实例
        Job job = Job.getInstance(conf, "line count");

        job.setJarByClass(LineCount.class);

        //设置mapper,combiner和reducer
        job.setMapperClass(LineMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        //设置输出的key,value类,由于我们不需要输出key的内容，就使用NullWritable类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        //设置参数
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}