package cn.edu.nju.se.lawcase.database;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class SparkHelper {
	public static final String LAWCASE = "lawCase";
	public static final String SEGMENT = "segment";
	private static String typeNow = null;
	private static SparkSession spark = null;
	private static JavaSparkContext jsc = null;

	private static JavaSparkContext EnsureJSC(String type) {
		if (jsc == null) {
			typeNow = type;
			spark = SparkSession.builder().master("local").appName("lawcaseStorage")
					.config("spark.mongodb.input.uri", "mongodb://192.168.68.11:20000/"+typeNow+".paragraph")
					.config("spark.mongodb.output.uri", "mongodb://192.168.68.11:20000/"+typeNow+".paragraph").getOrCreate();
			jsc = new JavaSparkContext(spark.sparkContext());
		}
		return jsc;
	}

	public static JavaSparkContext getJSC (String type) {
		if(jsc==null) {
			return EnsureJSC(type);
		}
		if(typeNow!=null&&type.equals(typeNow)) {
			return ChangeJSC(type);
		}
		return jsc;
		
	}
	
	private static JavaSparkContext ChangeJSC(String type) {
		typeNow = type;
		spark = SparkSession.builder().master("local").appName("lawcaseStorage")
				.config("spark.mongodb.input.uri", "mongodb://192.168.68.11:20000/"+typeNow+".paragraph")
				.config("spark.mongodb.output.uri", "mongodb://192.168.68.11:20000/"+typeNow+".paragraph").getOrCreate();
		jsc = new JavaSparkContext(spark.sparkContext());
		return jsc;
	}

	public static void closeJSC() {
		jsc.close();
	}
		
}
