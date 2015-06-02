package com.ibm.rpe.web.service.docgen.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import com.ibm.rpe.web.service.docgen.utils.ExportUtil;
import com.ibm.rpe.web.service.docgen.utils.FileUtils;
import com.ibm.rpe.web.service.docgen.utils.JSONUtils;
import com.ibm.rpe.web.service.docgen.utils.XsdGeneration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Servlet implementation class conversting xml to json
 */
@Path("/xmltoxsd")
public class XmlToXsd {

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN })
	public Response convertXmlToJson(@Context HttpServletRequest request,
			@QueryParam("url") String xmlUrl) throws Exception {
		String xsd = null;
		try {
			Client client = new Client();
			WebResource service = client.resource(UriBuilder.fromUri(xmlUrl)
					.build());

			// create the job
			ClientResponse clientResponse = service.accept(
					MediaType.APPLICATION_XML).get(ClientResponse.class);
			if (Response.Status.OK.getStatusCode() != clientResponse
					.getStatus()) {
				// return Response.serverError().status(Status.BAD_REQUEST)
				// .entity(clientResponse.getEntity(String.class)).build();
			}
			InputStream is = clientResponse.getEntityInputStream();
			String inputXml = FileUtils.getStringFromInputStream(is);
			System.out.println(inputXml);
			String output_file = "C:\\tmp\\results\\"
					+ UUID.randomUUID().toString() + ".xsd";
			String path = "C:\\tmp\\results\\" + UUID.randomUUID().toString()
					+ ".xml";
			FileWriter fileWritter = new FileWriter(path);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			String s = new String(inputXml.getBytes(), "UTF-8");// force to
																// convert UTF-8
																// standard will
																// address
																// this
																// issue Invalid
																// byte 1 of
																// 1-byte
																// UTF-8
																// sequence
			try {
				bufferWritter.write(s);
			} finally {
				bufferWritter.close();
			}
			try {
				XsdGeneration.generateXSD(path, output_file);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			// return Response.ok().build();
			xsd = ExportUtil.readFile(new File(output_file));
			return Response.ok().entity(xsd).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().status(Status.BAD_REQUEST)
					.entity(JSONUtils.writeValue(e.getLocalizedMessage()))
					.build();
		}
	}
}
