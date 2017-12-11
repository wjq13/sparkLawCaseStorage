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
import cn.edu.nju.se.lawcase.entities.LawReference;

import com.mongodb.client.MongoCollection;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.WriteConfig;

public class LawReferenceService {

	private static MongoCollection<Document> lawReferenceCollection = MongodbHelper
			.getMongoDataBase().getCollection("lawreference");
	
	public static void write(String fullTextID, List<LawReference> lawReferences){
		Document document = new Document(LawCase.FullTextId, fullTextID);
		
		List<Document> lawReferencesDoc = new ArrayList<Document>();
		for(LawReference lawReference : lawReferences){
			Document lawReferenceDoc = new Document("name", lawReference.getLawName());
			lawReferenceDoc.append("levelone", lawReference.getLevelOneTiao());
			lawReferenceDoc.append("leveltwo", lawReference.getLevelTwoKuan());
			
			lawReferencesDoc.add(lawReferenceDoc);
		}
		
		document.append("references", lawReferencesDoc);
		
		lawReferenceCollection.insertOne(document);
	}

	public static void writeMany(List<LawCase> lawCaseList) {
		// TODO Auto-generated method stub
		List<Document> lawreferenceDocList = new ArrayList<Document>();
		for(LawCase lawcase : lawCaseList){
			Document document = new Document(LawCase.FullTextId, lawcase.getFullTextId());
			
			List<Document> lawReferencesDoc = new ArrayList<Document>();
			for(LawReference lawReference : lawcase.getLawReferences()){
				Document lawReferenceDoc = new Document("name", lawReference.getLawName());
				lawReferenceDoc.append("levelone", lawReference.getLevelOneTiao());
				lawReferenceDoc.append("leveltwo", lawReference.getLevelTwoKuan());
				
				lawReferencesDoc.add(lawReferenceDoc);
			}
			
			document.append("references", lawReferencesDoc);
			lawreferenceDocList.add(document);
		}
		lawReferenceCollection.insertMany(lawreferenceDocList);
	}

	public static void sparkWrite(JavaSparkContext jsc, JavaRDD<LawCase> lawcaseRdd) {
		Map<String, String> writeOverrides = new HashMap<String, String>();
		writeOverrides.put("collection", "lawreference");
		writeOverrides.put("writeConcern.w", "majority");
		WriteConfig writeConfig = WriteConfig.create(jsc).withOptions(writeOverrides);
		JavaRDD<Document> lawcaseDocs = lawcaseRdd.map(new Function<LawCase, Document>() {
			@Override
			public Document call(LawCase lawcase) throws Exception {
				Document document = new Document(LawCase.FullTextId, lawcase.getFullTextId());
				
				List<Document> lawReferencesDoc = new ArrayList<Document>();
				for(LawReference lawReference : lawcase.getLawReferences()){
					Document lawReferenceDoc = new Document("name", lawReference.getLawName());
					lawReferenceDoc.append("levelone", lawReference.getLevelOneTiao());
					lawReferenceDoc.append("leveltwo", lawReference.getLevelTwoKuan());
					
					lawReferencesDoc.add(lawReferenceDoc);
				}
				
				document.append("references", lawReferencesDoc);
				return document;
			}
		});
		
		MongoSpark.save(lawcaseDocs, writeConfig);
	}
}
