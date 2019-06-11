package cn.com.xiaofabo.scia.aiawardcheck.main;

import cn.com.xiaofabo.scia.aiawardcheck.entity.Award;
import cn.com.xiaofabo.scia.aiawardcheck.fileprocessor.AwardReader;
import cn.com.xiaofabo.scia.aiawardcheck.fileprocessor.AwardWriter;

public class App {
	public static void main(String[] args) throws Exception {
		try {
			AwardReader arbReader = new AwardReader();
			Award ab = arbReader.buildArbitration("D:/tmp/1.docx");
			AwardWriter arbWriter = new AwardWriter("D:/tmp/out.docx");
			arbWriter.generateAwardDoc(ab);
		} catch (Exception e) {
			System.err.println("Wrong!");
		}
	}
}
