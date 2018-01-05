package cn.edu.nju.se.lawcase.main;

import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.WriteConfig;

import cn.edu.nju.se.lawcase.database.SparkHelper;
import cn.edu.nju.se.lawcase.database.service.LawCaseService;
import cn.edu.nju.se.lawcase.database.service.LawReferenceService;
import cn.edu.nju.se.lawcase.database.service.ParagraphService;
import cn.edu.nju.se.lawcase.entities.LawCase;
import cn.edu.nju.se.lawcase.util.Segment;
import cn.edu.nju.se.lawcase.util.XmlToLawCase;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.SparkSession;

import org.bson.Document;

import static java.util.Arrays.asList;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparkLawCaseStore {
	public static void main(final String[] args) throws InterruptedException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String filepath = "F:\\研一work\\xml民事案件";// 文件夹路径

		List<File> files = XmlToLawCase.getFileList(filepath);
		Segment.init();
		System.out.println(df.format(new Date())+"--start");
		JavaSparkContext jsc = SparkHelper.getJSC(SparkHelper.LAWCASE);

		JavaRDD<File> fileRDD = jsc.parallelize(files);

		JavaRDD<LawCase> lawcaseRdd = fileRDD.map(new Function<File, LawCase>() {
			@Override
			public LawCase call(File file) throws Exception {
				LawCase lawCase = XmlToLawCase.transOneXml(file);
				lawCase.setFilePath(file.getAbsolutePath());
				lawCase.setParagraphs();
				return lawCase;

			}
		});
		JavaRDD<LawCase> afterFullRdd = LawCaseService.sparkWriteFullText(jsc, lawcaseRdd);
		JavaRDD<LawCase> afterWriteRdd = ParagraphService.sparkWriteLawCase(jsc, afterFullRdd);
		LawReferenceService.sparkWrite(jsc,afterWriteRdd);
		System.out.println(df.format(new Date())+"--end");
		SparkHelper.closeJSC();
	}
}
