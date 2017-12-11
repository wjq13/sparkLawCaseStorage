package cn.edu.nju.se.lawcase.service;

import cn.edu.nju.se.lawcase.database.service.CodeOfCAService;
import cn.edu.nju.se.lawcase.entities.CodeOfCA;

public class ReadCodeTree {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CodeOfCAService ccas = new CodeOfCAService();
		
		String resultTree = ccas.readTreeOfCodeOfCA("9194");
		
		CodeOfCA cca = ccas.readCodeOfCA("9194");
		
		System.out.println(resultTree);
		System.out.println(cca.getCurrentCauseofAction());
		System.out.println(cca.getTreeString());
	}

}
