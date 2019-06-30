package cn.com.xiaofabo.scia.aiawardcheck.main;

import org.apache.log4j.Logger;

import cn.com.xiaofabo.scia.aiawardcheck.entity.Award;
import cn.com.xiaofabo.scia.aiawardcheck.fileprocessor.AwardReader;
import cn.com.xiaofabo.scia.aiawardcheck.fileprocessor.AwardWriter;

public class App {
	public static Logger logger = Logger.getLogger(App.class.getName());
	public static void main(String[] args) throws Exception {
		try {
			AwardReader arbReader = new AwardReader();
			Award ab = arbReader.buildArbitration("D:/tmp/sampleinput1.doc");
			AwardWriter arbWriter = new AwardWriter("D:/tmp/out.docx");
			logger.debug("Output document generated");
			arbWriter.generateAwardDoc(ab);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
