package cn.com.xiaofabo.scia.aiawardcheck.fileprocessor;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import cn.com.xiaofabo.scia.aiawardcheck.entity.Award;
import cn.com.xiaofabo.scia.aiawardcheck.entity.Pair;
import cn.com.xiaofabo.scia.aiawardcheck.entity.Proposer;
import cn.com.xiaofabo.scia.aiawardcheck.entity.Respondent;
import cn.com.xiaofabo.scia.aiawardcheck.entity.Routine;

public class AwardReader extends DocReader {
	public static Logger logger = Logger.getLogger(AwardReader.class.getName());

	/// Regular expressions for reading award document
	private static final String REGEX_CASE_PROPOSER_TEXT_TITLE = "申请人的仲裁请求、事实及理由";
	private static final String REGEX_CASE_REPLY_TEXT_TITLE = ".*相关被申请人的主要答辩意见";
	private static final String REGEX_CASE_COUNTER_CLAIM_TEXT_TITLE = "）被申请人的仲裁反请求、事实及理由";
	private static final String REGEX_CASE_COUNTER_COUNTER_CLAIM_TEXT_TITLE = "）申请人就反请求的主要答辩意见";
	private static final String REGEX_CASE_PROPOSER_AGENT_TEXT_TITLE = ".*申请人代理人的主要代理意见";
	private static final String REGEX_CASE_RESPONDENT_AGENT_TEXT_TITLE = ".*被申请人代理人的主要代理意见";

	private static final String REGEX_ARBIOP_FACT_TEXT_TITLE = "仲裁庭认定的事实";
	private static final String REGEX_ARBIOP_FOREIGN_CASE_TEXT_TITLE = "关于本案法律适用问题";
	private static final String REGEX_ARBIOP_CONTRACT_REGULATION_TEXT_TITLE = "关于本案合同的效力";
	private static final String REGEX_ARBIOP_FOCUS_TEXT_TITLE = "关于本案的争议焦点";
	private static final String REGEX_ARBIOP_REQUEST_TEXT_TITLE = "关于申请人的仲裁请求";
	private static final String REGEX_ARBIOP_COUNTER_REQUEST_TEXT_TITLE = "关于被申请人的仲裁反请求";
	private static final String REGEX_ARBIOP_RESPONDENT_ABSENT_TEXT_TITLE = "被申请人缺席的法律后果";

	private final List<Proposer> proposerList;
	private final List<Respondent> respondentList;

	public AwardReader() {
		super();
		proposerList = new LinkedList();
		respondentList = new LinkedList();
	}

	public Award buildAward(String inputPath) throws IOException {
		readWordFile(inputPath);
		return buildAward();
	}

	public Award buildAward(File inputFile) throws IOException {
		readWordFile(inputFile);
		return buildAward();
	}

	private Award buildAward() throws IOException {
		String lines[] = docText.split("\\r?\\n");
		readProAndRes(lines);

		String dateText = "";
		String caseIdText = "";

		int routineTextLineStart = 0;
		int routineTextLineEnd = 0;
		List routineText = new LinkedList<String>();

		int caseTextLineStart = 0;
		int caseTextLineEnd = 0;
		List caseText = new LinkedList<String>();

		int arbiOpinionTextLineStart = 0;
		int arbiOpinionTextLineEnd = 0;
		List arbiOpinionText = new LinkedList<String>();

		int arbitramentTextLineStart = 0;
		int arbitramentTextLineEnd = 0;
		List arbitramentText = new LinkedList<String>();

		lines = docText.split("\\r?\\n");
		int startLineNum = 0, endLineNum = 0;
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i].trim();
			if (removeAllSpaces(line).equals("深圳")) {
				int lineIndex = i;
				while (lineIndex < lines.length) {
					String l = lines[lineIndex++];
					if (removeAllSpaces(l).matches("\\b.*年.*月.*日")) {
						dateText = l;
						break;
					}
				}
			}

			if (line.matches(".*深国仲裁.*号")) {
				caseIdText = line;
				routineTextLineStart = i + 1;
			}

