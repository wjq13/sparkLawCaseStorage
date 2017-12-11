package cn.edu.nju.se.lawcase.database;

import com.mongodb.MongoClient;
import com.mongodb.client.*;

public class MongodbHelper {
	static final String DBName = "test";
	static final String ServerAddress = "localhost";
	static final int PORT = 27017;
//	static final String ServerAddress = "192.168.68.220";
//	static final int PORT = 30000;

	private static MongoClient mongoClient = null;
	private static MongoDatabase mongoDataBase = null;

	private static MongoClient getMongoClient() {
		try {
			// 连接到 mongodb 服务
			if (mongoClient == null) {
				mongoClient = new MongoClient(ServerAddress, PORT);
				System.out.println("Connect to mongodb successfully");
			}
			return mongoClient;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		}
	}

	public static MongoDatabase getMongoDataBase() {
		try {
			if (mongoDataBase == null) {
				// 连接到数据库
				mongoClient = getMongoClient();
				mongoDataBase = mongoClient.getDatabase(DBName);
				System.out.println("Connect to DataBase successfully");
			} 
			return mongoDataBase;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void closeMongoClient(MongoDatabase mongoDataBase,
			MongoClient mongoClient) {
		if (mongoDataBase != null) {
			mongoDataBase = null;
		}
		if (mongoClient != null) {
			mongoClient.close();
		}
		System.out.println("CloseMongoClient successfully");

	}
}
