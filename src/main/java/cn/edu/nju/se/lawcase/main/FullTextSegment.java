package cn.edu.nju.se.lawcase.main;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.bson.Document;

import cn.edu.nju.se.lawcase.database.SparkHelper2;
import cn.edu.nju.se.lawcase.database.service.FullTextNLPIRService;
import cn.edu.nju.se.lawcase.database.service.LawCaseService;
import cn.edu.nju.se.lawcase.util.Segment;

public class FullTextSegment {
	
	public static void main(String args[]) {
		Segment.init();
		SparkHelper2 shelper = new SparkHelper2();
		JavaSparkContext lawcasejsc = shelper.getJSC(SparkHelper2.LAWCASE);
		//JavaSparkContext segmentjsc = shelper.getJSC(SparkHelper2.SEGMENT);
		JavaRDD<Document> lawcases = LawCaseService.sparkFind(lawcasejsc,100,1000);
		System.out.println("find all");
		FullTextNLPIRService.writeSegmentations(lawcases, lawcasejsc);
	}
}
