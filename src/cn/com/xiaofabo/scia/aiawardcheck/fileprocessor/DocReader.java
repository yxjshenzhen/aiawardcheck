package cn.com.xiaofabo.scia.aiawardcheck.fileprocessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class DocReader {
	protected String docText;
	XWPFDocument docx;
	HWPFDocument doc;
	private List<String> userErrors;
	private List<String> userWarnings;

	public DocReader() {
		userErrors = new ArrayList();
		userWarnings = new ArrayList();
	}

	public void readWordFile(String inputPath) throws IOException {
		try {
			docx = new XWPFDocument(new FileInputStream(inputPath));
			XWPFWordExtractor we = new XWPFWordExtractor(docx);
			docText = we.getText();
		} /// Old version word documents
		catch (Exception e) {
			doc = new HWPFDocument(new FileInputStream(inputPath));
			WordExtractor we = new WordExtractor(doc);
			docText = we.getText();
		}
	}
	
	public void readWordFile(File inputFile) throws IOException {
		try {
			docx = new XWPFDocument(new FileInputStream(inputFile));
			XWPFWordExtractor we = new XWPFWordExtractor(docx);
			docText = we.getText();
		} /// Old version word documents
		catch (Exception e) {
			doc = new HWPFDocument(new FileInputStream(inputFile));
			WordExtractor we = new WordExtractor(doc);
			docText = we.getText();
		}
	}

	protected String combineLines(String[] lines, int startIndex, int endIndex) {
		if (startIndex >= lines.length || endIndex >= lines.length) {
			return null;
		}
		StringBuilder toReturn = new StringBuilder();
		for (int i = startIndex; i < endIndex; ++i) {
			if (!removeAllSpaces(lines[i]).isEmpty()) {
				toReturn.append(lines[i]).append("\n");
			}
		}
		return toReturn.toString();
	}

	protected String removeAllSpaces(String input) {
		return input.replaceAll("\\s+", "");
	}

	/// Getters
	public String getDocText() {
		return docText;
	}

	public XWPFDocument getDocx() {
		return docx;
	}

	public HWPFDocument getDoc() {
		return doc;
	}

	public List<String> getUserErrors() {
		return userErrors;
	}

	public List<String> getUserWarnings() {
		return userWarnings;
	}
}
