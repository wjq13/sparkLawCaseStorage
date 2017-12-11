package cn.edu.nju.se.lawcase.database.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import cn.edu.nju.se.lawcase.database.MongodbHelper;
import cn.edu.nju.se.lawcase.entities.LawCase;
import cn.edu.nju.se.lawcase.util.Segment;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class SegmentService {

	private static MongoCollection<Document> segmentCollection = MongodbHelper.getMongoDataBase()
			.getCollection("segment");

	public static List<Document> insertMany(List<Document> segmentDocList) {
		segmentCollection.insertMany(segmentDocList);
		return segmentDocList;
	}
	
	public static List<Document> writeSegmentations(List<String> paragraphs) {
		List<Document> paralist = new ArrayList<>();
		List<Document> segList = new ArrayList<>();
		for (String paragraph : paragraphs) {
			String[] segmentations = Segment.getSegmentation(paragraph);
			Document document = new Document("words", segmentations[0]);
			document.append("wordpostags", segmentations[1]);
			document.append("keywords", segmentations[2]);
			document.append("keywordpostags", segmentations[3]);
			segList.add(document);
		}
		segmentCollection.insertMany(segList);
		for (int i = 0; i < paragraphs.size(); i++) {
			Document paraDoc = new Document("text", paragraphs.get(i));
			paraDoc.append("segmentid", segList.get(i).get("_id").toString());
			paralist.add(paraDoc);
		}
		return paralist;

	}

	public static String writeSegmentation(String[] segmentations) {
		Document document = new Document("words", segmentations[0]);
		document.append("wordpostags", segmentations[1]);
		document.append("keywords", segmentations[2]);
		document.append("keywordpostags", segmentations[3]);

		segmentCollection.insertOne(document);
		return document.get("_id").toString();
	}

	public static String getWordsStringByID(String segID) {
		return (String) segmentCollection.find(Filters.eq("_id", new ObjectId(segID))).first().get("words");
	}
}
