import org.apache.hadoop.fs.Path;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.sql.*;

public class HiveJdbcClient {

    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    public static void main(String[] args) throws SQLException {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        Connection con = DriverManager.getConnection("jdbc:hive2://localhost:10000/default","","");
        Statement stmt = con.createStatement();
        String tableName = "testHiveDriverTable";
//        stmt.executeQuery("drop table if exists " + tableName);

        String sql = "create table if not exists " + tableName + " (key int, value string) row format delimited fields terminated by ','";
        stmt.execute(sql);
        //show tables
        sql = "show tables '" + tableName + "'";
        System.out.println("Running: " + sql);
        ResultSet res = stmt.executeQuery(sql);
        if(res.next()){
            System.out.println(res.getString(1));
        }
        //describe table
        sql = "describe " + tableName;
        System.out.println("Running: " + sql);
        res = stmt.executeQuery(sql);
        while(res.next()){
            System.out.println(res.getString(1) + "\t" + res.getString(2));
        }

        //load data into table
        //Note: filePath has to be local to hive server
        String filePath = System.getProperty("user.dir") + "/a.txt";
        sql = "load data local inpath '" + filePath + "' into table " + tableName;
        System.out.println("Running: " + sql);
        stmt.execute(sql);

        //select * query
        sql = "select * from " + tableName;
        System.out.println("Running: " + sql);
        res = stmt.executeQuery(sql);
        while(res.next()){
            System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
        }

        //regular hive query
        sql = "select count(1) from " + tableName;
        System.out.println("Running: " + sql);
        res = stmt.executeQuery(sql);
        while(res.next()){
            System.out.println(res.getString(1));
        }

        sql = "drop table if exists " + tableName;
        System.out.println("Running: " + sql);
        stmt.execute(sql);

    }
}
