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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;

import cn.com.xiaofabo.scia.aiawardcheck.entity.Award;
import cn.com.xiaofabo.scia.aiawardcheck.entity.Routine;

public class AwardWriter extends DocWriter {
	
	private final XWPFDocument awardDoc;
	private final String outFileUrl;
	
	public AwardWriter (String outFileUrl) {
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
		}catch (FileNotFoundException e) {
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

        addSubTitle("一、案    情");
        addTitleTextParagraph("（一）申请人的主张和请求", 0);
        
        /// 申请人称
        addNormalTextParagraph("申请人称：", 0);
        addNormalTextParagraphs(award.getCaseText(), 0, 1);
        
        addSubTitle("二、仲裁庭意见");
        addNormalTextParagraphs(award.getArbiOpinionText(), 0, 1);
        
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
            String str = ((String)strList.get(i)).trim();
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

//        paragraph.getCTP().addNewR().addNewRPr().addNewSz().setVal(BigInteger.valueOf(18));
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

//        CTP ctp = paragraph.getCTP();
//        ctp.addNewFldSimple().setInstr("PAGE \\* MERGEFORMAT");
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
}
