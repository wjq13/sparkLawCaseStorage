package cn.edu.nju.se.lawcase.database.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.bson.Document;

import cn.edu.nju.se.lawcase.database.MongodbHelper;
import cn.edu.nju.se.lawcase.entities.LawCase;
import cn.edu.nju.se.lawcase.util.Segment;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.ReadConfig;
import com.mongodb.spark.config.WriteConfig;

public class ParagraphService {

	private static MongoCollection<Document> paragraphCollection = MongodbHelper.getMongoDataBase()
			.getCollection("paragraph");

	public static void writeLawCase(LawCase lawCase) {
		paragraphCollection.insertOne(parseLawCase(lawCase));
	}

	public static FindIterable<Document> findALL() {
		return paragraphCollection.find();
	}

	public static Document findOneForTest() {
		return paragraphCollection.find().first();
	}

	public static int countAll() {

		return (int) paragraphCollection.count();

	}

	public static void writeLawCaseMany(List<LawCase> lawCaseList) {
		List<Document> docList = new ArrayList<>();
		List<Document> segementDocList = new ArrayList<>();
		List<Document> segementDocListNotNULL = new ArrayList<>();
		for (LawCase lawCase : lawCaseList) {
			for (int i = 0; i < lawCase.getParagraphSize(); i++) {
				String paragraph = lawCase.getByPName(LawCase.pNames[i]);
				if (!paragraph.isEmpty()) {
					String[] segmentations = Segment.getSegmentation(paragraph);
					Document document = new Document("words", segmentations[0]);
					document.append("wordpostags", segmentations[1]);
					document.append("keywords", segmentations[2]);
					document.append("keywordpostags", segmentations[3]);
					segementDocList.add(document);
					segementDocListNotNULL.add(document);
				}else{
					segementDocList.add(null);
				}
			}
		}
		
		segementDocListNotNULL = SegmentService.insertMany(segementDocListNotNULL);
		int segementDocStartIndex = 0, segementDocStartIndexNotNULL = 0;
		for (LawCase lawCase : lawCaseList) {
			List<Document> singleParaSegementDocList = new ArrayList<Document>();
			for(int segmentIndex = 0; segmentIndex < lawCase.getParagraphSize(); segmentIndex ++){
				if(segementDocList.get(segementDocStartIndex + segmentIndex) != null){
					singleParaSegementDocList.add(segementDocListNotNULL.get(segementDocStartIndexNotNULL));
					segementDocStartIndexNotNULL ++;
				}else{
					singleParaSegementDocList.add(null);
				}
			}
			segementDocStartIndex += lawCase.getParagraphSize();
			
			Document document = new Document(LawCase.FullTextId, lawCase.getFullTextId());
			document.append(LawCase.CauseOfAction, lawCase.getCauseOfAction());
			document.append(LawCase.CodeOfCA, lawCase.getCodeOfCauseOfAction());
			if (lawCase.getCodeOfCauseOfAction() == "") {
				document.append("codeofcatree", "");
			} else {
				document.append("codeofcatree", CodeOfCAService.readTreeOfCodeOfCA(lawCase.getCodeOfCauseOfAction()));
			}

			
			for (int i = 0; i < lawCase.getParagraphSize(); i++) {
				String paragraph = lawCase.getByPName(LawCase.pNames[i]);
				if (paragraph.isEmpty()) {
					document.append(LawCase.pNames[i], "");
				} else {
					Document paraTextDoc = new Document("text", paragraph);
					paraTextDoc.append("segmentid", singleParaSegementDocList.get(i).get("_id").toString());
					document.append(LawCase.pNames[i], paraTextDoc);
				}
			}
			singleParaSegementDocList = null;
			document.append("title", lawCase.getTitle());
			
			docList.add(document);
		}
		paragraphCollection.insertMany(docList);
		docList = null;
		segementDocList = null;
		segementDocListNotNULL = null;
	}
	
