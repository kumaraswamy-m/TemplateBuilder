/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rpe.web.service.docgen.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.ibm.rpe.web.service.docgen.api.model.Template;
import com.ibm.rpe.web.service.docgen.impl.TemplateBuilderImpl;
import com.ibm.rpe.web.service.docgen.utils.JSONUtils;
import com.ibm.rpe.web.service.docgen.utils.Parameters;
import com.ibm.rpe.web.service.docgen.utils.TemplateConstants;

/**
 * Servlet implementation class TemplateBuilder
 */
@Path("/template")
public class TemplateBuilder
{
	@GET
	@Path("{path}")
	@Produces(
	{ MediaType.TEXT_PLAIN })
	public Response unhandled(@PathParam("path") String path) throws IOException
	{
		return Response.status(Response.Status.BAD_REQUEST).entity("Unsupported request: " + path).build(); //$NON-NLS-1$
	}

	@PUT
	@Path("/update")
	@Produces(
	{ MediaType.APPLICATION_JSON })
	public Response update(@FormParam(Parameters.Form.REPORT) String reportJSON)
	{
		// no update allowed in this version
		return Response.ok().entity(reportJSON).build();
	}

	@POST
	@Path("/create")
	@Produces(
	{ MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM })
	public Response change(@Context HttpServletRequest request,
			@FormParam(Parameters.Form.TEMPLATE_DATA) String templateData,
			@HeaderParam(Parameters.Header.SECRET) String secret,
			@HeaderParam(Parameters.Header.AUTH_TYPE) String authType,
			@HeaderParam(Parameters.Header.AUTH_COOKIES) String authCookies) throws IOException
	{
		TemplateBuilderImpl tempBuilderImpl = new TemplateBuilderImpl();
		String templatesHome = (String) request.getServletContext().getAttribute(TemplateConstants.TEMPLATES_HOME);

		templatesHome += File.separator + TemplateConstants.PATH_TEMPLATE;

		try
		{
			Template template = (Template) JSONUtils.readValue(templateData, Template.class);
			String templatePath = tempBuilderImpl.generateTemplate(templatesHome, template);
			File outFile = new File(templatePath);

			ResponseBuilder responseBuilder = Response.ok(outFile, MediaType.APPLICATION_OCTET_STREAM);
			responseBuilder.header("Content-Disposition", "attachment; filename=" + outFile.getName()); //$NON-NLS-1$ //$NON-NLS-2$

			return responseBuilder.build();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return Response.serverError().status(Status.BAD_REQUEST).entity(JSONUtils.writeValue(e.getLocalizedMessage())).build();
		}
	}
}
