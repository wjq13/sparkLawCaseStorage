package cn.edu.nju.se.lawcase.entities;

import java.util.Map;

public class LawCaseWordsTfIdf {
	private String lawcaseID;
	private Map<String, Double> wordTfIdfMap;

	public String getLawcaseID() {
		return lawcaseID;
	}

	public void setLawcaseID(String lawcaseID) {
		this.lawcaseID = lawcaseID;
	}

	public Map<String, Double> getWordTfIdfMap() {
		return wordTfIdfMap;
	}

	public void setWordTfIdfMap(Map<String, Double> wordTfIdfMap) {
		this.wordTfIdfMap = wordTfIdfMap;
	}
}
