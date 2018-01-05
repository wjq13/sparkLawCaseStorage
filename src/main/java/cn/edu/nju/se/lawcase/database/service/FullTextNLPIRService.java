package cn.edu.nju.se.lawcase.database.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.WriteConfig;

import cn.edu.nju.se.lawcase.database.MongodbHelper;
import cn.edu.nju.se.lawcase.util.Segment;

public class FullTextNLPIRService {
	private static MongoCollection<Document> NLPIRCollection = MongodbHelper.getMongoDataBase("segment")
			.getCollection("NLPIR");
	
	public static void writeSegmentations(JavaRDD<Document> lawcases,JavaSparkContext jsc) {
		Map<String, String> writeOverrides = new HashMap<String, String>();
		writeOverrides.put("database","segment");
		writeOverrides.put("collection", "NLPIR");
		writeOverrides.put("writeConcern.w", "majority");
		WriteConfig writeConfig = WriteConfig.create(jsc).withOptions(writeOverrides);
		
		JavaRDD<Document> NLPIRDocs = lawcases.map(new Function<Document, Document>() {
			@Override
			public Document call(Document doc) throws Exception {
				String text = doc.get("text").toString();
				String id = doc.get("_id").toString();
				System.out.println(id);
				String[] segmentations = Segment.getSegmentation(text);
				Document document = new Document("fullTextId", id);
				document.append("words", segmentations[0]);
				document.append("wordpostags", segmentations[1]);
				document.append("keywords", segmentations[2]);
				document.append("keywordpostags", segmentations[3]);
				return document;
			}
		});
		System.out.println("before save");
		MongoSpark.save(NLPIRDocs, writeConfig);
	}
}
