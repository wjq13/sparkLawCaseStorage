package cn.edu.nju.se.lawcase.entities;

import java.util.HashMap;
import java.util.Map;

public class LawCaseWords {

	private String lawcaseID;
	private String allWords;
	private Map<String, Integer> wordCountMap;
	
	public String getLawcaseID() {
		return lawcaseID;
	}
	public void setLawcaseID(String lawcaseID) {
		this.lawcaseID = lawcaseID;
	}
	public String getAllWords() {
		return allWords;
	}
	public void setAllWords(String allWords) {
		this.allWords = allWords;
	}
	public Map<String, Integer> getWordCountMap() {
		return wordCountMap;
	}
	public void setWordCountMap(Map<String, Integer> wordCountMap) {
		this.wordCountMap = wordCountMap;
	}
	
	public void generateWordCountMap(){
		String[] allWordsArray = this.allWords.split(" ");
		Map<String, Integer> wordCountMapTmp = new HashMap<>();
		for(int i = 0; i < allWordsArray.length; ++ i) {
			String currentWord = allWordsArray[i];
			if(wordCountMapTmp.containsKey(currentWord)){
				wordCountMapTmp.put(currentWord, (int) wordCountMapTmp.get(currentWord) + 1);
			}else{
				wordCountMapTmp.put(currentWord, 1);
			}
		}
		this.wordCountMap = wordCountMapTmp;
	}

}
