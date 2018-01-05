package cn.edu.nju.se.lawcase.database.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import cn.edu.nju.se.lawcase.database.MongodbHelper;
import cn.edu.nju.se.lawcase.entities.LawCase;
import cn.edu.nju.se.lawcase.util.Segment;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class Segment2Service {

	private static MongoCollection<Document> segmentCollection = MongodbHelper.getMongoDataBase()
			.getCollection("segment2");

	public static String getWordsStringByID(String segID) {
		FindIterable<Document> segs = segmentCollection.find(Filters.eq("_id", new ObjectId(segID)));
		if(segs.iterator().hasNext()){
			return (String)segs.first().get("words");
		}
		else{
			return "";
		}
		
	}
}
