/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rpe.web.service.docgen.servlet;

import java.io.InputStream;

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
import org.json.JSONObject;
import org.json.XML;

import com.ibm.rpe.web.service.docgen.utils.JSONUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Servlet implementation class converting xml to json
 */
@Path("/xmltojson")
public class XmlToJSON
{

	@GET
	@Produces(
	{ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
	public Response convertXmlToJson(@Context HttpServletRequest request, @QueryParam("url") String xmlUrl)
			throws Exception
	{
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

			return Response.ok().entity( getXMLfromJson(xmlAsString)).build();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return Response.serverError().status(Status.BAD_REQUEST).entity(JSONUtils.writeValue(e.getLocalizedMessage())).build();
		}
	}

	private String getXMLfromJson(String xmlContent)
	{
		JSONObject jsonObj = XML.toJSONObject(xmlContent);
		return jsonObj.toString();
	}
}
