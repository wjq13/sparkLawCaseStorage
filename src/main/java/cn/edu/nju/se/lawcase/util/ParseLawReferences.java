package cn.edu.nju.se.lawcase.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.edu.nju.se.lawcase.entities.LawReference;

public class ParseLawReferences {

	public static List<LawReference> extractLawReferences(File file) {
		// 创建SAXReader对象
		SAXReader reader = new SAXReader();
		// 读取文件 转换成Document
		org.dom4j.Document domDocument = null;
		// 获取根节点元素对象
		Element root = null;

		List<LawReference> lawReferences = new ArrayList<LawReference>();
		if (file != null) {
			try {
				domDocument = reader.read(file);
				root = domDocument.getRootElement().element("QW");

				Element analysisProcess = root.element("CPFXGC");
				if (analysisProcess != null) {
					generateLawReferences(analysisProcess, lawReferences);
				}
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lawReferences;
	}

	@SuppressWarnings("unchecked")
	public static void generateLawReferences(Element analysisProcess,
			List<LawReference> lawReferences) {
		Element lawGroup = analysisProcess.element("CUS_FLFT_FZ_RY");
		if (lawGroup != null) {
			List<Element> laws = lawGroup.elements("CUS_FLFT_RY");
			for (Element law : laws) {
				String lawInfo = law.attributeValue("value");
				LawReference lawReference = new LawReference();

				String[] nameWithOthers = null;
				if (lawInfo.contains("》")) {
					nameWithOthers = lawInfo.split("》");
					lawReference.setLawName(nameWithOthers[0] + "》");
				} else {
					nameWithOthers = lawInfo.split("法");
					lawReference.setLawName("《" + nameWithOthers[0] + "法》");
				}
				if (nameWithOthers.length > 1) {
					String[] levelOneWithOthers = nameWithOthers[1].split("条");
					lawReference.setLevelOneTiao(levelOneWithOthers[0] + "条");
					if (levelOneWithOthers.length > 1) {
						lawReference.setLevelTwoKuan(levelOneWithOthers[1]);
					}
				}

				lawReferences.add(lawReference);
			}
		}
	}
}
