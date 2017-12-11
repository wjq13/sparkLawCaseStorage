package cn.edu.nju.se.lawcase.database;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class SparkHelper {
	private static SparkSession spark = null;
	private static JavaSparkContext jsc = null;

	private static JavaSparkContext EnsureJSC() {
		if (jsc == null) {
			spark = SparkSession.builder().master("local").appName("lawcaseStorage")
					.config("spark.mongodb.input.uri", "mongodb://127.0.0.1/test.myCollection")
					.config("spark.mongodb.output.uri", "mongodb://127.0.0.1/test.myCollection").getOrCreate();
			jsc = new JavaSparkContext(spark.sparkContext());
		}
		return jsc;
	}

	public static JavaSparkContext getJSC () {
		if(jsc==null) {
			return EnsureJSC();
		}
		return jsc;
		
	}
	
	public static void closeJSC() {
		jsc.close();
	}
		
}
