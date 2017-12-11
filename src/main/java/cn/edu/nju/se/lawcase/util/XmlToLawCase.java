package cn.edu.nju.se.lawcase.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.edu.nju.se.lawcase.entities.LawCase;
import cn.edu.nju.se.lawcase.entities.LawReference;


public class XmlToLawCase {
	
	public static List<File> getFileList(String strPath) {
		List<File> filelist = new ArrayList<>();
		File dir = new File(strPath);
		File[] files = dir.listFiles();
		//System.out.println(files.length);
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				//System.out.println(fileName);
				if (files[i].isDirectory()) {
					getFileList(files[i].getAbsolutePath());
				} else if (fileName.endsWith("xml")) {
					filelist.add(files[i]);
				} else {
					continue;
				}
			}

		}
		return filelist;
	}
	
	public static LawCase transOneXml(File file) {
		// 创建SAXReader对象
		SAXReader reader = new SAXReader();
		// 读取文件 转换成Document
		org.dom4j.Document domdocument = null;
		// 获取根节点元素对象
		Element root = null;
		Element ele = null;
		Attribute value = null;
		LawCase lawCase = new LawCase();
		lawCase.setSourceFileName(file.getName());
		//System.out.println(file.getName());
		
		root = null;
		ele = null;
		value = null;
		if (file != null) {
			try {
				domdocument = reader.read(file);

				root = domdocument.getRootElement().element("QW");
				lawCase.setFullText(root.attribute("value").getValue());

				ele = root.element("WS");
				if (ele != null)
					value = ele.attribute("value");
				else
					value.setValue("");
				lawCase.setBeginning(value.getValue());

				ele = root.element("DSR");
				if (ele != null)
					value = ele.attribute("value");
				else
					value.setValue("");
				lawCase.setLitigant(value.getValue());

				ele = root.element("SSJL");
				if (ele != null)
					value = ele.attribute("value");
				else
					value.setValue("");
				lawCase.setLitigationRecord(value.getValue());
				
				if (ele != null) {
					ele = ele.element("AY");
					if (ele != null) {
						Element eleTmp = ele.element("WZAY");
						if (eleTmp != null)
							value = eleTmp.attribute("value");
						else
							value.setValue("");
						lawCase.setCauseOfAction(value.getValue());
						
						eleTmp = ele.element("AYDM");
						if (eleTmp != null)
							value = eleTmp.attribute("value");
						else
							value.setValue("");
						lawCase.setCodeOfCauseOfAction(value.getValue());
					} else {
						lawCase.setCauseOfAction("");
						lawCase.setCodeOfCauseOfAction("");
					}
				} else {
					lawCase.setCauseOfAction("");
					lawCase.setCodeOfCauseOfAction("");
				}

				Element ajjbqk = root.element("AJJBQK");
				if (ajjbqk != null) {
					ele = ajjbqk.element("YGSCD");
					if (ele != null)
						value = ele.attribute("value");
					else
						value.setValue("");
					lawCase.setPlaintiffAlleges(value.getValue());

					ele = ajjbqk.element("BGBCD");
					if (ele != null){
						value = ele.attribute("value");
					}
					else{
						ele = ajjbqk.element("BGRBC");
						if(ele != null){
							value = ele.attribute("value");
						}else
							value.setValue("");
					}
						
					lawCase.setDefendantArgued(value.getValue());

					ele = ajjbqk.element("CMSSD");
					if (ele != null)
						value = ele.attribute("value");
					else
						value.setValue("");
					lawCase.setFactFinded(value.getValue());
				} else {
					lawCase.setPlaintiffAlleges(value.getValue());
					lawCase.setDefendantArgued(value.getValue());
					lawCase.setFactFinded(value.getValue());
				}

				ele = root.element("CPFXGC");
				if (ele != null){
					value = ele.attribute("value");
					
					//System.out.println(file.getAbsolutePath());
					
					List<LawReference> lawReferences = new ArrayList<LawReference>();
					ParseLawReferences.generateLawReferences(ele, lawReferences);
					lawCase.setLawReferences(lawReferences);
				}else{
					value.setValue("");
				}
				lawCase.setAnalysisProcess(value.getValue());

				ele = root.element("PJJG");
				if (ele != null)
					value = ele.attribute("value");
				else
					value.setValue("");
				lawCase.setDecisionResult(value.getValue());

				ele = root.element("WW");
				if (ele != null)
					value = ele.attribute("value");
				else
					value.setValue("");
				lawCase.setEnd(value.getValue());
				
				ele = root.element("title");
				if (ele != null)
					value = ele.attribute("value");
				else
					value.setValue("");
				lawCase.setTitle(value.getValue());
				
				return lawCase;
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}
}