			if (removeAllSpaces(line).contains("一、案情")) {
				routineTextLineEnd = i - 1;
				caseTextLineStart = i + 1;

				for (int index = routineTextLineStart; index < routineTextLineEnd + 1; ++index) {
					if (!lines[index].isEmpty()) {
						routineText.add(lines[index]);
					}
				}
			}

			if (removeAllSpaces(line).contains("仲裁庭意见")) {
				caseTextLineEnd = i - 1;
				arbiOpinionTextLineStart = i + 1;

				for (int index = caseTextLineStart; index < caseTextLineEnd + 1; ++index) {
					if (!lines[index].isEmpty()) {
						caseText.add(lines[index]);
					}
				}
			}

			if (removeAllSpaces(line).contains("三、裁决")) {
				arbiOpinionTextLineEnd = i - 1;
				arbitramentTextLineStart = i + 1;

				for (int index = arbiOpinionTextLineStart; index < arbiOpinionTextLineEnd + 1; ++index) {
					if (!lines[index].isEmpty()) {
						arbiOpinionText.add(lines[index]);
					}
				}
			}

			if (removeAllSpaces(line).contains("（紧接下一页）")) {
				arbitramentTextLineEnd = i - 1;

				for (int index = arbitramentTextLineStart; index < arbitramentTextLineEnd + 1; ++index) {
					if (!lines[index].isEmpty()) {
						arbitramentText.add(lines[index]);
					}
				}
			}
		}

		if (arbitramentTextLineEnd == 0) {
			arbitramentTextLineEnd = lines.length - 1;
			for (int index = arbitramentTextLineStart; index < arbitramentTextLineEnd + 1; ++index) {
				if (!lines[index].isEmpty()) {
					arbitramentText.add(lines[index]);
				}
			}
		}

		Award award = new Award();
		award.setDateText(dateText);
		award.setCaseIdText(caseIdText);
		award.setProposerList(proposerList);
		award.setRespondentList(respondentList);
		award.setRoutineText(routineText);

		/// 一、案情
		award = divideCaseText(award, caseText);
		/// 二、仲裁庭意见
		award = divideArbiOpinionText(award, arbiOpinionText);
		/// 三、裁 决
		award.setArbitramentText(arbitramentText);

		return award;
	}

	private void readProAndRes(String[] lines) throws IOException {

		List<Integer> proposerChunkStartIdx = new LinkedList();
		List<Integer> respondentChunkStartIdx = new LinkedList();

		int lastIdx = 0;
		for (int lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
			String line = lines[lineIndex].trim();
			// String compressedLine = removeAllSpaces(line);
			Pattern pattern = Pattern.compile("^(第.)?申\\s*请\\s*人");
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				proposerChunkStartIdx.add(lineIndex);
			}
			pattern = Pattern.compile("^(第.)?被\\s*申\\s*请\\s*人");
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				respondentChunkStartIdx.add(lineIndex);
			}
			pattern = Pattern.compile("^深\\s*圳$");
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				lastIdx = lineIndex;
				break;
			}
		}

		if (proposerChunkStartIdx.isEmpty()) {
			logger.warn("Did not find proposer!");
		}
		if (respondentChunkStartIdx.isEmpty()) {
			logger.warn("Did not find respondent!");
		}
		if (lastIdx == 0) {
			/// First page in routine document is not correctly formatted!!!
		}

		List<String> proposerChunk = new LinkedList();
		List<String> respondentChunk = new LinkedList();

		for (int i = 0; i < proposerChunkStartIdx.size(); ++i) {
			int startIdx = proposerChunkStartIdx.get(i);
			int endIdx = 0;
			if (i != proposerChunkStartIdx.size() - 1) {
				endIdx = proposerChunkStartIdx.get(i + 1);
			} else {
				endIdx = respondentChunkStartIdx.get(0) - 1;
			}
			proposerChunk.add(combineLines(lines, startIdx, endIdx));
		}

		for (int i = 0; i < respondentChunkStartIdx.size(); ++i) {
			int startIdx = respondentChunkStartIdx.get(i);
			int endIdx = 0;
			if (i != respondentChunkStartIdx.size() - 1) {
				endIdx = respondentChunkStartIdx.get(i + 1);
			} else {
				endIdx = lastIdx - 1;
			}
			respondentChunk.add(combineLines(lines, startIdx, endIdx));
		}

		for (int i = 0; i < proposerChunk.size(); ++i) {
			String pChunk = proposerChunk.get(i);
			proposerList.add(createProposer(pChunk));
		}

		for (int i = 0; i < respondentChunk.size(); ++i) {
			String rChunk = respondentChunk.get(i);
			respondentList.add(createRespondent(rChunk));
		}

		/// Error handling
		if (proposerList.isEmpty()) {
		}
		if (respondentList.isEmpty()) {
		}
	}

	private Proposer createProposer(String pChunk) {
		pChunk = pAndrProcess(pChunk);
		String plines[] = pChunk.split("\\r?\\n");
		List<Pair> proposerPairList = new LinkedList();
		int currentPairIndex = -1;
		for (int pLineIndex = 0; pLineIndex < plines.length; ++pLineIndex) {
			String rline = plines[pLineIndex].trim();
			if (!rline.contains("：") && currentPairIndex != -1) {
				String value = rline;
				proposerPairList.get(currentPairIndex)
						.setValue(proposerPairList.get(currentPairIndex).getValue() + "\n" + value);
			}else {
				String key = rline.substring(0, rline.indexOf("："));
				String value = rline.substring(rline.indexOf("：") + 1);
				proposerPairList.add(new Pair(key, value));
				++currentPairIndex;
			}
		}

		Proposer pro = new Proposer();
		String proposer = "";
		String id = "";
		String address = "";
		String representative = "";
		String agency = "";
		String type = "";

		for (int i = 0; i < proposerPairList.size(); ++i) {
			String key = proposerPairList.get(i).getKey();
			String value = proposerPairList.get(i).getValue();
			String keyNoSpace = removeAllSpaces(key);

			if (keyNoSpace == null || keyNoSpace.isEmpty()) {
				continue;
			}

			if (keyNoSpace.contains("申请人")) {
				proposer = value;
			}

			if (keyNoSpace.equals("统一社会信用代码")) {
				type = "COM";
				id = value;
			}

			if (keyNoSpace.equals("公民身份证号码") || keyNoSpace.equals("公民身份号码")) {
				type = "IND";
				id = value;
			}

			if (keyNoSpace.equals("住址") || keyNoSpace.equals("地址") || keyNoSpace.equals("住所")
					|| keyNoSpace.equals("身份证地址")) {
				address = value;
			}
			if (keyNoSpace.equals("法定代表人") || keyNoSpace.equals("负责人")) {
				representative = value;
			}
			if (keyNoSpace.equals("代理人")) {
				agency = value;
			}
			
		}
		pro.setProposer(proposer);
		pro.setId(id);
		pro.setAddress(address);
		pro.setRepresentative(representative);
		pro.setAgency(agency);
		pro.setType(type);
		return pro;
	}

	private Respondent createRespondent(String rChunk) {
		rChunk = pAndrProcess(rChunk);
		String rlines[] = rChunk.split("\\r?\\n");
		List<Pair> respondentPairList = new LinkedList();
		int currentPairIndex = -1;
		for (int rLineIndex = 0; rLineIndex < rlines.length; ++rLineIndex) {
			String rline = rlines[rLineIndex].trim();
			if (!rline.contains("：") && currentPairIndex != -1) {
				String value = rline;
				respondentPairList.get(currentPairIndex)
						.setValue(respondentPairList.get(currentPairIndex).getValue() + "\n" + value);
			}else {
				String key = rline.substring(0, rline.indexOf("："));
				String value = rline.substring(rline.indexOf("：") + 1);
				respondentPairList.add(new Pair(key, value));
				++currentPairIndex;
			}
		}

		Respondent res = new Respondent();
		String respondent = "";
		String id = "";
		String address = "";
		String representative = "";
		String agency = "";
		String type = "";
		for (int i = 0; i < respondentPairList.size(); ++i) {
			String key = respondentPairList.get(i).getKey();
			String value = respondentPairList.get(i).getValue();
			String keyNoSpace = removeAllSpaces(key);

			if (keyNoSpace == null || keyNoSpace.isEmpty()) {
				continue;
			}

			if (keyNoSpace.contains("被申请人")) {
				respondent = value;
			}

			if (keyNoSpace.equals("统一社会信用代码")) {
				type = "COM";
				id = value;
			}

			if (keyNoSpace.equals("公民身份证号码") || keyNoSpace.equals("公民身份号码")) {
				type = "IND";
				id = value;
			}

			if (keyNoSpace.equals("住址") || keyNoSpace.equals("地址") || keyNoSpace.equals("住所")
					|| keyNoSpace.equals("身份证地址")) {
				address = value;
			}
			if (keyNoSpace.equals("法定代表人") || keyNoSpace.equals("负责人")) {
				representative = value;
			}
			if (keyNoSpace.equals("代理人")) {
				agency = value;
			}
		}
		
		res.setRespondentName(respondent);
		res.setId(id);
		res.setAddress(address);
		res.setRepresentative(representative);
		res.setAgency(agency);
		res.setType(type);

		return res;
	}

	private String pAndrProcess(String proposerStr) {
		proposerStr = proposerStr.replaceAll("，", "   ");
		proposerStr = proposerStr.replaceAll(",", "   ");
		proposerStr = proposerStr.replaceAll("。", "   ");
		if (proposerStr.contains("性别") && !proposerStr.contains("性别：")) {
			proposerStr = proposerStr.replaceAll("性别", "性别：");
		}
		if (proposerStr.contains("身份证号码") && !proposerStr.contains("身份证号码：")) {
			proposerStr = proposerStr.replaceAll("身份证号码", "身份证号码：");
		}
		if (proposerStr.contains("身份号码") && !proposerStr.contains("身份号码：")) {
			proposerStr = proposerStr.replaceAll("身份号码", "身份号码：");
		}
		if (proposerStr.contains("住址") && !proposerStr.contains("住址：")) {
			proposerStr = proposerStr.replaceAll("住址", "住址：");
		}
		return proposerStr;
	}

	private Award divideCaseText(Award award, List caseText) {
		/// Further divide case text

		/// proposerTextStart, replyTextStart, counterClaimTextStart,
		/// counterCounterClaimTextStart, proposerAgentTextStart,
		/// respondentAgentTextStart
		int[] startPos = { 0, 0, 0, 0, 0, 0 };

		/// proposerTextEnd, replyTextEnd, counterClaimTextEnd,
		/// counterCounterClaimTextEnd, proposerAgentTextEnd, respondentAgentTextEnd
		int[] endPos = { 0, 0, 0, 0, 0, 0 };

		for (int i = 0; i < caseText.size(); ++i) {
			String line = (String) caseText.get(i);

			Pattern pattern = Pattern.compile(REGEX_CASE_PROPOSER_TEXT_TITLE);
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				startPos[0] = i + 1;
			}

			pattern = Pattern.compile(REGEX_CASE_REPLY_TEXT_TITLE);
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				award.setHasReply(true);
				startPos[1] = i + 1;
			}

			pattern = Pattern.compile(REGEX_CASE_COUNTER_CLAIM_TEXT_TITLE);
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				award.setHasCounterClaim(true);
				startPos[2] = i + 1;
			}

			pattern = Pattern.compile(REGEX_CASE_COUNTER_COUNTER_CLAIM_TEXT_TITLE);
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				award.setHasCounterCounterClaim(true);
				startPos[3] = i + 1;
			}

			pattern = Pattern.compile(REGEX_CASE_PROPOSER_AGENT_TEXT_TITLE);
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				award.setHasProposerAgentClaim(true);
				startPos[4] = i + 1;
			}

			pattern = Pattern.compile(REGEX_CASE_RESPONDENT_AGENT_TEXT_TITLE);
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				award.setHasRespondentAgentClaim(true);
				startPos[5] = i + 1;
			}
		}

		for (int i = 0; i < startPos.length - 1; ++i) {
			if (startPos[i] == 0) {
				continue;
			}
			int startPosIdx = i + 1;
			while (startPos[startPosIdx] == 0) {
				if (startPosIdx == startPos.length - 1) {
					break;
				}
				++startPosIdx;
			}
			endPos[i] = startPosIdx == startPos.length - 1 ? caseText.size() : startPos[startPosIdx] - 1;
		}

		endPos[startPos.length - 1] = startPos[startPos.length - 1] == 0 ? 0 : caseText.size();

		/// 案情部分
		award.setProposerText(caseText.subList(startPos[0], endPos[0]));
		award.setReplyText(caseText.subList(startPos[1], endPos[1]));
		award.setCounterClaimText(caseText.subList(startPos[2], endPos[2]));
		award.setCounterCounterClaimText(caseText.subList(startPos[3], endPos[3]));
		award.setProposerAgentClaimText(caseText.subList(startPos[4], endPos[4]));
		award.setRespondentAgentClaimText(caseText.subList(startPos[5], endPos[5]));
		return award;
	}

	private Award divideArbiOpinionText(Award award, List arbiOpinionText) {
		/// Further divide arbitration opinion text

		int[] startPos = { 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] endPos = { 0, 0, 0, 0, 0, 0, 0, 0 };

		for (int i = 0; i < arbiOpinionText.size(); ++i) {
			String line = (String) arbiOpinionText.get(i);

			startPos[0] = 0;

			Pattern pattern = Pattern.compile(REGEX_ARBIOP_FACT_TEXT_TITLE);
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				startPos[1] = i + 1;
			}

			pattern = Pattern.compile(REGEX_ARBIOP_FOREIGN_CASE_TEXT_TITLE);
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				award.setForeignCase(true);
				startPos[2] = i + 1;
			}

			pattern = Pattern.compile(REGEX_ARBIOP_CONTRACT_REGULATION_TEXT_TITLE);
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				award.setHasContractRegulation(true);
				startPos[3] = i + 1;
			}

			pattern = Pattern.compile(REGEX_ARBIOP_FOCUS_TEXT_TITLE);
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				award.setHasFocus(true);
				startPos[4] = i + 1;
			}

			pattern = Pattern.compile(REGEX_ARBIOP_REQUEST_TEXT_TITLE);
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				award.setHasRequest(true);
				startPos[5] = i + 1;
			}

			pattern = Pattern.compile(REGEX_ARBIOP_COUNTER_REQUEST_TEXT_TITLE);
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				award.setHasCounterRequest(true);
				startPos[6] = i + 1;
			}

			pattern = Pattern.compile(REGEX_ARBIOP_RESPONDENT_ABSENT_TEXT_TITLE);
			matcher = pattern.matcher(line);
			if (matcher.find()) {
				award.setRespondentAbsent(true);
				startPos[7] = i + 1;
			}
		}

		for (int i = 0; i < startPos.length - 1; ++i) {
			if (startPos[i] == 0) {
				continue;
			}
			int startPosIdx = i + 1;
			while (startPos[startPosIdx] == 0) {
				if (startPosIdx == startPos.length - 1) {
					break;
				}
				++startPosIdx;
			}
			endPos[i] = startPosIdx == startPos.length - 1 ? arbiOpinionText.size() : startPos[startPosIdx] - 1;
		}

		endPos[startPos.length - 1] = startPos[startPos.length - 1] == 0 ? 0 : arbiOpinionText.size();

		award.setArbiPreStatementText(arbiOpinionText.subList(startPos[0], endPos[0]));
		award.setArbiOpFactText(arbiOpinionText.subList(startPos[1], endPos[1]));
		award.setForeignCaseLawText(arbiOpinionText.subList(startPos[2], endPos[2]));
		award.setContractRegulationText(arbiOpinionText.subList(startPos[3], endPos[3]));
		award.setFocusText(arbiOpinionText.subList(startPos[4], endPos[4]));
		award.setRequestText(arbiOpinionText.subList(startPos[5], endPos[5]));
		award.setCounterRequestText(arbiOpinionText.subList(startPos[6], endPos[6]));

		return award;
	}
}
