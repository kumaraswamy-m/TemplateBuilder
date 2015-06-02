/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © © Copyright IBM Corporation 2014, 2015. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.ibm.rpe.web.service.docgen.api.model.Template;
import com.ibm.rpe.web.service.docgen.utils.JSONUtils;
import com.ibm.rpe.web.service.docgen.utils.Parameters;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@SuppressWarnings("nls")
public class TestTemplateLocal
{
	private static final String LOCAL_TEMPLATE_CREATE_URL = "http://localhost:8080/rpet/api/template";

	private static final String LOCAL_XML_URL = "http://localhost:8080/rpet/template/data/requisitepro.xml";

	/**
	 * Test document template creation
	 * 
	 */
	@Test
	public void localTemplateCreation() throws ClientHandlerException, UniformInterfaceException, IOException,
			JAXBException
	{
		Client client = new Client();

		MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
		formData.add(Parameters.Form.TEMPLATE_DATA, getTemplateData());

		WebResource service = client.resource(UriBuilder.fromUri(LOCAL_TEMPLATE_CREATE_URL).build());

		// create the job
		ClientResponse response = service.path("create").accept(MediaType.APPLICATION_OCTET_STREAM).post(ClientResponse.class, formData);
		Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

		InputStream is = response.getEntityInputStream();

		File localFile = new File("C:\\tmp\\results\\" + UUID.randomUUID().toString() + ".dta");
		localFile.getParentFile().mkdirs();
		IOUtils.copy(is, new FileOutputStream(localFile));

		System.out.println("File downloaded to: " + localFile.getAbsolutePath());

	}

	private static String getTemplateData() throws IOException
	{
		Template template = new Template();
		template.setName("Requirements");
		template.setXmlPath(LOCAL_XML_URL);

		template.setSchema("test");

		template.setHeaderText("My Requirements");
		template.setFooterText("Copyright from dev");
		template.setPageOrientation("portrait");
		template.setQuery("Project");
		template.setTitle("Requirement Specifications from Requisite Pro");
		template.setToc(true);
		template.setTitleQuery(false);

		Template.Container container = new Template.Container();
		container.setType("paragraph");
		container.setTitle("FullTag");
		container.setTitleQuery(true);
		container.setTitleStyle("1");
		container.setQuery("Project/Requirements/PRRequirement");
		container.setHeaderLabels(Arrays.asList("Text", "Priority", "changed"));
		container.setQueryAttributes(Arrays.asList("Text", "Priority", "Changed"));

		template.addContainer(container);

		container = new Template.Container();
		container.setType("table");
		container.setTitle("My test table");
		container.setTitleQuery(false);
		container.setTitleStyle("1");
		container.setQuery("Project/Requirements/PRRequirement");
		container.setHeaderLabels(Arrays.asList("Full Tag", "Text", "Priority", "Changed"));
		container.setQueryAttributes(Arrays.asList("FullTag", "Text", "Priority", "Changed"));

		template.addContainer(container);

		return JSONUtils.writeValue(template);
	}
}
