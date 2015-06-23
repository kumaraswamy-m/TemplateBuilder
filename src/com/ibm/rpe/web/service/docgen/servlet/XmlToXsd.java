/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rpe.web.service.docgen.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
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

import org.apache.commons.io.IOUtils;

import com.ibm.rpe.web.service.docgen.utils.FileUtils;
import com.ibm.rpe.web.service.docgen.utils.JSONUtils;
import com.ibm.rpe.web.service.docgen.utils.XsdGeneration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Servlet implementation class converting xml to xsd
 */
@Path("/utils/xmltoxsd")
public class XmlToXsd
{

	@GET
	@Produces(
	{ MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN })
	public Response convertXmlToJson(@Context HttpServletRequest request, @QueryParam("url") String xmlUrl)
			throws Exception
	{
		String xsd = null;
		try
		{
			Client client = new Client();
			WebResource service = client.resource(UriBuilder.fromUri(xmlUrl).build());

			ClientResponse clientResponse = service.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
			if (Response.Status.OK.getStatusCode() != clientResponse.getStatus())
			{
				return Response.serverError().status(Status.BAD_REQUEST).entity(clientResponse.getEntity(String.class)).build();
			}
			InputStream xmlStream = clientResponse.getEntityInputStream();
			String xmlAsString = IOUtils.toString(xmlStream, "UTF-8");

			String workingDirectory = System.getProperty("java.io.tmpdir") + "schema_" + UUID.randomUUID().toString() + File.separator; //$NON-NLS-1$ //$NON-NLS-2$

			String xsdFilePath = workingDirectory + "xsd_" + UUID.randomUUID().toString() + ".xsd"; //$NON-NLS-1$ //$NON-NLS-2$
			FileUtils.createFileParent(xsdFilePath);

			String xmlFilePath = workingDirectory + "xsd_" + UUID.randomUUID().toString() + ".xml"; //$NON-NLS-1$ //$NON-NLS-2$
			FileWriter fileWritter = new FileWriter(xmlFilePath);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			String s = new String(xmlAsString.getBytes(), "UTF-8"); //$NON-NLS-1$
			try
			{
				bufferWritter.write(s);
			}
			finally
			{
				bufferWritter.close();
			}
			try
			{
				XsdGeneration.generateXSD(xmlFilePath, xsdFilePath);
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}
			// return Response.ok().build();
			xsd = IOUtils.toString(new FileInputStream(xsdFilePath), "UTF-8");
			// TODO Delete workingDirectory directory
			return Response.ok().entity(xsd).build();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return Response.serverError().status(Status.BAD_REQUEST).entity(JSONUtils.writeValue(e.getLocalizedMessage())).build();
		}
	}
}
