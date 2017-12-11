package cn.edu.nju.se.lawcase.database.service;

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
import cn.edu.nju.se.lawcase.entities.LawCaseWords;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;

public class LawCaseWordsService {

	/*
	 * 用于操作案件中相关段落词语
	 */
	
	private static MongoCollection<Document> lawcasewordsCollection = MongodbHelper.getMongoDataBase()
			.getCollection("lawcasewords");

	public static void writeLawCaseWords(LawCaseWords lawcaseWords) {
		Document lawcaseWordsDoc = new Document("lawcaseid", lawcaseWords.getLawcaseID());
		lawcaseWordsDoc.append("allwords", lawcaseWords.getAllWords());

		List<Document> wordsList = new ArrayList<Document>();
		int sumWordCount = 0;
		for (String key : lawcaseWords.getWordCountMap().keySet()) {
			Document tmpWord = new Document("word", key);
			tmpWord.append("count", lawcaseWords.getWordCountMap().get(key) + "");
			wordsList.add(tmpWord);
			sumWordCount += lawcaseWords.getWordCountMap().get(key);
		}
		lawcaseWordsDoc.append("wordslist", wordsList);
		lawcaseWordsDoc.append("sum_word_count", sumWordCount);
		lawcasewordsCollection.insertOne(lawcaseWordsDoc);
	}

	@SuppressWarnings("unchecked")
	public static LawCaseWords getByLawCaseId(String lawcaseID) {
		Document lawcaseWordsDoc = lawcasewordsCollection.find(Filters.eq("lawcaseid", lawcaseID)).first();
		LawCaseWords lawcaseWords = new LawCaseWords();
		lawcaseWords.setLawcaseID(lawcaseID);
		lawcaseWords.setAllWords(lawcaseWordsDoc.getString("allwords"));

		Map<String, Integer> wordcountMap = new HashMap<String, Integer>();
		List<Document> wordsList = (List<Document>) lawcaseWordsDoc.get("wordslist");
		for (Document wordCountDoc : wordsList) {
			wordcountMap.put(wordCountDoc.getString("word"), Integer.parseInt((String) wordCountDoc.get("count")));
		}
		lawcaseWords.setWordCountMap(wordcountMap);

		return lawcaseWords;
	}
	
	public static FindIterable<Document> findALL(){
		return lawcasewordsCollection.find();
	}

	public static void saprkWriteLawCaseWords(JavaSparkContext jsc, JavaRDD<LawCaseWords> lawcaseWordsRdd) {
		Map<String, String> writeOverrides = new HashMap<String, String>();
		writeOverrides.put("collection", "lawcasewords");
		writeOverrides.put("writeConcern.w", "majority");
		WriteConfig writeConfig = WriteConfig.create(jsc).withOptions(writeOverrides);
		JavaRDD<Document> lawcaseWordsDocs = lawcaseWordsRdd.map(new Function<LawCaseWords, Document>() {
			@Override
			public Document call(LawCaseWords lawcaseWords) throws Exception {
				Document lawcaseWordsDoc = new Document("lawcaseid", lawcaseWords.getLawcaseID());
				lawcaseWordsDoc.append("allwords", lawcaseWords.getAllWords());

				List<Document> wordsList = new ArrayList<Document>();
				int sumWordCount = 0;
				for (String key : lawcaseWords.getWordCountMap().keySet()) {
					Document tmpWord = new Document("word", key);
					tmpWord.append("count", lawcaseWords.getWordCountMap().get(key) + "");
					wordsList.add(tmpWord);
					sumWordCount += lawcaseWords.getWordCountMap().get(key);
				}
				lawcaseWordsDoc.append("wordslist", wordsList);
				lawcaseWordsDoc.append("sum_word_count", sumWordCount);
				return lawcaseWordsDoc;
			}
		});
		MongoSpark.save(lawcaseWordsDocs, writeConfig);
	}

	public static JavaRDD<Document> sparkFindALL(JavaSparkContext jsc) {
		Map<String, String> readOverrides = new HashMap<String, String>();
	    readOverrides.put("collection", "lawcasewords");
	    readOverrides.put("readPreference.name", "secondaryPreferred");
	    ReadConfig readConfig = ReadConfig.create(jsc).withOptions(readOverrides);
	    JavaRDD<Document> lawcasewordsDocs = MongoSpark.load(jsc, readConfig);
		return lawcasewordsDocs;
	}
}
