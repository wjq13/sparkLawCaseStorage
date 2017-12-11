package cn.edu.nju.se.lawcase.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.edu.nju.se.lawcase.database.service.LawCaseService;
import cn.edu.nju.se.lawcase.database.service.LawReferenceService;
import cn.edu.nju.se.lawcase.database.service.ParagraphService;
import cn.edu.nju.se.lawcase.entities.LawCase;
import cn.edu.nju.se.lawcase.util.Segment;
import cn.edu.nju.se.lawcase.util.XmlToLawCase;

public class LawCaseStore {
	public static void main(String args[]) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String filepath = "F://研一//民事一审测试集";// 文件夹路径
//		String filepath = args[0];// 文件夹路径

		File dir = new File(filepath);
		File[] folders = dir.listFiles();
		Segment.init();
		LawCase lawCase = null;
		int processedFiles = 0;
		List<LawCase> lawCaseList = new ArrayList<>();
		for(File folder:folders){
			if(!folder.isDirectory()){
				continue;
			}
			File[] files = folder.listFiles();
			for (File file : files) {
				processedFiles++;
				lawCase = XmlToLawCase.transOneXml(file);
				lawCase.setFilePath(file.getAbsolutePath());
				lawCase.setParagraphs();

				//LawCaseService.writeFullText(lawCase);
				//if (lawCase.getLawReferences().size() > 0) {
				//	LawReferenceService.write(lawCase.getFullTextId(), lawCase.getLawReferences());
				//}
				
				lawCaseList.add(lawCase);
				
				if (processedFiles % 2000 == 0) {
					
					LawCaseService.writeFullTextMany(lawCaseList);
					LawReferenceService.writeMany(lawCaseList);
					ParagraphService.writeLawCaseMany(lawCaseList);
					System.out.println(df.format(new Date())+"--"+processedFiles);
					
					lawCaseList = new ArrayList<LawCase>();
				}
				//if (processedFiles % 10000 == 0) {
				//	ParagraphService.writeLawCases(lawCaseList);
				//	lawCaseList = new ArrayList<>();
				//	System.out.println(df.format(new Date())+"--10000--"+processedFiles);
				//}
			}
		}

		if(lawCaseList.size()>0){
			LawCaseService.writeFullTextMany(lawCaseList);
			LawReferenceService.writeMany(lawCaseList);
			ParagraphService.writeLawCaseMany(lawCaseList);
			System.out.println(df.format(new Date())+"--"+processedFiles);
		}
		
	}

}
