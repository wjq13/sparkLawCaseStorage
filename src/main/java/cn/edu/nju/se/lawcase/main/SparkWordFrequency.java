package cn.edu.nju.se.lawcase.main;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.bson.Document;

import cn.edu.nju.se.lawcase.database.SparkHelper;
import cn.edu.nju.se.lawcase.database.service.LawCaseWordsService;
import cn.edu.nju.se.lawcase.database.service.ParagraphService;
import cn.edu.nju.se.lawcase.database.service.SegmentService;
import cn.edu.nju.se.lawcase.database.service.SingleWordService;
import cn.edu.nju.se.lawcase.entities.LawCase;
import cn.edu.nju.se.lawcase.entities.LawCaseWords;
import cn.edu.nju.se.lawcase.entities.SingleWord;

import com.mongodb.client.FindIterable;

public class SparkWordFrequency {

	
	static Map<String,SingleWord> wordCount = new HashMap<String, SingleWord>();
	public static void main(String args[]){
		JavaSparkContext jsc = SparkHelper.getJSC();
		
		JavaRDD<Document> lawcaseParagraphs = ParagraphService.sparkFindALL(jsc);
		
		JavaRDD<LawCaseWords> lawcaseWordsRdd = lawcaseParagraphs.map(new Function<Document, LawCaseWords>() {
			@Override
			public LawCaseWords call(Document doc) throws Exception {
				LawCaseWords lawcaseWords = generateLawCaseTF(doc);
				return lawcaseWords;
			}
		});
		LawCaseWordsService.saprkWriteLawCaseWords(jsc,lawcaseWordsRdd);
		lawcaseWordsRdd.map(new Function<LawCaseWords, LawCaseWords>() {
			@Override
			public LawCaseWords call(LawCaseWords lawcaseWords) throws Exception {
				for(String word : lawcaseWords.getWordCountMap().keySet()){
					SingleWord singleWord = new SingleWord();
					singleWord.setWord(word);
					singleWord.setCurrentDocID(lawcaseWords.getLawcaseID());
					singleWord.setCurrentDocCount(lawcaseWords.getWordCountMap().get(word));
					if(wordCount.get(word)!=null){
						singleWord.setDocCount(1+wordCount.get(word).getDocCount());
						wordCount.put(word, singleWord);
					}else{
						singleWord.setDocCount(1);
						wordCount.put(word, singleWord);
					}
				}
				return lawcaseWords;
			}
		});
		SingleWordService.sparkUpdateSingleWords(jsc,wordCount);
	}

	public static LawCaseWords generateLawCaseTF(Document lawcaseParagraph) {
		// TODO Auto-generated method stub
		LawCaseWords lawcaseWords = new LawCaseWords();
		
		String allWords = "";
		String lawcaseId = lawcaseParagraph.get("_id").toString();
		lawcaseWords.setLawcaseID(lawcaseId);
		
		for(String paragraphName : LawCase.pNamesTF){
			if(!(lawcaseParagraph.get(paragraphName).toString().isEmpty())){
				String segID = ((Document)lawcaseParagraph.get(paragraphName)).get("segmentid").toString();
				allWords += SegmentService.getWordsStringByID(segID);
			}
		}
		lawcaseWords.setAllWords(allWords);
		lawcaseWords.generateWordCountMap();
		
		return lawcaseWords;
	}
	
}
