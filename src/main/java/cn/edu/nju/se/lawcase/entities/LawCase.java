package cn.edu.nju.se.lawcase.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LawCase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 全文
	private String fullText;
	// 全文Id
	private String fullTextId;
	// 来源文件
	private String sourceFileName;
	// 案由
	private String causeOfAction;
	// 案由代码
	private String codeOfCauseOfAction;
	// 文首
	private String beginning;
	// 当事人
	private String litigant;
	// 诉讼记录全文
	private String litigationRecord;
	// 原告诉称
	private String plaintiffAlleges;
	// 被告辩称
	private String defendantArgued;
	// 查明事实
	private String factFinded;
	// 裁判分析过程
	private String analysisProcess;
	// 判决结果
	private String decisionResult;
	// 文尾
	private String end;
	// 标题
	private String title;

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getCauseOfAction() {
		return causeOfAction;
	}

	public void setCauseOfAction(String causeOfAction) {
		this.causeOfAction = causeOfAction;
	}

	public String getCodeOfCauseOfAction() {
		return codeOfCauseOfAction;
	}

	public void setCodeOfCauseOfAction(String codeOfCauseOfAction) {
		this.codeOfCauseOfAction = codeOfCauseOfAction;
	}

	public String getBeginning() {
		return beginning;
	}

	public void setBeginning(String beginning) {
		this.beginning = beginning;
	}

	public String getLitigant() {
		return litigant;
	}

	public void setLitigant(String litigant) {
		this.litigant = litigant;
	}

	public String getLitigationRecord() {
		return litigationRecord;
	}

	public void setLitigationRecord(String litigationRecord) {
		this.litigationRecord = litigationRecord;
	}

	public String getPlaintiffAlleges() {
		return plaintiffAlleges;
	}

	public void setPlaintiffAlleges(String plaintiffAlleges) {
		this.plaintiffAlleges = plaintiffAlleges;
	}

	public String getDefendantArgued() {
		return defendantArgued;
	}

	public void setDefendantArgued(String defendantArgued) {
		this.defendantArgued = defendantArgued;
	}

	public String getAnalysisProcess() {
		return analysisProcess;
	}

	public void setAnalysisProcess(String analysisProcess) {
		this.analysisProcess = analysisProcess;
	}

	public String getDecisionResult() {
		return decisionResult;
	}

	public void setDecisionResult(String decisionResult) {
		this.decisionResult = decisionResult;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getFactFinded() {
		return factFinded;
	}

	public void setFactFinded(String factFinded) {
		this.factFinded = factFinded;
	}

	public String getFullTextId() {
		return fullTextId;
	}

	public void setFullTextId(String fullTextId) {
		this.fullTextId = fullTextId;
	}

	public final static String FullText = "fullText";
	public final static String FullTextId = "fullTextId";
	public final static String SourceFile = "sourceFileName";
	public final static String CauseOfAction = "causeOfAction";
	public final static String CodeOfCA = "codeOfCauseOfAction";

	public final static String Head = "head";
	public final static String Litigant = "litigant";
	public final static String LitigationRecord = "litigationRecord";
	public final static String PlaintiffAlleges = "plaintiffAlleges";
	public final static String DefendantArgued = "defendantArgued";
	public final static String FactFound = "factFound";
	public final static String AnalysisProcess = "analysisProcess";
	public final static String CaseDecision = "caseDecision";

	private Map<String, String> paragraphs;
	private LawCaseWords lawcaseTF;
	private List<LawReference> lawReferences;

	private String filePath;

	public static String[] pNames = new String[] { Head, LitigationRecord, PlaintiffAlleges, DefendantArgued, FactFound,
			AnalysisProcess, CaseDecision };

	public static String[] pNamesTF = new String[] { LitigationRecord, PlaintiffAlleges, DefendantArgued, FactFound };

	public static String[] pNamesJieBaTF = new String[] { PlaintiffAlleges, DefendantArgued, FactFound };

	public LawCase() {
		this.lawcaseTF = null;
		this.lawReferences = new ArrayList<LawReference>();
	}

	public void setParagraphs() {
		this.paragraphs = new HashMap<String, String>();
		this.paragraphs.put(LawCase.Head, this.getBeginning());
		this.paragraphs.put(LawCase.LitigationRecord, this.getLitigationRecord());
		this.paragraphs.put(LawCase.PlaintiffAlleges, this.getPlaintiffAlleges());
		this.paragraphs.put(LawCase.DefendantArgued, this.getDefendantArgued());
		this.paragraphs.put(LawCase.FactFound, this.getFactFinded());
		this.paragraphs.put(LawCase.AnalysisProcess, this.getAnalysisProcess());
		this.paragraphs.put(LawCase.CaseDecision, this.getDecisionResult());
	}

	public int getParagraphSize() {
		return paragraphs.size();
	}

	public String getByPName(String pName) {
		return paragraphs.get(pName);
	}

	public LawCaseWords getLawcaseTF() {
		return lawcaseTF;
	}

	public void setLawcaseTF(LawCaseWords lawcaseTF) {
		this.lawcaseTF = lawcaseTF;
	}

	public List<LawReference> getLawReferences() {
		return lawReferences;
	}

	public void setLawReferences(List<LawReference> lawReferences) {
		this.lawReferences = lawReferences;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
