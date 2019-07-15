package cn.com.xiaofabo.scia.aiawardcheck.entity;

import java.util.List;

public class Award {
	private String id;
	private String dateText;
	private String caseIdText;
	private List partyList;
	private List jianjieText;
	private List routineText;
	private List caseText;
	private List arbiOpinionText;
	private List arbitramentText;
	private List footText;

	/// 案情部分
	private boolean hasReply;
	private boolean hasCounterClaim;
	private boolean hasProposerEvidence;
	private boolean hasRespondentEvidence;
	private boolean hasCounterCounterClaim;
	private boolean hasProposerAgentClaim;
	private boolean hasRespondentAgentClaim;

	/// 仲裁庭意见
	private boolean isForeignCase;
	private boolean hasContractRegulation;
	private boolean hasFocus;
	private boolean hasRequest;
	private boolean hasCounterRequest;
	private boolean isRespondentAbsent;

	/// 案情部分
	private List proposerText;
	private List replyText;
	private List counterClaimText;
	private List proposerEvidenceText;
	private List responderEvidenceText;
	private List counterCounterClaimText;
	private List proposerAgentClaimText;
	private List respondentAgentClaimText;

	/// 仲裁庭意见
	private List arbiPreStatementText;
	private List arbiOpFactText;
	private List foreignCaseLawText;
	private List contractRegulationText;
	private List focusText;
	private List requestText;
	private List counterRequestText;

	public static String RESP_ABSENT_RESULT_TEXT = "被申请人经合法通知无正当理由未到庭，亦未提交任何书面答辩意见或证据，视为自行放弃抗辩的权利，应自行承担由此引起的法律后果。";

