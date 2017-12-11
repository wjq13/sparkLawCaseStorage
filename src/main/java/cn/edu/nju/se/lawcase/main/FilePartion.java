package cn.edu.nju.se.lawcase.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.mapreduce.v2.jobhistory.FileNameIndexUtils;

public class FilePartion {

	
	public static void main(String args[]){
		File fatherFolder = new File("new lawcases");
		if(!fatherFolder.exists()){
			fatherFolder.mkdir();
		}
		File dir = new File(args[0]);
		File[] files = dir.listFiles();
		
		int tmpFileCount = 0, tmpFolderCount = 0;
		File subFolder = new File(fatherFolder + "/subfolder" + tmpFolderCount);
		if(!subFolder.exists()){
			subFolder.mkdir();
		}
		for(int fileIndex = 0; fileIndex < files.length; fileIndex ++){
			files[fileIndex].renameTo(new File(subFolder + "/" + files[fileIndex].getName()));
			if(tmpFileCount < 9999){
				tmpFileCount ++;
			}else{
				tmpFolderCount ++;
				tmpFileCount = 0;
				subFolder = new File(fatherFolder + "/subfolder" + tmpFolderCount);
				if(!subFolder.exists()){
					subFolder.mkdir();
				}
			}
		}
	}
}
