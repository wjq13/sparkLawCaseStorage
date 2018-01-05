package cn.edu.nju.se.lawcase.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class Segment {

	/**
	 * 
	 * @author Li Chuanyi
	 * NLPIR接口定义
	 */
	public interface CLibrary extends Library {
		CLibrary Instance = (CLibrary) Native.loadLibrary("/opt/SegmentationJars/libNLPIR.so", CLibrary.class);
		// 定义并初始化接口的静态变量 这一个语句是来加载 dll 的，注意 dll 文件的路径,可以是绝对路径也可以是相对路径，只需要填写 dll 的文件名，不能加后缀。

		// 初始化函数声明
		public int NLPIR_Init(byte[] sDataPath, int encoding, byte[] sLicenceCode);

		//执行分词函数声明
		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

		//提取关键词函数声明
		public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit, boolean bWeightOut);

		//退出函数声明
		public void NLPIR_Exit();
	}
	
	/**
	 * 把Segment当成一个对象使用，需要初始化；这里提供初始化工作。之后可以使用这个对象处理所有的句子
	 * @return
	 */
	public static boolean init(){
		String argu = "/opt";
		String system_charset = "utf8";
		int charset_type = 1;
		int init_flag;
		try {
			init_flag = CLibrary.Instance.NLPIR_Init(argu.getBytes(system_charset),
					charset_type, "0".getBytes(system_charset));
			if (0 == init_flag) {
				System.out.println("初始化失败！");
				return false;
			}
			return true;
		} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 一次使用完之后需要推出分词模式
	 * @return
	 */
	public static boolean exit(){
		try {
			CLibrary.Instance.NLPIR_Exit();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * 使用NLPIR进行分词
	 * @param content 需要分词的文本内容
	 * @return 
	 * 一个包含四个串的数组：每个串内使用空格分隔内容
	 * 	1.单词串
	 * 	2.单词的Postag串
	 * 	3.关键词串
	 * 	4.关键词的postag串
	 */
	public static String[] processSentenceNLPIR(String content){
		String wordsWithTag = CLibrary.Instance.NLPIR_ParagraphProcess(content, 1);
		String keywordsWithTag = CLibrary.Instance.NLPIR_GetKeyWords(content, 15, true);

		String[] wordsandtags = processWordWithTagsNLPIR(wordsWithTag, " +");
		String[] keywordsandtags = processWordWithTagsNLPIR(keywordsWithTag, "#");
		
		return concat(wordsandtags, keywordsandtags);
	}
	
	/**
	 * 对NLPIR分词结果切分原文串或关键词串
	 * @param wordsWithTag 处理后的分词结果串
	 * @param spliter 串内的分隔符，原文是空格，关键词是“#”
	 * @return 单词串和标签串的数组
	 */
	private static String[] processWordWithTagsNLPIR(String wordsWithTag, String spliter){
		String[] wordsWithTagArray = wordsWithTag.split(spliter);
		String words = "", tags = "";
		for(String wordWithTag : wordsWithTagArray){
			if(wordWithTag.equals("")){
				continue;
			}
			String[] wordAndTag = wordWithTag.split("/");
			if(wordAndTag.length<2){
				continue;
			}
			words += wordAndTag[0] + " ";
			tags += "/" + wordAndTag[1] + " ";
		}
		return new String[]{words, tags};
	}
	
	/**
	 * 合并两个数组
	 * @param first
	 * @param second
	 * @return
	 */
	public static <T> T[] concat(T[] first, T[] second) {  
		  T[] result = Arrays.copyOf(first, first.length + second.length);  
		  System.arraycopy(second, 0, result, first.length, second.length);  
		  return result;  
		}

	/**
	 * @param mouduanchangwen 某段长信息
	 * @return 生成的分词信息:
	 */
	public static String[] getSegmentation(String originalText){
		return processSentenceNLPIR(originalText);
	}
	
}
