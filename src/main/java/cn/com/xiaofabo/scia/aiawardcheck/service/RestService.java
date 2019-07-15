package cn.com.xiaofabo.scia.aiawardcheck.service;

import cn.com.xiaofabo.scia.aiawardcheck.entity.Award;
import com.sun.jersey.multipart.FormDataParam;
import cn.com.xiaofabo.scia.aiawardcheck.fileprocessor.AwardReader;
import cn.com.xiaofabo.scia.aiawardcheck.fileprocessor.AwardWriter;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/formatcheckservice")
public class RestService {

	public static Logger logger = Logger.getLogger(RestService.class.getName());
	public static void main(String[] args) throws Exception {
		try {
			AwardReader arbReader = new AwardReader();
			File sampleInput = new File("E:/sampleinput.doc");
			Award ab = arbReader.buildAward(sampleInput);
			AwardWriter arbWriter = new AwardWriter("E:/out.docx");
			logger.debug("Output document generated");
			arbWriter.generateAwardDoc(ab);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response postTest(@FormDataParam("file") InputStream uploadedInputStream) {

		File file = null;
		try {

			String uploadedFileURL = "C://temp//uploadedFile.docx";
			String outputFilePath = "C://temp//";
			String outputFileName = "output.docx";
			final String outputFileURL = outputFilePath + outputFileName;

			// save it
			writeToFile(uploadedInputStream, uploadedFileURL);

			file = new File(uploadedFileURL);
			AwardReader arbReader = new AwardReader();
			Award ab = arbReader.buildAward(uploadedFileURL);
			
			AwardWriter arbWriter = new AwardWriter(outputFileURL);
			arbWriter.generateAwardDoc(ab);
			
			StreamingOutput fileStream = new StreamingOutput() {
				@Override
				public void write(OutputStream output) throws IOException, WebApplicationException {
					try {
						java.nio.file.Path path = Paths.get(outputFileURL);
						byte[] data = Files.readAllBytes(path);
						output.write(data);
						output.flush();
					} catch (Exception e) {
						throw new WebApplicationException(Response.status(404).build());
					}

				}
			};
			return Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
					.header("content-disposition", "attachment; filename = " + outputFileName).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(417).build();
	}

	@GET
	@Path("/download")
	public Response downloadFile() {
		StreamingOutput fileStream = new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try {
					java.nio.file.Path path = Paths.get("D://tmp/upload.docx");
					byte[] data = Files.readAllBytes(path);
					output.write(data);
					output.flush();
				} catch (Exception e) {
					throw new WebApplicationException(Response.status(404).build());
				}

			}
		};
		return Response.ok(fileStream, MediaType.APPLICATION_OCTET_STREAM)
				.header("content-disposition", "attachment; filename = upload.docx").build();
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
