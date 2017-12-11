package cn.edu.nju.se.lawcase.database.service;

import org.bson.Document;

import cn.edu.nju.se.lawcase.database.MongodbHelper;
import cn.edu.nju.se.lawcase.entities.CodeOfCA;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

public class CodeOfCAService {

	/**
	 * 案由代码表的操作
	 */
	
	private static MongoCollection<Document> codeOfCACollection = MongodbHelper
			.getMongoDataBase().getCollection("codeofca");

	
	/**
	 * 初始化时调用的插入一个完整的案由代码对象
	 * @param code
	 */
	public static void writeEachCode(CodeOfCA code) {
		Document document = new Document("currentcode", code.getCurrentCode());
		document.append("causeofaction", code.getCurrentCauseofAction());
		document.append("tree", code.getTreeString());
		
		codeOfCACollection.insertOne(document);
	}

	
	/**
	 * 根据一个案由代码查询一个完整的案由代码对象
	 * @param currentCode
	 * @return
	 */
	public static CodeOfCA readCodeOfCA(String currentCode) {
		FindIterable<Document> find = codeOfCACollection
				.find(new BasicDBObject("currentcode", currentCode));
		Document codeOfCADoc = find.first();
		CodeOfCA codeOfCA = new CodeOfCA();
		codeOfCA.setCurrentCode(codeOfCADoc.getString("currentcode"));
		codeOfCA.setCodeTree(codeOfCADoc.getString("tree"));
		codeOfCA.setCurrentCauseofAction(codeOfCADoc.getString("causeofaction"));

		return codeOfCA;
	}

	/**
	 * 根据案由代码查询案由的代码树
	 * @param currentCode
	 * @return
	 */
	public static String readTreeOfCodeOfCA(String currentCode) {
		FindIterable<Document> find = codeOfCACollection
				.find(new BasicDBObject("currentcode", currentCode));
		MongoCursor<Document> cursor= find.iterator();
		if(cursor.hasNext()){
			return cursor.next().getString("tree");
		}else{
			return "";
		}
		
	}
}
