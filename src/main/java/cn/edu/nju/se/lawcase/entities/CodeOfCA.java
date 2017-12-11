package cn.edu.nju.se.lawcase.entities;

import java.util.ArrayList;
import java.util.List;

public class CodeOfCA {

	/**
	 * 案由代码类，包括案由代码、案由内容和父代码、到根代码的代码树
	 */
	
	private String currentCode;
	private String currentCauseofAction;
	private String fatherCode;
	
	private List<String> codeTree;

	public CodeOfCA(){
		this.codeTree = new ArrayList<String>();
	}
	
	public CodeOfCA(String currentCode, String currentCA, String fatherCode){
		this.currentCode = currentCode;
		this.currentCauseofAction = currentCA;
		this.fatherCode = fatherCode;
		
		this.codeTree = new ArrayList<String>();
	}

	public String getCurrentCode() {
		return currentCode;
	}

	public void setCurrentCode(String currentCode) {
		this.currentCode = currentCode;
	}

	public String getCurrentCauseofAction() {
		return currentCauseofAction;
	}

	public void setCurrentCauseofAction(String currentCauseofAction) {
		this.currentCauseofAction = currentCauseofAction;
	}

	public String getFatherCode() {
		return fatherCode;
	}

	public void setFatherCode(String fatherCode) {
		this.fatherCode = fatherCode;
	}

	public List<String> getCodeTree() {
		return codeTree;
	}

	public void setCodeTree(List<String> codeTree) {
		this.codeTree = codeTree;
	}
	
	public void setCodeTree(String codeTree) {
		String[] tree = codeTree.split("-");
		for(int i = 1; i < tree.length; i ++){
			this.codeTree.add(tree[i]);
		}
		this.setFatherCode(this.codeTree.get(0));
	}

	public String getTreeString(){
		String tree = "";
		for(String father : this.codeTree){
			tree += father + "-";
		}
		return tree;
	}
}
