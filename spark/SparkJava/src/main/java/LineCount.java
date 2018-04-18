import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Line Count example
 */
public class LineCount {

    static final String usage = "spark-submit --class LineCount --master local[2] target/SparkJava-1.0-SNAPSHOT.jar INPUT_FILE_PATH";

    public static void main(String[] args) {
        if(args.length<1){
            System.out.println(usage);
            return;
        }

        SparkConf sparkConf = new SparkConf().setAppName("Line Count");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);

        // Read the source file
        JavaRDD<String> input = javaSparkContext.textFile(args[0]);

        // Gets the number of entries in the RDD
        long count = input.count();

        System.out.println(String.format("%s lines num: %d",args[0],count));
        
    }

}