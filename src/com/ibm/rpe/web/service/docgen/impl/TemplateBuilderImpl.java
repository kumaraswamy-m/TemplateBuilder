/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rpe.web.service.docgen.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.ibm.rpe.web.service.docgen.api.model.Template;
import com.ibm.rpe.web.service.docgen.api.model.Template.Container;
import com.ibm.rpe.web.service.docgen.utils.ExportUtil;
import com.ibm.rpe.web.service.docgen.utils.FileUtils;
import com.ibm.rpe.web.service.docgen.utils.JSONUtils;
import com.ibm.rpe.web.service.docgen.utils.TemplateConstants;
import com.ibm.rpe.web.service.docgen.utils.XsdGeneration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class TemplateBuilderImpl
{

	public static int START_ELEMENT_ID = 200;

	private static String templatesHome = null;

	// generateTemplate replaces the place holders in various files by using a
	// hash map.
	public String generateTemplate(String templatesHome, Template template) throws Exception
	{
		this.templatesHome = templatesHome;
		System.out.println("###### Generating template for " + template.getName()); //$NON-NLS-1$

		Map<String, String> params = new HashMap<String, String>();
		Map<String, String> childParams = new HashMap<String, String>();
		String documentTitleRow = getTitleRow(TemplateConstants.DOCUMENT_TITLE, template.getTitle(), template.isTitleQuery(), childParams);

		// replacing place holders in base template.
		params.put(TemplateConstants.SCHEMA_LABEL, template.getSchema());
		params.put(TemplateConstants.TITLE_LEVEL_QUERY, template.getQuery());
		params.put(TemplateConstants.TITLE_EXPRESSION, documentTitleRow);
		params.put(TemplateConstants.STATIC_TITLE, documentTitleRow);

		// Container class contains input for type,orientation,header & footer,
		// header label list and data list.
		List<Container> containers = template.getContainer();

		StringBuffer tableContainerBuffer = new StringBuffer();
		StringBuffer paragraphContainerBuffer = new StringBuffer();

		for (Container container : containers)
		{
			childParams = new HashMap<String, String>();
			childParams.put(TemplateConstants.SCHEMA_LABEL, template.getSchema());
			childParams.put(TemplateConstants.DATA_LEVEL_QUERY, container.getQuery());
			childParams.put(TemplateConstants.STATIC_TITLE, container.getTitle());
			childParams.put(TemplateConstants.STYLE_NAME, container.getTitleStyle());
			if (TemplateConstants.TABLE_TYPE.equalsIgnoreCase(container.getType()))
			{
				// table header cell replacement.
				List<String> headerCellList = container.getHeaderLabels();
				StringBuffer headerBuffer = new StringBuffer();
				for (String attr : headerCellList)
				{
					Map<String, String> innerParams = new HashMap<String, String>();
					innerParams.put(TemplateConstants.TABLE_CELL_ATTRIBUTE_LABEL, attr);
					headerBuffer.append(replacePlaceHolders(innerParams, TemplateConstants.TABLE_HEADER_CELL_XML_PATH));
				}
				// table query data cell replacement.
				StringBuffer dataBuffer = new StringBuffer();
				List<String> dataAttributeList = container.getQueryAttributes();
				for (String data : dataAttributeList)
				{
					Map<String, String> innerParams = new HashMap<String, String>();
					innerParams.put(TemplateConstants.TABLE_CELL_ATTRIBUTE_QUERY, data);
					dataBuffer.append(replacePlaceHolders(innerParams, TemplateConstants.TABLE_QUERY_CELL_XML_PATH));
				}

				// table container replacements.
				childParams.put(TemplateConstants.TITLE_EXPRESSION, getTitleRow(TemplateConstants.TABLE_CONTAINER, container.getTitle(), container.isTitleQuery(), childParams));
				childParams.put(TemplateConstants.TABLE_SINGLE_CELL_LABEL, headerBuffer.toString());
				childParams.put(TemplateConstants.TABLE_SINGLE_CELL_QUERY, dataBuffer.toString());

				tableContainerBuffer.append(replacePlaceHolders(childParams, TemplateConstants.TABLE_CONTAINER_XML_PATH));

			}
			else if (TemplateConstants.PARAGRAPH_TYPE.equalsIgnoreCase(container.getType()))
			{
				// paragraph header cell replacement.
				List<String> headerCellList = container.getHeaderLabels();
				List<String> dataAttributeList = container.getQueryAttributes();

				StringBuffer paraRowBuffer = new StringBuffer();
				for (int i = 0; i < headerCellList.size(); i++)
				{
					Map<String, String> innerParams = new HashMap<String, String>();
					innerParams.put(TemplateConstants.PARAGRAPH_ATTRIBUTE_LABEL, headerCellList.get(i));
					innerParams.put(TemplateConstants.PARAGRAPH_ATTRIBUTE_QUERY, dataAttributeList.get(i));

					paraRowBuffer.append(replacePlaceHolders(innerParams, TemplateConstants.PARAGRAPH_ROW_XML_PATH));
				}

				// paragraph container
				childParams.put(TemplateConstants.TITLE_EXPRESSION, getTitleRow(TemplateConstants.PARAGRAPH_CONTAINER, container.getTitle(), container.isTitleQuery(), childParams));
				childParams.put(TemplateConstants.PARAGRAPH_SINGLE_ROW, paraRowBuffer.toString());

				paragraphContainerBuffer.append(replacePlaceHolders(childParams, TemplateConstants.PARAGRAPH_CONTAINER_XML_PATH));
			}
		}

		String templatesPath = System.getProperty("java.io.tmpdir") + "template_" + UUID.randomUUID().toString() + File.separator; //$NON-NLS-1$

		if (template.isToc())
		{
			params.put(TemplateConstants.TOC_CONTAINER, readTemplatsFile(TemplateConstants.TOC_XML_PATH));
		}

		params.put(TemplateConstants.TABLE_CONTAINER, tableContainerBuffer.toString());
		params.put(TemplateConstants.PARAGRAPH_CONTAINER, paragraphContainerBuffer.toString());

		params.put(TemplateConstants.MASTERPAGE_HEADER_TEXT, template.getHeaderText());
		params.put(TemplateConstants.MASTERPAGE_FOOTER_TEXT, template.getFooterText());
		params.put(TemplateConstants.MASTERPAGE_ORIENTATION, template.getPageOrientation());

		String templateXml = replacePlaceHolders(params, TemplateConstants.BASE_TEMPLATE_XML_PATH);
		FileUtils.createFileParent(templatesPath + "template.xml"); //$NON-NLS-1$
		System.out.println("Temp path: " + templatesPath);

		// Generate XSD

		String schemaPath = templatesPath + "schemas" + File.separator + "schema" + File.separator + "schema.xsd";//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		FileUtils.createFileParent(schemaPath);
		String xsdContents = null;
		if (template.getXsdPath() != null)
		{
			xsdContents = downloadFile(template.getXsdPath());
		}
		else if (template.getXmlPath() != null)
		{
			System.out.println("XSD not specified. Generate XSD");
			String xmlContents = downloadFile(template.getXmlPath());

			FileUtils.writeFile(templatesPath + File.separator + "temp.xml", xmlContents);
			xsdContents = XsdGeneration.generateXSD(templatesPath + File.separator + "temp.xml", templatesPath + "schemas" + File.separator + "schema" + File.separator + "schema.xsd"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else
		{
			throw new Exception("xml path or xsd path is mandatory");
		}

		new File(templatesPath + File.separator + "template.xml").createNewFile(); //$NON-NLS-1$
		FileUtils.writeFile(templatesPath + "template.xml", templateXml); //$NON-NLS-1$

		String name = template.getName();

		if (name == null || name.isEmpty())
		{
			name = "template"; //$NON-NLS-1$
		}

		File zip = File.createTempFile(name + "_", ".dta", new File(templatesPath)); //$NON-NLS-1$ //$NON-NLS-2$

		FileUtils.zipFiles(templatesPath, zip.getAbsolutePath());
		System.out.println("Template path: " + zip.getAbsolutePath());
		return zip.getAbsolutePath();
	}

	public void generateDocspec(String templatePath, String dataSourceUrl, String schemaName) throws Exception
	{
		System.out.println("###### Generating document specification"); //$NON-NLS-1$
		String specificationContent = readTemplatsFile(TemplateConstants.BASE_DOCSPEC_PATH);

		// Convert any '&' characters in the query URL to "&amp;" or else RPE
		// launcher will not load the DSX file.

		// Substitute the schema name and query URL in the specification content
		Map<String, String> params = new HashMap<String, String>();
		params.put(TemplateConstants.TEMPLATE_NAME_PATH, templatePath);
		params.put(TemplateConstants.DATA_SOURCE_URL, dataSourceUrl);
		params.put(TemplateConstants.SCHEMA_LABEL, schemaName);
		for (Entry<String, String> entry : params.entrySet())
		{
			specificationContent = specificationContent.replace(entry.getKey(), entry.getValue());
		}

		File temlateFile = new File(templatePath);
		String docSpecName = temlateFile.getName().replace(".dta", ".dsx"); //$NON-NLS-1$ //$NON-NLS-2$
		String docSpec = temlateFile.getParent() + File.separator + docSpecName;

		new File(docSpec).createNewFile();
		FileUtils.writeFile(docSpec, specificationContent);
		System.out.println("Doc spec: " + docSpec);
	}

	private static String replacePlaceHolders(Map<String, String> params, String xmlPath) throws FileNotFoundException
	{

		String cellFormat = readTemplatsFile(xmlPath);

		// params.put(ELEMENT_ID, Integer.toString(++START_ELEMENT_ID));

		// Use the original cell format string and replace the parameters
		for (Entry<String, String> entry : params.entrySet())
		{
			cellFormat = cellFormat.replace(entry.getKey(), entry.getValue());
		}

		while (cellFormat.contains(TemplateConstants.ELEMENT_ID))
		{
			cellFormat = cellFormat.replaceFirst(TemplateConstants.ELEMENT_ID, Integer.toString(++START_ELEMENT_ID));
		}

		return cellFormat;
	}

	private static String getTitleRow(String type, String titleInput, boolean isQuery, Map<String, String> childParams)
			throws FileNotFoundException
	{
		String title = "";
		if (titleInput != null && !titleInput.isEmpty())
		{
			String context = "";
			String contextInfo = readTemplatsFile(TemplateConstants.QUERY_CONTEXT_TEXT_PATH);
			if (type != null && contextInfo != null)
			{
				String[] contexts = contextInfo.split(";");
				for (String ctx : contexts)
				{
					if (ctx.startsWith(type))
					{
						context = ctx.split("=")[1];
						break;
					}
				}
			}
			childParams.put(TemplateConstants.TITLE, titleInput);
			if (isQuery)
			{
				childParams.put(TemplateConstants.QUERY_CONTEXT, context);
				title = replacePlaceHolders(childParams, TemplateConstants.QUERY_TITLE_XML_PATH);
			}
			else
			{
				title = replacePlaceHolders(childParams, TemplateConstants.STATIC_TITLE_XML_PATH);
			}
		}
		return title;
	}

	public static void main(String args[]) throws URISyntaxException, Exception
	{
		String xmlPath = "https://rpe.mybluemix.net/rpeng/examples/data/requisitepro.xml";
		String xmlFilePath = downloadFile(xmlPath);

		TemplateBuilderImpl generator = new TemplateBuilderImpl();

		//generator.templatesHome = "D:\\ks\\work\\RPE_2012\\Repository\\ws_rpe_main_stream_jee\\ws\\TemplateBuilder\\WebContent\\template";
		
		generator.templatesHome = "C:\\Users\\IBM_ADMIN\\workspace_new\\TemplateBuilder\\WebContent\\template";
		String templateJson = readTemplatsFile("/data/template.json");

		Template template = (Template) JSONUtils.readValue(templateJson, Template.class);
		System.out.println(JSONUtils.writeValue(template));

		generator.generateTemplate(templatesHome, template);
	}

	private static String readTemplatsFile(String templateFile) throws FileNotFoundException
	{
		return ExportUtil.readFile(new FileInputStream(templatesHome + templateFile));
	}

	private static String downloadFile(String path) throws Exception
	{
		String data = null;

		if (path != null)
		{

			if (path.toUpperCase().startsWith("FILE:") || (new File(path)).exists())
			{
				data = ExportUtil.readFile(new File(path));
			}
			else if (path.toUpperCase().startsWith("HTTP"))
			{
				Client client = new Client();

				ClientResponse response = client.resource(UriBuilder.fromUri(path).build()).get(ClientResponse.class);

				if (Response.Status.OK.getStatusCode() == response.getStatus())
				{
					data = response.getEntity(String.class);
				}
				else
				{
					throw new Exception("Error while getting data");
				}
			}
		}

		return data;
	}
}
