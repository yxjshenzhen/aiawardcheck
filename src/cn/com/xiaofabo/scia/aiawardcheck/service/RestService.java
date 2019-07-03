package cn.com.xiaofabo.scia.aiawardcheck.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import cn.com.xiaofabo.scia.aiawardcheck.entity.Award;
import cn.com.xiaofabo.scia.aiawardcheck.fileprocessor.AwardReader;
import cn.com.xiaofabo.scia.aiawardcheck.fileprocessor.AwardWriter;

@Path("/checkservice")
public class RestService {

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response postTest(@FormDataParam("file") InputStream uploadedInputStream) {

		File file = null;
		try {

			String uploadedFileURL = "d://tmp//uploadedFile.docx";
			String outputFilePath = "d://tmp//";
			String outputFileName = "output.docx";
			String outputFileURL = outputFilePath + outputFileName;

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
						throw new WebApplicationException("File Not Found !!");
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
					throw new WebApplicationException("File Not Found !!");
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
