package cn.edu.nju.se.lawcase.main;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;

import cn.edu.nju.se.lawcase.database.service.LawCaseWordsService;
import cn.edu.nju.se.lawcase.database.service.ParagraphService;
import cn.edu.nju.se.lawcase.database.service.SegmentService;
import cn.edu.nju.se.lawcase.database.service.SingleWordService;
import cn.edu.nju.se.lawcase.entities.LawCase;
import cn.edu.nju.se.lawcase.entities.LawCaseWords;
import cn.edu.nju.se.lawcase.entities.SingleWord;

import com.mongodb.client.FindIterable;

public class WordFrequency {

	
	static Map<String,SingleWord> wordCount = new HashMap<String, SingleWord>();
	public static void main(String args[]){
		
		FindIterable<Document> lawcaseParagraphs = ParagraphService.findALL();
		System.out.println("paragraph findALL OK");
		for(Document lawcaseParagraph : lawcaseParagraphs){
			LawCaseWords lawcaseWords = generateLawCaseTF(lawcaseParagraph);
			LawCaseWordsService.writeLawCaseWords(lawcaseWords);
			
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
				
//				SingleWord singleWord = new SingleWord();
//				singleWord.setWord(word);
//				singleWord.setCurrentDocID(lawcaseWords.getLawcaseID());
//				singleWord.setCurrentDocCount(lawcaseWords.getWordCountMap().get(word));
//				
//				singleWord.setDocCount(1);
//				
//				SingleWordService.updateSingleWord(singleWord);
			}
			
		}
		SingleWordService.updateSingleWords(wordCount);
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