	public Award() {
		/// Initialize all possible factors to false
		hasReply = false;
		hasCounterClaim = false;
		hasProposerEvidence = false;
		hasRespondentEvidence = false;
		hasCounterCounterClaim = false;
		hasProposerAgentClaim = false;
		hasRespondentAgentClaim = false;

		isForeignCase = false;
		hasContractRegulation = false;
		hasFocus = false;
		hasRequest = false;
		hasCounterRequest = false;
		isRespondentAbsent = false;
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

	public List getPartyList() {
		return partyList;
	}

	public void setPartyList(List partyList) {
		this.partyList = partyList;
	}

	public List getJianjieText() {
		return jianjieText;
	}

	public void setJianjieText(List jianjieText) {
		this.jianjieText = jianjieText;
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

	public List getFootText() {
		return footText;
	}

	public void setFootText(List footText) {
		this.footText = footText;
	}

	/* -----------------------------分------割------线----------------------------- */

	public boolean hasReply() {
		return hasReply;
	}

	public void setHasReply(boolean hasReply) {
		this.hasReply = hasReply;
	}

	public boolean hasCounterClaim() {
		return hasCounterClaim;
	}

	public void setHasCounterClaim(boolean hasCounterClaim) {
		this.hasCounterClaim = hasCounterClaim;
	}

	public boolean hasProposerEvidence() {
		return hasProposerEvidence;
	}

	public void setHasProposerEvidence(boolean hasProposerEvidence) {
		this.hasProposerEvidence = hasProposerEvidence;
	}

	public boolean hasRespondentEvidence() {
		return hasRespondentEvidence;
	}

	public void setHasRespondentEvidence(boolean hasRespondentEvidence) {
		this.hasRespondentEvidence = hasRespondentEvidence;
	}

	public boolean hasCounterCounterClaim() {
		return hasCounterCounterClaim;
	}

	public void setHasCounterCounterClaim(boolean hasCounterCounterClaim) {
		this.hasCounterCounterClaim = hasCounterCounterClaim;
	}

	public boolean hasProposerAgentClaim() {
		return hasProposerAgentClaim;
	}

	public void setHasProposerAgentClaim(boolean hasProposerAgentClaim) {
		this.hasProposerAgentClaim = hasProposerAgentClaim;
	}

	public boolean hasRespondentAgentClaim() {
		return hasRespondentAgentClaim;
	}

	public void setHasRespondentAgentClaim(boolean hasRespondentAgentClaim) {
		this.hasRespondentAgentClaim = hasRespondentAgentClaim;
	}

	public boolean isForeignCase() {
		return isForeignCase;
	}

	public void setForeignCase(boolean isForeignCase) {
		this.isForeignCase = isForeignCase;
	}

	public boolean hasContractRegulation() {
		return hasContractRegulation;
	}

	public void setHasContractRegulation(boolean hasContractRegulation) {
		this.hasContractRegulation = hasContractRegulation;
	}

	public boolean hasFocus() {
		return hasFocus;
	}

	public void setHasFocus(boolean hasFocus) {
		this.hasFocus = hasFocus;
	}

	public boolean hasRequest() {
		return hasRequest;
	}

	public void setHasRequest(boolean hasRequest) {
		this.hasRequest = hasRequest;
	}

	public boolean hasCounterRequest() {
		return hasCounterRequest;
	}

	public void setHasCounterRequest(boolean hasCounterRequest) {
		this.hasCounterRequest = hasCounterRequest;
	}

	public boolean isRespondentAbsent() {
		return isRespondentAbsent;
	}

	public void setRespondentAbsent(boolean isRespondentAbsent) {
		this.isRespondentAbsent = isRespondentAbsent;
	}

	/* -----------------------------分------割------线----------------------------- */
	public List getProposerText() {
		return proposerText;
	}

	public void setProposerText(List proposerText) {
		this.proposerText = proposerText;
	}

	public List getReplyText() {
		return replyText;
	}

	public void setReplyText(List replyText) {
		this.replyText = replyText;
	}

	public List getCounterClaimText() {
		return counterClaimText;
	}

	public void setCounterClaimText(List counterClaimText) {
		this.counterClaimText = counterClaimText;
	}

	public List getProposerEvidenceText() {
		return proposerEvidenceText;
	}

	public void setProposerEvidenceText(List proposerEvidenceText) {
		this.proposerEvidenceText = proposerEvidenceText;
	}

	public List getResponderEvidenceText() {
		return responderEvidenceText;
	}

	public void setResponderEvidenceText(List responderEvidenceText) {
		this.responderEvidenceText = responderEvidenceText;
	}

	public List getCounterCounterClaimText() {
		return counterCounterClaimText;
	}

	public void setCounterCounterClaimText(List counterCounterClaimText) {
		this.counterCounterClaimText = counterCounterClaimText;
	}

	public List getProposerAgentClaimText() {
		return proposerAgentClaimText;
	}

	public void setProposerAgentClaimText(List proposerAgentClaimText) {
		this.proposerAgentClaimText = proposerAgentClaimText;
	}

	public List getRespondentAgentClaimText() {
		return respondentAgentClaimText;
	}

	public void setRespondentAgentClaimText(List respondentAgentClaimText) {
		this.respondentAgentClaimText = respondentAgentClaimText;
	}

	public List getArbiPreStatementText() {
		return arbiPreStatementText;
	}

	public void setArbiPreStatementText(List arbiPreStatementText) {
		this.arbiPreStatementText = arbiPreStatementText;
	}

	public List getArbiOpFactText() {
		return arbiOpFactText;
	}

	public void setArbiOpFactText(List arbiOpFactText) {
		this.arbiOpFactText = arbiOpFactText;
	}

	public List getForeignCaseLawText() {
		return foreignCaseLawText;
	}

	public void setForeignCaseLawText(List foreignCaseLawText) {
		this.foreignCaseLawText = foreignCaseLawText;
	}

	public List getContractRegulationText() {
		return contractRegulationText;
	}

	public void setContractRegulationText(List contractRegulationText) {
		this.contractRegulationText = contractRegulationText;
	}

	public List getFocusText() {
		return focusText;
	}

	public void setFocusText(List focusText) {
		this.focusText = focusText;
	}

	public List getRequestText() {
		return requestText;
	}

	public void setRequestText(List requestText) {
		this.requestText = requestText;
	}

	public List getCounterRequestText() {
		return counterRequestText;
	}

	public void setCounterRequestText(List counterRequestText) {
		this.counterRequestText = counterRequestText;
	}
}
