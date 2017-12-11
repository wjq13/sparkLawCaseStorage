package cn.edu.nju.se.lawcase.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.se.lawcase.database.service.CodeOfCAService;
import cn.edu.nju.se.lawcase.entities.CodeOfCA;



public class InitializeCodeOfCA {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String, CodeOfCA> allCodes = readFromFile("Resources/code of cause action.txt");
		
		updateAllCodes(allCodes);
	}

	private static void updateAllCodes(Map<String, CodeOfCA> allCodes) {
		// TODO Auto-generated method stub
		for(String currentCode : allCodes.keySet()){
			CodeOfCA tmpCca = allCodes.get(currentCode);
			tmpCca.getCodeTree().add(currentCode);
			
			String rootCode = tmpCca.getFatherCode();
			if(allCodes.get(rootCode) == null){
				CodeOfCAService.writeEachCode(tmpCca);
				continue;
			}
			
			if(allCodes.get(rootCode).getTreeString().contains("9000")){
				tmpCca.getCodeTree().addAll(allCodes.get(rootCode).getCodeTree());
				CodeOfCAService.writeEachCode(tmpCca);
				
				continue;
			}
			
			while(!rootCode.equals("9000")){
				tmpCca.getCodeTree().add(rootCode);
				
				rootCode = allCodes.get(rootCode).getFatherCode();
			}
			tmpCca.getCodeTree().add(rootCode);
			
			CodeOfCAService.writeEachCode(tmpCca);
		}
	}

	private static Map<String, CodeOfCA> readFromFile(String file) {
		// TODO Auto-generated method stub
		Map<String, CodeOfCA> codes = new HashMap<String, CodeOfCA>();
		try{
			BufferedReader codeReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
			String line = "";
			while((line = codeReader.readLine()) != null){
				String[] components = line.split("\t");
				CodeOfCA cca = new CodeOfCA(components[0], components[2], components[1]);
				codes.put(cca.getCurrentCode(), cca);
			}
			codeReader.close();
			return codes;
		}catch (Exception e){
			return null;
		}
	}

}