	public static Document parseLawCase(LawCase lawCase){
		Document document = new Document(LawCase.FullTextId, lawCase.getFullTextId());
		document.append(LawCase.CauseOfAction, lawCase.getCauseOfAction());
		document.append(LawCase.CodeOfCA, lawCase.getCodeOfCauseOfAction());
		if (lawCase.getCodeOfCauseOfAction() == "") {
			document.append("codeofcatree", "");
		} else {
			document.append("codeofcatree", CodeOfCAService.readTreeOfCodeOfCA(lawCase.getCodeOfCauseOfAction()));
		}

		List<String> paragraphs = new ArrayList<>();
		// 调segment中的方法获取长字段分词，调写分词库方法
		for (int i = 0; i < lawCase.getParagraphSize(); i++) {
			String paragraph = lawCase.getByPName(LawCase.pNames[i]);
			if (!paragraph.isEmpty()) {
				paragraphs.add(paragraph);
			}
		}

		List<Document> paraList = SegmentService.writeSegmentations(paragraphs);
		int j = 0;
		for (int i = 0; i < lawCase.getParagraphSize(); i++) {
			String paragraph = lawCase.getByPName(LawCase.pNames[i]);
			if (paragraph.isEmpty()) {
				document.append(LawCase.pNames[i], "");
			} else {
				document.append(LawCase.pNames[i], paraList.get(j));
				j++;
			}
		}
		document.append("title", lawCase.getTitle());
		return document;
	}

	public static JavaRDD<LawCase> sparkWriteLawCase(JavaSparkContext jsc,JavaRDD<LawCase> lawcaseRdd) {
		Map<String, String> writeOverrides = new HashMap<String, String>();
		writeOverrides.put("collection", "paragraph");
		writeOverrides.put("writeConcern.w", "majority");
		WriteConfig writeConfig = WriteConfig.create(jsc).withOptions(writeOverrides);
		JavaRDD<Document> lawcaseDocs = lawcaseRdd.map(new Function<LawCase, Document>() {
			@Override
			public Document call(LawCase lawCase) throws Exception {
				return sparkParseLawCase(lawCase);
			}
		});
		
		MongoSpark.save(lawcaseDocs, writeConfig);
		return lawcaseRdd;
		
	}
	
	public static Document sparkParseLawCase(LawCase lawCase){
		Document document = new Document(LawCase.FullTextId, lawCase.getFullTextId());
		document.append(LawCase.CauseOfAction, lawCase.getCauseOfAction());
		document.append(LawCase.CodeOfCA, lawCase.getCodeOfCauseOfAction());
		if (lawCase.getCodeOfCauseOfAction() == "") {
			document.append("codeofcatree", "");
		} else {
			document.append("codeofcatree", CodeOfCAService.readTreeOfCodeOfCA(lawCase.getCodeOfCauseOfAction()));
		}

		List<String> paragraphs = new ArrayList<>();
		// 调segment中的方法获取长字段分词，调写分词库方法
		for (int i = 0; i < lawCase.getParagraphSize(); i++) {
			String paragraph = lawCase.getByPName(LawCase.pNames[i]);
			if (!paragraph.isEmpty()) {
				paragraphs.add(paragraph);
			}
		}

		List<Document> paraList = SegmentService.writeSegmentations(paragraphs);
		int j = 0;
		for (int i = 0; i < lawCase.getParagraphSize(); i++) {
			String paragraph = lawCase.getByPName(LawCase.pNames[i]);
			if (paragraph.isEmpty()) {
				document.append(LawCase.pNames[i], "");
			} else {
				document.append(LawCase.pNames[i], paraList.get(j));
				j++;
			}
		}
		document.append("title", lawCase.getTitle());
		return document;
	}

	public static JavaRDD<Document> sparkFindALL(JavaSparkContext jsc) {
		Map<String, String> readOverrides = new HashMap<String, String>();
	    readOverrides.put("collection", "paragraph");
	    readOverrides.put("readPreference.name", "secondaryPreferred");
	    ReadConfig readConfig = ReadConfig.create(jsc).withOptions(readOverrides);
	    JavaRDD<Document> paragraphDocs = MongoSpark.load(jsc, readConfig);
		return paragraphDocs;
	}

	public static int sparkCountAll(JavaSparkContext jsc) {
		Map<String, String> readOverrides = new HashMap<String, String>();
	    readOverrides.put("collection", "paragraph");
	    readOverrides.put("readPreference.name", "secondaryPreferred");
	    ReadConfig readConfig = ReadConfig.create(jsc).withOptions(readOverrides);
	    JavaRDD<Document> paragraphDocs = MongoSpark.load(jsc, readConfig);
		return (int) paragraphDocs.count();
	}
}
