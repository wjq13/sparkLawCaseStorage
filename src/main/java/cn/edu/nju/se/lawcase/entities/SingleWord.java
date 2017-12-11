package cn.edu.nju.se.lawcase.entities;

import java.util.Map;

public class SingleWord {

	private String word;
	private int docCount;
	
	private String currentDocID;
	private int currentDocCount;
	/*
	 * 包含这个词的所有文档ID和对应的词频
	 */
	private Map<String, Integer> docWithCount;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getDocCount() {
		return docCount;
	}
	public void setDocCount(int docCount) {
		this.docCount = docCount;
	}
	public Map<String, Integer> getDocWithCount() {
		return docWithCount;
	}
	public void setDocWithCount(Map<String, Integer> docWithCount) {
		this.docWithCount = docWithCount;
	}
	public String getCurrentDocID() {
		return currentDocID;
	}
	public void setCurrentDocID(String currentDocID) {
		this.currentDocID = currentDocID;
	}
	public int getCurrentDocCount() {
		return currentDocCount;
	}
	public void setCurrentDocCount(int currentDocCount) {
		this.currentDocCount = currentDocCount;
	}

}
