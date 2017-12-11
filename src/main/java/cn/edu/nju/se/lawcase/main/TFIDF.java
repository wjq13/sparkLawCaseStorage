package cn.edu.nju.se.lawcase.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import cn.edu.nju.se.lawcase.database.service.LawCaseWordsService;
import cn.edu.nju.se.lawcase.database.service.ParagraphService;
import cn.edu.nju.se.lawcase.database.service.SingleWordService;
import cn.edu.nju.se.lawcase.database.service.TfIdfService;
import cn.edu.nju.se.lawcase.entities.LawCaseWordsTfIdf;

import com.mongodb.client.FindIterable;

public class TFIDF {

	
	static Map<String, Integer> worddocCount = new HashMap<>();
	public static void main(String args[]) {
		int filenum = ParagraphService.countAll();
		worddocCount = SingleWordService.findALLWordCount();
		FindIterable<Document> lawcaseWords = LawCaseWordsService.findALL();
		for (Document lawcaseWord : lawcaseWords) {
			LawCaseWordsTfIdf tfIdf = generateLawCaseTfIdf(lawcaseWord, filenum);
			TfIdfService.InsertOne(tfIdf);
		}
	}

	@SuppressWarnings("unchecked")
	private static LawCaseWordsTfIdf generateLawCaseTfIdf(Document lawcaseWord, int fileNum) {
		LawCaseWordsTfIdf lcw = new LawCaseWordsTfIdf();
		
		String lawcaseId = lawcaseWord.get("lawcaseid").toString();
		lcw.setLawcaseID(lawcaseId);
		int countSum = (int) lawcaseWord.get("sum_word_count");

		Map<String, Double> wordtfidfMap = new HashMap<String, Double>();
		List<Document> wordsList = (List<Document>) lawcaseWord.get("wordslist");
		
		for (Document wordCountDoc : wordsList) {
			String word = wordCountDoc.getString("word");
			int wordcount = Integer.parseInt(wordCountDoc.get("count").toString());
			//int docCount = SingleWordService.getDocCountByWord(word);
			int docCount = worddocCount.get(word);
			double tf = wordcount / (double) countSum;
			double idf = Math.log10((double) fileNum / (1 + docCount));
			double tfidf = tf * idf;
			wordtfidfMap.put(word, tfidf);

			
		}
		lcw.setWordTfIdfMap(wordtfidfMap);
		return lcw;
	}
}
