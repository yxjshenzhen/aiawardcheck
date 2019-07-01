package cn.com.xiaofabo.scia.aiawardcheck.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	public String postTest(InputStream uploadedInputStream) {
		try {
			writeToFile(uploadedInputStream, "./input.docx");
			File inputFile = new File("./input.docx");
			System.out.println(inputFile.getAbsolutePath());
			// AwardReader arbReader = new AwardReader();
			// Award ab = arbReader.buildAward(inputFile);
			// AwardWriter arbWriter = new AwardWriter("out.docx");
			// arbWriter.generateAwardDoc(ab);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Successful!";
	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
}
