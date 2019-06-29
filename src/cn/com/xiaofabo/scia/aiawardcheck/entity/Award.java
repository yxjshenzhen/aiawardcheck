package cn.com.xiaofabo.scia.aiawardcheck.entity;

import java.util.List;

public class Award {
	private String id;
	private String dateText;
	private String caseIdText;
	private List proposerList;
	private List respondentList;
	private List routineText;
	private List caseText;
	private List arbiOpinionText;
	private List arbitramentText;
	
	public static String RESP_ABSENT_RESULT_TEXT = 
			"被申请人经合法通知无正当理由未到庭，亦未提交任何书面答辩意见或证据，视为自行放弃抗辩的权利，应自行承担由此引起的法律后果。";

	public Award() {

	}

	public Award(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDateText() {
		return dateText;
	}

	public void setDateText(String dateText) {
		this.dateText = dateText;
	}

	public String getCaseIdText() {
		return caseIdText;
	}

	public void setCaseIdText(String caseIdText) {
		this.caseIdText = caseIdText;
	}

	public List getProposerList() {
		return proposerList;
	}

	public void setProposerList(List proposerList) {
		this.proposerList = proposerList;
	}

	public List getRespondentList() {
		return respondentList;
	}

	public void setRespondentList(List respondentList) {
		this.respondentList = respondentList;
	}

	public List getRoutineText() {
		return routineText;
	}

	public void setRoutineText(List routineText) {
		this.routineText = routineText;
	}

	public List getCaseText() {
		return caseText;
	}

	public void setCaseText(List caseText) {
		this.caseText = caseText;
	}

	public List getArbiOpinionText() {
		return arbiOpinionText;
	}

	public void setArbiOpinionText(List arbiOpinionText) {
		this.arbiOpinionText = arbiOpinionText;
	}

	public List getArbitramentText() {
		return arbitramentText;
	}

	public void setArbitramentText(List arbitramentText) {
		this.arbitramentText = arbitramentText;
	}
}
