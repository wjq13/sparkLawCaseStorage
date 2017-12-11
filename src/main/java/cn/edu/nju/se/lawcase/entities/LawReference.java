package cn.edu.nju.se.lawcase.entities;

import java.io.Serializable;

public class LawReference implements Serializable{

	private String lawName = "";
	private String levelOneTiao = "";
	private String levelTwoKuan = "";
	private String levelThreeXiang = "";
	
	private String contentLevenOne = "";

	public String getLawName() {
		return lawName;
	}

	public void setLawName(String lawName) {
		this.lawName = lawName;
	}

	public String getLevelOneTiao() {
		return levelOneTiao;
	}

	public void setLevelOneTiao(String levelOneTiao) {
		this.levelOneTiao = levelOneTiao;
	}

	public String getLevelTwoKuan() {
		return levelTwoKuan;
	}

	public void setLevelTwoKuan(String levelTwoKuan) {
		this.levelTwoKuan = levelTwoKuan;
	}

	public String getLevelThreeXiang() {
		return levelThreeXiang;
	}

	public void setLevelThreeXiang(String levelThreeXiang) {
		this.levelThreeXiang = levelThreeXiang;
	}

	public String getContentLevenOne() {
		return contentLevenOne;
	}

	public void setContentLevenOne(String contentLevenOne) {
		this.contentLevenOne = contentLevenOne;
	}
	
}
