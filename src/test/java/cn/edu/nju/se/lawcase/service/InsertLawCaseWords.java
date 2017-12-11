package cn.edu.nju.se.lawcase.service;

import org.bson.Document;

import cn.edu.nju.se.lawcase.database.service.LawCaseWordsService;
import cn.edu.nju.se.lawcase.database.service.ParagraphService;
import cn.edu.nju.se.lawcase.entities.LawCaseWords;
import cn.edu.nju.se.lawcase.main.WordFrequency;

public class InsertLawCaseWords {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		Document lawcaseParagraph = ParagraphService.findOneForTest();
//		LawCaseWords lawcaseWords = TFIDF.generateLawCaseTF(lawcaseParagraph);
//		LawCaseWordsService.writeLawCaseWords(lawcaseWords);
		LawCaseWords lcw = LawCaseWordsService.getByLawCaseId("5a0ea46c2e4edb2de08d7f64");
		System.out.println(lcw.getWordCountMap().get("公司"));
	}

}
