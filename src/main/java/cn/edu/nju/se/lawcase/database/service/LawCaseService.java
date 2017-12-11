package cn.edu.nju.se.lawcase.database.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.bson.Document;

import cn.edu.nju.se.lawcase.database.MongodbHelper;
import cn.edu.nju.se.lawcase.entities.LawCase;
import cn.edu.nju.se.lawcase.util.XmlToLawCase;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;

public class LawCaseService {

	/*
	 * 用于操作案件全文表
	 */

	private static MongoCollection<Document> lawCaseCollection = MongodbHelper.getMongoDataBase()
			.getCollection("lawcase");

	public static JavaRDD<LawCase> sparkWriteFullText(JavaSparkContext jsc , JavaRDD<LawCase> lawcaseRdd) {
		Map<String, String> writeOverrides = new HashMap<String, String>();
		writeOverrides.put("collection", "lawcase");
		writeOverrides.put("writeConcern.w", "majority");
		WriteConfig writeConfig = WriteConfig.create(jsc).withOptions(writeOverrides);
		
		JavaRDD<Document> lawcaseDocs = lawcaseRdd.map(new Function<LawCase, Document>() {
			@Override
			public Document call(LawCase lawCase) throws Exception {
				Document document = new Document("text", lawCase.getFullText());
				document.append("filepath", lawCase.getFilePath());
				return document;
			}
		});
		
		MongoSpark.save(lawcaseDocs, writeConfig);
		List<LawCase> lawcaseList=lawcaseRdd.collect();
		
		Map<String, String> readOverrides = new HashMap<String, String>();
	    readOverrides.put("collection", "lawcase");
	    readOverrides.put("readPreference.name", "secondaryPreferred");
	    ReadConfig readConfig = ReadConfig.create(jsc).withOptions(readOverrides);
	    lawcaseDocs = MongoSpark.load(jsc, readConfig);
	    
		JavaRDD<LawCase> retRdd = lawcaseDocs.map(new Function<Document, LawCase>() {
			@Override
			public LawCase call(Document doc) throws Exception {
				for(LawCase tempCase : lawcaseList) {
					if(tempCase.getFullText().equals(doc.get("text").toString())) {
						tempCase.setFullTextId(doc.get("_id").toString());
						return tempCase;
					}
				}
				return null;
			}
		});
		return retRdd;
	}

	/**
	 * 写入完整的案件文书
	 * 
	 * @param lawCase
	 */
	public static void writeFullText(LawCase lawCase) {
		Document document = new Document("text", lawCase.getFullText());
		document.append("filepath", lawCase.getFilePath());
		lawCaseCollection.insertOne(document);
		lawCase.setFullTextId(document.get("_id").toString());
	}

	/**
	 * 读取所有的案件文书，用于初始化相关索引等内容
	 * 
	 * @return
	 */
	public static FindIterable<Document> findALL() {
		return lawCaseCollection.find();
	}

	public static void writeFullTextMany(List<LawCase> lawCaseList) {
		// TODO Auto-generated method stub
		List<Document> lawcaseDocList = new ArrayList<Document>();
		for (LawCase lawcase : lawCaseList) {
			Document document = new Document("text", lawcase.getFullText());
			document.append("filepath", lawcase.getFilePath());
			lawcaseDocList.add(document);
		}
		lawCaseCollection.insertMany(lawcaseDocList);
		for (int caseindex = 0; caseindex < lawCaseList.size(); caseindex++) {
			lawCaseList.get(caseindex).setFullTextId(lawcaseDocList.get(caseindex).get("_id").toString());
		}
	}
}
