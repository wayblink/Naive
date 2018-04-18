import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

/**
 * WordCount example
 */
public class WordCount {

    static final String usage = "spark-submit --class LineCount --master local[2] target/SparkJava-1.0-SNAPSHOT.jar INPUT_FILE_PATH [OUTPUT_FILE_PATH]";

    public static void main(String[] args) {

        if(args.length<1){
            System.out.println(usage);
            return;
        }

        SparkConf sparkConf = new SparkConf().setAppName("Word Count");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        // Read the source file
        JavaRDD<String> input = sparkContext.textFile(args[0]);

        JavaPairRDD<String, Integer> counts = input
                .flatMap(s -> Arrays.asList(s.split(" ")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);

        System.out.println(args[0] + " word num：" + counts.values().collect().get(0));

        if(args.length == 2){
            counts.saveAsTextFile(args[1]);
        }
    }
}