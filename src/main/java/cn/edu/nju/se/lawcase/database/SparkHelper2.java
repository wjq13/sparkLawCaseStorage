package cn.edu.nju.se.lawcase.database;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class SparkHelper2 {
	public static final String LAWCASE = "lawCase";
	public static final String SEGMENT = "segment";
	
	public JavaSparkContext getJSC (String type) {
//		SparkSession spark = SparkSession.builder().master("local").appName("lawcaseStorage")
//				.config("spark.mongodb.input.uri", "mongodb://127.0.0.1:27017/"+type+".paragraph")
//				.config("spark.mongodb.output.uri", "mongodb://127.0.0.1:27017/"+type+".paragraph").getOrCreate();
		SparkSession spark = SparkSession.builder().master("spark://192.168.68.11:7077").appName("lawcaseStorage")
				.config("spark.driver.host","192.168.68.11")
				.config("spark.mongodb.kepp_alive_ms","100000")
				.config("spark.mongodb.input.partitioner","MongoShardedPartitioner")
				.config("spark.mongodb.input.uri", "mongodb://192.168.68.11:20000/"+type+".myCollection")
				.config("spark.mongodb.output.uri", "mongodb://192.168.68.11:20000/"+type+".myCollection").getOrCreate();
		JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
		return jsc;
	}
}
