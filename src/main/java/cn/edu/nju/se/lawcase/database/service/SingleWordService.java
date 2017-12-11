package cn.edu.nju.se.lawcase.database.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.bson.Document;

import cn.edu.nju.se.lawcase.database.MongodbHelper;
import cn.edu.nju.se.lawcase.entities.LawCase;
import cn.edu.nju.se.lawcase.entities.SingleWord;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;

public class SingleWordService {

	private static MongoCollection<Document> singlewordCollection = MongodbHelper.getMongoDataBase()
			.getCollection("singleword");

	public static void writeSingleWord(SingleWord singleWord) {
		Document singlewordDoc = new Document("word", singleWord.getWord());
		singlewordDoc.append("doccount", singleWord.getDocCount() + "");

		List<Document> docWithCounts = new ArrayList<Document>();
		Document docWithCount = new Document();
		docWithCount.append("docid", singleWord.getCurrentDocID());
		docWithCount.append("count", singleWord.getCurrentDocCount() + "");
		docWithCounts.add(docWithCount);

		singlewordDoc.append("docwithcount", docWithCounts);

		singlewordCollection.insertOne(singlewordDoc);
	}

	public static void updateSingleWord(SingleWord singleWord) {
		Document existWordDoc = singlewordCollection.find(
				Filters.eq("word", singleWord.getWord())).first();
		if (existWordDoc == null) {
			SingleWordService.writeSingleWord(singleWord);
		} else {
			int docCount = Integer.parseInt((String) existWordDoc.get("doccount")) + 1;
			//singlewordCollection.updateOne(Filters.eq("word", singleWord.getWord()), Updates.set("doccount", docCount + ""));
			Document docWithCount = new Document();
			docWithCount.append("docid", singleWord.getCurrentDocID());
			docWithCount.append("count", singleWord.getCurrentDocCount() + "");

			singlewordCollection.updateOne(
					Filters.eq("word", singleWord.getWord()),
					Updates.combine(Updates.set("doccount", docCount + ""),
							Updates.addToSet("docwithcount", docWithCount)));
		}
	}

	@SuppressWarnings("unchecked")
	public static SingleWord findByWord(String word) {
		Document existWordDoc = singlewordCollection.find(Filters.eq("word", word)).first();
		if (existWordDoc == null) {
			return null;
		} else {
			SingleWord singleWord = new SingleWord();
			singleWord.setWord(word);
			singleWord.setDocCount(Integer.parseInt((String) existWordDoc.get("doccount")));

			Map<String, Integer> docWithCounts = new HashMap<String, Integer>();
			List<Document> docWithCountDocs = (List<Document>) existWordDoc.get("docwithcount");
			for (Document docWithCountDoc : docWithCountDocs) {
				docWithCounts.put(docWithCountDoc.getString("docid"),
						Integer.parseInt((String) docWithCountDoc.get("count")));
			}
			singleWord.setDocWithCount(docWithCounts);

			return singleWord;
		}
	}

	public static FindIterable<Document> findALL() {
		return singlewordCollection.find();
	}

	public static int getDocCountByWord(String word) {
		Document existWordDoc = singlewordCollection.find(Filters.eq("word", word)).first();
		if (existWordDoc == null) {
			return 0;
		} else {
			return Integer.parseInt((String) existWordDoc.get("doccount"));
		}
	}

	public static void updateSingleWords(Map<String, SingleWord> wordCount) {
		for(String word:wordCount.keySet()){
			SingleWordService.writeSingleWord(wordCount.get(word));
		}
		
	}

	public static Map<String, Integer> findALLWordCount() {
		Map<String, Integer> ret = new HashMap<>();
		FindIterable<Document> docs = findALL();
		MongoCursor<Document> cursor = docs.iterator();
		while(cursor.hasNext()){
			Document doc = cursor.next();
			ret.put(doc.getString("word"), Integer.parseInt(doc.get("doccount").toString()));
		}
		return ret;
	}
	
	public static Map<String, Integer> sparkFindALLWordCount(JavaSparkContext jsc) {
		Map<String, Integer> ret = new HashMap<>();
		JavaRDD<Document> docs = sparkFindALL(jsc);
		docs.map(new Function<Document, Document>() {
			@Override
			public Document call(Document singleWord) throws Exception {
				ret.put(singleWord.getString("word"), Integer.parseInt(singleWord.get("doccount").toString()));
				return singleWord;
			}
		});
		return ret;
	}

	public static JavaRDD<Document> sparkFindALL(JavaSparkContext jsc) {
		Map<String, String> readOverrides = new HashMap<String, String>();
	    readOverrides.put("collection", "singleword");
	    readOverrides.put("readPreference.name", "secondaryPreferred");
	    ReadConfig readConfig = ReadConfig.create(jsc).withOptions(readOverrides);
	    JavaRDD<Document> singlewordDocs = MongoSpark.load(jsc, readConfig);
		return singlewordDocs;
	}

	public static void sparkUpdateSingleWords(JavaSparkContext jsc, Map<String, SingleWord> wordCount) {
		Map<String, String> writeOverrides = new HashMap<String, String>();
		writeOverrides.put("collection", "singleword");
		writeOverrides.put("writeConcern.w", "majority");
		WriteConfig writeConfig = WriteConfig.create(jsc).withOptions(writeOverrides);
		
		JavaRDD<SingleWord> singleWordRDD = jsc.parallelize((List<SingleWord>) wordCount.values());
		JavaRDD<Document> singleWordDocs = singleWordRDD.map(new Function<SingleWord, Document>() {
			@Override
			public Document call(SingleWord singleWord) throws Exception {
				Document singlewordDoc = new Document("word", singleWord.getWord());
				singlewordDoc.append("doccount", singleWord.getDocCount() + "");

				List<Document> docWithCounts = new ArrayList<Document>();
				Document docWithCount = new Document();
				docWithCount.append("docid", singleWord.getCurrentDocID());
				docWithCount.append("count", singleWord.getCurrentDocCount() + "");
				docWithCounts.add(docWithCount);

				singlewordDoc.append("docwithcount", docWithCounts);
				return singlewordDoc;
			}
		});
		MongoSpark.save(singleWordDocs, writeConfig);
		
		
	}
}
