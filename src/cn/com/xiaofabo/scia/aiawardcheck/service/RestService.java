package cn.com.xiaofabo.scia.aiawardcheck.service;

import java.io.File;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cn.com.xiaofabo.scia.aiawardcheck.entity.Award;
import cn.com.xiaofabo.scia.aiawardcheck.fileprocessor.AwardReader;
import cn.com.xiaofabo.scia.aiawardcheck.fileprocessor.AwardWriter;

@Path("/checkservice")
public class RestService {

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public String postTest(File inputFile) {
		try {
			System.out.println(inputFile.length());
			AwardReader arbReader = new AwardReader();
			Award ab = arbReader.buildAward(inputFile);
			AwardWriter arbWriter = new AwardWriter("out.docx");
			arbWriter.generateAwardDoc(ab);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputFile.length()+"";
	}
}
