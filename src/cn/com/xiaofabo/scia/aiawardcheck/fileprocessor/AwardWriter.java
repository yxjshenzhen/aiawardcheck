package cn.com.xiaofabo.scia.aiawardcheck.fileprocessor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocDefaults;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPrDefault;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;

import cn.com.xiaofabo.scia.aiawardcheck.entity.Award;
import cn.com.xiaofabo.scia.aiawardcheck.entity.Proposer;
import cn.com.xiaofabo.scia.aiawardcheck.entity.Respondent;
import cn.com.xiaofabo.scia.aiawardcheck.entity.Routine;

public class AwardWriter extends DocWriter {

	private final XWPFDocument awardDoc;
	private final String outFileUrl;

	public AwardWriter(String outFileUrl) {
		super();
		this.outFileUrl = outFileUrl;
		awardDoc = new XWPFDocument();
	}

	public XWPFDocument generateAwardDoc(Award award) {
		pageSetup();
		generateCoverPage(award);
		generateContentPages(award);

		try {
			FileOutputStream fos = new FileOutputStream(outFileUrl);
			awardDoc.write(fos);
			fos.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		return awardDoc;
	}

	private void pageSetup() {
		CTDocument1 document = awardDoc.getDocument();

		CTBody body = document.getBody();

		if (!body.isSetSectPr()) {
			body.addNewSectPr();
		}
		CTSectPr section = body.getSectPr();

		if (!section.isSetPgSz()) {
			section.addNewPgSz();
		}

		CTPageSz pageSize = section.getPgSz();

		pageSize.setW(PAGE_A4_WIDTH);
		pageSize.setH(PAGE_A4_HEIGHT);
		pageSize.setOrient(STPageOrientation.PORTRAIT);

		CTSectPr sectPr = body.addNewSectPr();
		CTPageMar pageMar = sectPr.addNewPgMar();
		pageMar.setTop(PAGE_MARGIN_TOP);
		pageMar.setBottom(PAGE_MARGIN_BOTTOM);
		pageMar.setLeft(PAGE_MARGIN_LEFT);
		pageMar.setRight(PAGE_MARGIN_RIGHT);
		pageMar.setHeader(PAGE_MARGIN_HEADER);
		pageMar.setFooter(PAGE_MARGIN_FOOTER);

		XWPFStyles styles = awardDoc.createStyles();
		CTFonts fonts = CTFonts.Factory.newInstance();
		fonts.setEastAsia(FONT_FAMILY_SONG);
		fonts.setAscii(FONT_FAMILY_TIME_NEW_ROMAN);
		styles.setDefaultFonts(fonts);

		CTStyles ctStyles = CTStyles.Factory.newInstance();

		if (!ctStyles.isSetDocDefaults()) {
			ctStyles.addNewDocDefaults();
		}

		CTDocDefaults ctDocDefaults = ctStyles.getDocDefaults();

		if (!ctDocDefaults.isSetRPrDefault()) {
			ctDocDefaults.addNewRPrDefault();
		}

		CTRPrDefault ctRprDefault = ctDocDefaults.getRPrDefault();

		if (!ctRprDefault.isSetRPr()) {
			ctRprDefault.addNewRPr();
		}

		CTRPr ctRpr = ctRprDefault.getRPr();

		if (!ctRpr.isSetSz()) {
			ctRpr.addNewSz();
		}

		if (!ctRpr.isSetSzCs()) {
			ctRpr.addNewSzCs();
		}

		CTHpsMeasure sz = ctRpr.getSz();
		sz.setVal(DEFAULT_FONT_SIZE_HALF_16);

		CTHpsMeasure szCs = ctRpr.getSzCs();
		szCs.setVal(DEFAULT_FONT_SIZE_HALF_16);

		styles.setStyles(ctStyles);

		generateFooter();
	}

	private void generateCoverPage(Award award) {
		XWPFParagraph p1 = awardDoc.createParagraph();
		p1.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun p1r1 = p1.createRun();
		p1r1.setFontFamily(FONT_FAMILY_SONG);
		p1r1.setFontSize(CN_FONT_SIZE_XIAO_YI);
		p1r1.setText("深 圳 国 际 仲 裁 院\n");

		XWPFParagraph p2 = awardDoc.createParagraph();
		p2.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun p2r1 = p2.createRun();
		p2r1.setFontFamily(FONT_FAMILY_SONG);
		p2r1.setFontSize(CN_FONT_SIZE_XIAO_YI);
		p2r1.addBreak();
		p2r1.setText("裁  决  书");
		p2r1.addBreak();
		p2r1.addBreak();
		
        for (int i = 0; i < award.getProposerList().size(); ++i) {
            Proposer pro = (Proposer) award.getProposerList().get(i);
            addProposerTable(pro, i + 1, award.getProposerList().size());
            breakLine();
        }

        for (int i = 0; i < award.getRespondentList().size(); ++i) {
            Respondent res = (Respondent) award.getRespondentList().get(i);
            addRespondentTable(res, i + 1, award.getRespondentList().size());
            breakLine();
        }

		XWPFParagraph p5 = awardDoc.createParagraph();
		p5.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun p5r1 = p5.createRun();
		p5r1.setFontFamily(FONT_FAMILY_FANGSONG);
		p5r1.setFontSize(CN_FONT_SIZE_XIAO_ER);
		p5r1.addBreak();
		p5r1.setText("深   圳");

		XWPFParagraph p6 = awardDoc.createParagraph();
		p6.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun p6r1 = p6.createRun();
		p6r1.setFontFamily(FONT_FAMILY_FANGSONG);
		p6r1.setFontSize(CN_FONT_SIZE_XIAO_ER);
		p6r1.addBreak();
		p6r1.setText(award.getDateText());
		p6r1.addBreak(BreakType.PAGE);
	}

	private void generateContentPages(Award award) {
		XWPFParagraph p1 = awardDoc.createParagraph();
		p1.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun p1r1 = p1.createRun();
		p1r1.setFontFamily(FONT_FAMILY_SONG);
		p1r1.setFontSize(CN_FONT_SIZE_XIAO_YI);
		p1r1.setText("裁    决    书\n");

		XWPFParagraph p3 = awardDoc.createParagraph();
		p3.setAlignment(ParagraphAlignment.RIGHT);
		XWPFRun p3r1 = p3.createRun();
		p3r1.setFontFamily(FONT_FAMILY_FANGSONG);
		p3r1.setFontSize(CN_FONT_SIZE_SAN);
		p3r1.addBreak();
		p3r1.setText(award.getCaseIdText());
		p3r1.addBreak();

		addNormalTextParagraphs(award.getRoutineText(), 0, 0);

		
		/* -----------------------------分------割------线----------------------------- */
		addSubTitle("一、案    情");
		int caseContentSubSectionIndex = 1;

		addTitleTextParagraph("（" + DocUtil.numberToCN(caseContentSubSectionIndex++) + "）申请人的仲裁请求、事实及理由", 0);
		/// 申请人称
		addNormalTextParagraph("申请人称：", 0);
		addNormalTextParagraphs(award.getProposerText(), 0, 1);

		/// 被申请人答辩
		if (award.hasReply()) {
			addTitleTextParagraph("（" + DocUtil.numberToCN(caseContentSubSectionIndex++) + "）被申请人的主要答辩意见", 0);
			addNormalTextParagraphs(award.getReplyText(), 0, 1);
		}

		/// 被申请人的仲裁反请求、事实及理由
		if (award.hasCounterClaim()) {
			addTitleTextParagraph("（" + DocUtil.numberToCN(caseContentSubSectionIndex++) + "）被申请人的仲裁反请求、事实及理由", 0);
			addNormalTextParagraphs(award.getCounterClaimText(), 0, 1);
		}

		/// 申请人就反请求的主要答辩意见
		if (award.hasCounterCounterClaim()) {
			addTitleTextParagraph("（" + DocUtil.numberToCN(caseContentSubSectionIndex++) + "）申请人就反请求的主要答辩意见", 0);
			addNormalTextParagraphs(award.getCounterCounterClaimText(), 0, 1);
		}

		/// 申请人代理人的主要意见
		if (award.hasProposerAgentClaim()) {
			addTitleTextParagraph("（" + DocUtil.numberToCN(caseContentSubSectionIndex++) + "）申请人代理人的主要代理意见", 0);
			addNormalTextParagraphs(award.getProposerAgentClaimText(), 0, 1);
		}

		/// 被申请人代理人的主要意见
		if (award.hasRespondentAgentClaim()) {
			addTitleTextParagraph("（" + DocUtil.numberToCN(caseContentSubSectionIndex++) + "）被申请人代理人的主要代理意见", 0);
			addNormalTextParagraphs(award.getRespondentAgentClaimText(), 0, 1);
		}

		/* -----------------------------分------割------线----------------------------- */
		/// 仲裁庭意见
		addSubTitle("二、仲裁庭意见");
		addNormalTextParagraphs(award.getArbiPreStatementText(), 0, 1);

		int arbiOpSubSectionIndex = 1;

		/// 仲裁庭查明及认定的事实
		addTitleTextParagraph("（" + DocUtil.numberToCN(arbiOpSubSectionIndex++) + "）仲裁庭查明及认定的事实", 0);
		addNormalTextParagraphs(award.getArbiOpFactText(), 0, 1);

		/// 关于本案法律适用问题（涉外案件）
		if (award.isForeignCase()) {
			addTitleTextParagraph("（" + DocUtil.numberToCN(arbiOpSubSectionIndex++) + "）关于本案法律适用问题（涉外案件）", 0);
			addNormalTextParagraphs(award.getForeignCaseLawText(), 0, 1);
		}

		/// 关于本案合同的效力
		if (award.hasContractRegulation()) {
			addTitleTextParagraph("（" + DocUtil.numberToCN(arbiOpSubSectionIndex++) + "）关于本案合同的效力", 0);
			addNormalTextParagraphs(award.getContractRegulationText(), 0, 1);
		}

		/// 关于本案的争议焦点
		if (award.hasFocus()) {
			addTitleTextParagraph("（" + DocUtil.numberToCN(arbiOpSubSectionIndex++) + "）关于本案的争议焦点", 0);
			addNormalTextParagraphs(award.getFocusText(), 0, 1);
		}

		/// 关于申请人的仲裁请求
		if (award.hasRequest()) {
			addTitleTextParagraph("（" + DocUtil.numberToCN(arbiOpSubSectionIndex++) + "）关于申请人的仲裁请求", 0);
			addNormalTextParagraphs(award.getRequestText(), 0, 1);
		}

		/// 关于被申请人的仲裁反请求
		if (award.hasCounterRequest()) {
			addTitleTextParagraph("（" + DocUtil.numberToCN(arbiOpSubSectionIndex++) + "）关于被申请人的仲裁反请求", 0);
			addNormalTextParagraphs(award.getCounterRequestText(), 0, 1);
		}
		
		/// 被申请人缺席的法律后果
		if (award.isRespondentAbsent()) {
			addTitleTextParagraph("（" + DocUtil.numberToCN(arbiOpSubSectionIndex++) + "）被申请人缺席的法律后果", 0);
			addNormalTextParagraphs(Award.RESP_ABSENT_RESULT_TEXT, 0, 1);
		}

		/* -----------------------------分------割------线----------------------------- */
		/// 裁决
		addSubTitle("三、裁决");
		addNormalTextParagraphs(award.getArbitramentText(), 0, 1);
	}

	private void addTextParagraph(String str, int emptyLineAfter, boolean bold) {
		XWPFParagraph paragraph = awardDoc.createParagraph();

		CTPPr ppr = paragraph.getCTP().getPPr();
		if (ppr == null) {
			ppr = paragraph.getCTP().addNewPPr();
		}
		CTSpacing spacing = ppr.isSetSpacing() ? ppr.getSpacing() : ppr.addNewSpacing();
		spacing.setBefore(BigInteger.valueOf(0L));
		spacing.setAfter(BigInteger.valueOf(0L));
		spacing.setLineRule(STLineSpacingRule.EXACT);
		spacing.setLine(TEXT_LINE_SPACING);

		paragraph.setAlignment(ParagraphAlignment.LEFT);
		paragraph.setFirstLineIndent(CN_FONT_SIZE_SAN * 2 * 20);
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(FONT_FAMILY_FANGSONG);
		run.getCTR().getRPr().getRFonts().setAscii(FONT_FAMILY_TIME_NEW_ROMAN);
		run.getCTR().getRPr().getRFonts().setHAnsi(FONT_FAMILY_TIME_NEW_ROMAN);
		run.getCTR().getRPr().getRFonts().setEastAsia(FONT_FAMILY_FANGSONG);
		run.setFontSize(CN_FONT_SIZE_SAN);
		run.setBold(bold);
		run.setText(str);
		for (int i = 0; i < emptyLineAfter; ++i) {
			run.addBreak();
		}
	}

	private void addNormalTextParagraph(String str, int emptyLineAfter) {
		addTextParagraph(str, emptyLineAfter, false);
	}

	private void addNormalTextParagraphs(String str, int emptyLineInBetween, int emptyLineAfter) {
		String lines[] = str.split("\\r?\\n");
		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i].trim();
			if (line != null && !line.isEmpty()) {
				addTextParagraph(line, emptyLineInBetween, false);
			}
		}
		for (int i = 0; i < emptyLineAfter; ++i) {
			addTextParagraph("", emptyLineAfter - 1, false);
		}
	}

	private void addNormalTextParagraphs(List strList, int emptyLineInBetween, int emptyLineAfter) {
		for (int i = 0; i < strList.size(); ++i) {
			String str = ((String) strList.get(i)).trim();
			if (str != null && !str.isEmpty()) {
				addTextParagraph(str, emptyLineInBetween, false);
			}
		}
		for (int i = 0; i < emptyLineAfter; ++i) {
			addTextParagraph("", emptyLineAfter - 1, false);
		}
	}

	private void addTitleTextParagraph(String str, int emptyLineAfter) {
		addTextParagraph(str, emptyLineAfter, true);
	}

	private void addSubTitle(String str) {
		XWPFParagraph paragraph = awardDoc.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(FONT_FAMILY_HEITI);
		run.setFontSize(CN_FONT_SIZE_ER);
		run.addBreak();
		run.setText(str);
		run.addBreak();
		run.addBreak();
	}

	private void generateFooter() {
		XWPFParagraph paragraph = awardDoc.createParagraph();
		XWPFFooter footer;
		footer = awardDoc.createFooter(HeaderFooterType.FIRST);
		paragraph = footer.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);

		footer = awardDoc.createFooter(HeaderFooterType.DEFAULT);

		paragraph = footer.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);

		// paragraph.getCTP().addNewR().addNewRPr().addNewSz().setVal(BigInteger.valueOf(18));
		//
		XWPFRun run = paragraph.createRun();
		run.getCTR().addNewFldChar().setFldCharType(STFldCharType.BEGIN);
		run = paragraph.createRun();
		run.setFontSize(CN_FONT_SIZE_XIAO_WU);
		run.getCTR().addNewInstrText().setStringValue("PAGE \\* MERGEFORMAT");
		run = paragraph.createRun();
		run.getCTR().addNewFldChar().setFldCharType(STFldCharType.SEPARATE);
		run = paragraph.createRun();
		run.getCTR().addNewRPr().addNewNoProof();
		run = paragraph.createRun();
		run.getCTR().addNewFldChar().setFldCharType(STFldCharType.END);

		// CTP ctp = paragraph.getCTP();
		// ctp.addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");
		CTDocument1 document = awardDoc.getDocument();

		CTBody body = document.getBody();

		if (!body.isSetSectPr()) {
			body.addNewSectPr();
		}
		CTSectPr section = body.getSectPr();

		if (!section.isSetPgSz()) {
			section.addNewPgSz();
		}
		CTPageNumber pageNumber = section.getPgNumType();
		if (pageNumber == null) {
			pageNumber = section.addNewPgNumType();
		}
		pageNumber.setStart(PAGE_NUMBER_START);
	}
	
	private void addProposerTable(Proposer pro, int countPro, int totalCount) {
        XWPFTable proposerTable = awardDoc.createTable(5, 2);
        setTableBorderToNone(proposerTable);
        CTTblLayoutType type = proposerTable.getCTTbl().getTblPr().addNewTblLayout();
        type.setType(STTblLayoutType.FIXED);

        /// Doesn't seem to have any effect
        proposerTable.getCTTbl().addNewTblGrid().addNewGridCol().setW(TABLE_KEY_WIDTH);
        proposerTable.getCTTbl().getTblGrid().addNewGridCol().setW(TABLE_VALUE_WIDTH);

        String proKey = "";
        if (totalCount == 1) {
            proKey = "申  请  人：";
        } else {
            proKey = "第" + numberToCN((char) (countPro + '0')) + "申请人：";
        }
        int rowNumber = 0;
        setTableRowContent(proposerTable.getRow(rowNumber++), proKey, pro.getProposer());
        setTableRowContent(proposerTable.getRow(rowNumber++), "地      址：", pro.getAddress());
        String idDesc = pro.getType().equalsIgnoreCase("COM") ? "统一社会信用代码：" : "公民身份证号码：";
        setTableRowContent(proposerTable.getRow(rowNumber++), idDesc, pro.getId());
        setTableRowContent(proposerTable.getRow(rowNumber++), "法定代表人：", pro.getRepresentative());
        setTableRowContent(proposerTable.getRow(rowNumber++), "代  理  人：", pro.getAgency());
    }

    private void addRespondentTable(Respondent res, int countRes, int totalCount) {
        XWPFTable respondentTable = awardDoc.createTable(5, 2);
        setTableBorderToNone(respondentTable);
        CTTblLayoutType type = respondentTable.getCTTbl().getTblPr().addNewTblLayout();
        type.setType(STTblLayoutType.FIXED);

        /// Doesn't seem to have any effect
        respondentTable.getCTTbl().addNewTblGrid().addNewGridCol().setW(TABLE_KEY_WIDTH);
        respondentTable.getCTTbl().getTblGrid().addNewGridCol().setW(TABLE_VALUE_WIDTH);

        String resKey = "";
        if (totalCount == 1) {
            resKey = "被申请人：";
        } else {
            resKey = "第" + numberToCN((char) (countRes + '0')) + "被申请人：";
        }
        int rowNumber = 0;
        setTableRowContent(respondentTable.getRow(rowNumber++), resKey, res.getRespondentName());
        setTableRowContent(respondentTable.getRow(rowNumber++), "地      址：", res.getAddress());
        String idDesc = res.getType().equalsIgnoreCase("COM") ? "统一社会信用代码：" : "公民身份证号码：";
        setTableRowContent(respondentTable.getRow(rowNumber++), idDesc, res.getId());
        setTableRowContent(respondentTable.getRow(rowNumber++), "法定代表人：", res.getRepresentative());
        setTableRowContent(respondentTable.getRow(rowNumber++), "代  理  人：", res.getAgency());
    }
    
    private void setTableBorderToNone(XWPFTable proposerTable) {
        CTTblPr tblpro;
        CTTblBorders borders;
        tblpro = proposerTable.getCTTbl().getTblPr();
        borders = tblpro.addNewTblBorders();
        borders.addNewBottom().setVal(STBorder.NONE);
        borders.addNewLeft().setVal(STBorder.NONE);
        borders.addNewRight().setVal(STBorder.NONE);
        borders.addNewTop().setVal(STBorder.NONE);
        borders.addNewInsideH().setVal(STBorder.NONE);
        borders.addNewInsideV().setVal(STBorder.NONE);
    }

    private void setTableRowContent(XWPFTableRow tableRow, String key, String value) {
        XWPFParagraph paragraph;
        XWPFRun paragraphRun;
        tableRow.getCell(0).removeParagraph(0);
        tableRow.getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(TABLE_KEY_WIDTH);
        paragraph = tableRow.getCell(0).addParagraph();
        paragraph.setAlignment(ParagraphAlignment.DISTRIBUTE);
        paragraphRun = paragraph.createRun();
        paragraphRun.setFontFamily(FONT_FAMILY_FANGSONG);
        paragraphRun.setFontSize(CN_FONT_SIZE_SAN);
        paragraphRun.setText(key);

        tableRow.getCell(1).removeParagraph(0);
        tableRow.getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(TABLE_VALUE_WIDTH);
        paragraph = tableRow.getCell(1).addParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        paragraphRun = paragraph.createRun();
        paragraphRun.setFontFamily(FONT_FAMILY_FANGSONG);
        paragraphRun.setFontSize(CN_FONT_SIZE_SAN);
        paragraphRun.setText(value);
    }

	private void breakLine() {
		XWPFParagraph paragraph = awardDoc.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(FONT_FAMILY_SONG);
		run.setFontSize(CN_FONT_SIZE_SAN);
		run.addBreak();
	}

	private void breakToNextPage() {
		XWPFParagraph paragraph = awardDoc.createParagraph();
		XWPFRun run = paragraph.createRun();
		run.addBreak(BreakType.PAGE);
	}
	
	public String numberToCN(char number) {
        if (number > '9' || number < '0') {
            return null;
        }
        String toReturn;
        switch (number) {
            case '0':
                toReturn = "○";
                break;
            case '1':
                toReturn = "一";
                break;
            case '2':
                toReturn = "二";
                break;
            case '3':
                toReturn = "三";
                break;
            case '4':
                toReturn = "四";
                break;
            case '5':
                toReturn = "五";
                break;
            case '6':
                toReturn = "六";
                break;
            case '7':
                toReturn = "七";
                break;
            case '8':
                toReturn = "八";
                break;
            case '9':
                toReturn = "九";
                break;
            default:
                toReturn = "X";
                break;
        }
        return toReturn;
    }
}
