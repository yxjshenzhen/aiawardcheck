package cn.com.xiaofabo.scia.aiawardcheck.fileprocessor;

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
	private final List<Proposer> proposerList;
	private final List<Respondent> respondentList;

	public AwardReader() {
		super();
		proposerList = new LinkedList();
		respondentList = new LinkedList();
	}

	public Award buildArbitration(String inputPath) throws IOException {
		readWordFile(inputPath);

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
			
			if (removeAllSpaces(line).contains("二、仲裁庭意见")) {
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
		
		Award arbitration = new Award();
		arbitration.setDateText(dateText);
		arbitration.setCaseIdText(caseIdText);
		arbitration.setProposerList(proposerList);
		arbitration.setRespondentList(respondentList);
		arbitration.setRoutineText(routineText);
		arbitration.setCaseText(caseText);
		arbitration.setArbiOpinionText(arbiOpinionText);
		arbitration.setArbitramentText(arbitramentText);

		return arbitration;
	}

	private void readProAndRes(String[] lines) throws IOException {

		List<Integer> proposerChunkStartIdx = new LinkedList();
		List<Integer> respondentChunkStartIdx = new LinkedList();

		int lastIdx = 0;
		for (int lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
			String line = lines[lineIndex].trim();
			// String compressedLine = removeAllSpaces(line);
			Pattern pattern = Pattern.compile("^(第[一二三四五六])?申\\s*请\\s*人");
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				proposerChunkStartIdx.add(lineIndex);
			}
			pattern = Pattern.compile("^(第[一二三四五六])?被\\s*申\\s*请\\s*人");
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
			/// Did not find any proposer in routine document!!!
		}
		if (respondentChunkStartIdx.isEmpty()) {
			/// Did not find any respondent in routine document!!!
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
		for (int pLineIndex = 0; pLineIndex < plines.length; ++pLineIndex) {
			String pline = plines[pLineIndex].trim();
			List<Integer> indices = new LinkedList();
			int index = 0;
			while ((index = pline.indexOf("：", index)) != -1) {
				indices.add(index++);
			}

			int keyStartIdx = 0;
			int keyEndIdx = 0;
			int valueStartIdx = 0;
			int valueEndIdx = 0;
			for (int i = 0; i < indices.size(); ++i) {
				keyEndIdx = indices.get(i);
				String key = pline.substring(keyStartIdx, keyEndIdx);
				valueStartIdx = keyEndIdx + 1;
				if ((i + 1) != indices.size()) {
					int tmpIdx = indices.get(i + 1);
					valueEndIdx = pline.lastIndexOf(" ", tmpIdx);
				} else {
					valueEndIdx = -1;
				}
				String value = valueEndIdx == -1 ? pline.substring(valueStartIdx)
						: pline.substring(valueStartIdx, valueEndIdx);
				keyStartIdx = valueEndIdx + 1;
				proposerPairList.add(new Pair(key.trim(), value.trim()));
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

			if (keyNoSpace.contains("申请人")) {
				proposer = value;
			}

			if (keyNoSpace.equals("统一社会信用代码")) {
				type = "COM";
				id = value;
			}

			if (keyNoSpace.equals("公民身份证号码")) {
				type = "IND";
				id = value;
			}

			if (keyNoSpace.equals("住址") || keyNoSpace.equals("地址") || keyNoSpace.equals("住所")) {
				address = value;
			}
			if (keyNoSpace.equals("法定代表人")) {
				representative = value;
			}
			if (keyNoSpace.equals("代理人")) {
				agency = value;
			}
			pro.setProposer(proposer);
			pro.setId(id);
			pro.setAddress(address);
			pro.setRepresentative(representative);
			pro.setAgency(agency);
			pro.setType(type);
		}
		return pro;
	}

	private Respondent createRespondent(String rChunk) {
		rChunk = pAndrProcess(rChunk);
		String rlines[] = rChunk.split("\\r?\\n");
		List<Pair> respondentPairList = new LinkedList();
		for (int rLineIndex = 0; rLineIndex < rlines.length; ++rLineIndex) {
			String rline = rlines[rLineIndex].trim();
			List<Integer> indices = new LinkedList();
			int index = 0;
			while ((index = rline.indexOf("：", index)) != -1) {
				indices.add(index++);
			}

			int keyStartIdx = 0;
			int keyEndIdx = 0;
			int valueStartIdx = 0;
			int valueEndIdx = 0;
			for (int i = 0; i < indices.size(); ++i) {
				keyEndIdx = indices.get(i);
				String key = rline.substring(keyStartIdx, keyEndIdx);
				valueStartIdx = keyEndIdx + 1;
				if ((i + 1) != indices.size()) {
					int tmpIdx = indices.get(i + 1);
					valueEndIdx = rline.lastIndexOf(" ", tmpIdx);
				} else {
					valueEndIdx = -1;
				}
				String value = valueEndIdx == -1 ? rline.substring(valueStartIdx)
						: rline.substring(valueStartIdx, valueEndIdx);
				keyStartIdx = valueEndIdx + 1;
				respondentPairList.add(new Pair(key.trim(), value.trim()));
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

			if (keyNoSpace.contains("被申请人")) {
				respondent = value;
			}

			if (keyNoSpace.equals("统一社会信用代码")) {
				type = "COM";
				id = value;
			}

			if (keyNoSpace.equals("公民身份证号码")) {
				type = "IND";
				id = value;
			}
			if (keyNoSpace.equals("住址") || keyNoSpace.equals("地址") || keyNoSpace.equals("住所")) {
				address = value;
			}
			if (keyNoSpace.equals("法定代表人")) {
				representative = value;
			}
			if (keyNoSpace.equals("代理人")) {
				agency = value;
			}
			res.setRespondentName(respondent);
			res.setId(id);
			res.setAddress(address);
			res.setRepresentative(representative);
			res.setAgency(agency);
			res.setType(type);
		}

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
}
