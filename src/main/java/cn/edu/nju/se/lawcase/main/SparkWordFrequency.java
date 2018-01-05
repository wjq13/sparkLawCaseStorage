package cn.edu.nju.se.lawcase.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.bson.Document;

import cn.edu.nju.se.lawcase.database.SparkHelper;
import cn.edu.nju.se.lawcase.database.service.LawCaseWordsService;
import cn.edu.nju.se.lawcase.database.service.ParagraphService;
import cn.edu.nju.se.lawcase.database.service.Segment2Service;
import cn.edu.nju.se.lawcase.database.service.SegmentService;
import cn.edu.nju.se.lawcase.database.service.SingleWordService;
import cn.edu.nju.se.lawcase.entities.LawCase;
import cn.edu.nju.se.lawcase.entities.LawCaseWords;
import cn.edu.nju.se.lawcase.entities.SingleWord;
import scala.Tuple2;

import com.mongodb.client.FindIterable;

public class SparkWordFrequency {

	static Map<String, SingleWord> wordCount = new HashMap<String, SingleWord>();

	public static void main(String args[]) {
		JavaSparkContext jsc = SparkHelper.getJSC(SparkHelper.LAWCASE);

		JavaRDD<Document> lawcaseParagraphs = ParagraphService.sparkFindALL(jsc);

		JavaRDD<LawCaseWords> lawcaseWordsRdd = lawcaseParagraphs.map(new Function<Document, LawCaseWords>() {
			@Override
			public LawCaseWords call(Document doc) throws Exception {
				LawCaseWords lawcaseWords = generateLawCaseTF(doc);
				return lawcaseWords;
			}
		});
		LawCaseWordsService.saprkWriteLawCaseWords(jsc, lawcaseWordsRdd);

		// JavaPairRDD<String, Integer> wordPairRDD = lawcaseWordsRdd.flatMapToPair(new
		// PairFlatMapFunction<LawCaseWords, String, Integer>() {
		// @Override
		// public Iterator<Tuple2<String, Integer>> call(LawCaseWords lawcaseWords)
		// throws Exception {
		// List<Tuple2<String, Integer>> tpLists = new ArrayList<Tuple2<String,
		// Integer>>();
		// for(String word : lawcaseWords.getWordCountMap().keySet()){
		// Tuple2 tp = new Tuple2<String,Integer>(word, 1);
		// tpLists.add(tp);
		// }
		// return tpLists.iterator();
		// }
		// });
		// JavaPairRDD<String, Integer> wordsCount = wordPairRDD.reduceByKey((v1,v2)->
		// (v1+v2));
		// JavaRDD<SingleWord> singleWordRdd = wordsCount.map(pair->{
		// SingleWord singleWord = new SingleWord();
		// singleWord.setWord(pair._1);
		// singleWord.setDocCount(pair._2);
		// return singleWord;
		// });

		lawcaseWordsRdd.map(new Function<LawCaseWords, LawCaseWords>() {
			@Override
			public LawCaseWords call(LawCaseWords lawcaseWords) throws Exception {
				for (String word : lawcaseWords.getWordCountMap().keySet()) {
					SingleWord singleWord = new SingleWord();
					singleWord.setWord(word);
					singleWord.setCurrentDocID(lawcaseWords.getLawcaseID());
					singleWord.setCurrentDocCount(lawcaseWords.getWordCountMap().get(word));
					if (wordCount.get(word) != null) {
						singleWord.setDocCount(1 + wordCount.get(word).getDocCount());
						wordCount.put(word, singleWord);
					} else {
						singleWord.setDocCount(1);
						wordCount.put(word, singleWord);
					}
				}
				return lawcaseWords;
			}
		});
		SingleWordService.sparkUpdateSingleWords(jsc, wordCount);
	}

	public static LawCaseWords generateLawCaseTF(Document lawcaseParagraph) {
		// TODO Auto-generated method stub
		LawCaseWords lawcaseWords = new LawCaseWords();

		String allWords = "";
		String lawcaseId = lawcaseParagraph.get("_id").toString();
		lawcaseWords.setLawcaseID(lawcaseId);

		// for(String paragraphName : LawCase.pNamesTF){
		// if(!(lawcaseParagraph.get(paragraphName).toString().isEmpty())){
		// String segID =
		// ((Document)lawcaseParagraph.get(paragraphName)).get("segmentid").toString();
		// allWords += SegmentService.getWordsStringByID(segID);
		// }
		// }

		// jieba分词记词频
		for (String paragraphName : LawCase.pNamesJieBaTF) {
			if (!(lawcaseParagraph.get(paragraphName).toString().isEmpty())) {
				String segID = ((Document) lawcaseParagraph.get(paragraphName)).get("segmentid").toString();
				allWords += Segment2Service.getWordsStringByID(segID);
			}
		}
		lawcaseWords.setAllWords(allWords);
		lawcaseWords.generateWordCountMap();

		return lawcaseWords;
	}

}
