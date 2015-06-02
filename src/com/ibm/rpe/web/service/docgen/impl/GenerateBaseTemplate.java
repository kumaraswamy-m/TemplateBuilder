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
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ibm.rpe.web.service.docgen.api.model.BaseTemplateInput;
import com.ibm.rpe.web.service.docgen.utils.ExportUtil;
import com.ibm.rpe.web.service.docgen.utils.FileUtils;
import com.ibm.rpe.web.service.docgen.utils.JSONUtils;
import com.ibm.rpe.web.service.docgen.utils.TemplateConstants;

@SuppressWarnings("nls")
public class GenerateBaseTemplate
{
	private static final String TEMPLATE_XML_PATH = "/template/template.xml";

	private final static Map<String, String> FILENAME_MAP = new HashMap<String, String>();

	private static String templatesHome = null;

	static
	{
		final int PATH_INDEX = TemplateConstants.TABLE_HEADER_CELL_XML_PATH.lastIndexOf("/") + 1;
		FILENAME_MAP.put(TemplateConstants.TABLE_SINGLE_CELL_LABEL, TemplateConstants.TABLE_HEADER_CELL_XML_PATH.substring(PATH_INDEX));
		FILENAME_MAP.put(TemplateConstants.TABLE_SINGLE_CELL_QUERY, TemplateConstants.TABLE_QUERY_CELL_XML_PATH.substring(PATH_INDEX));
		FILENAME_MAP.put(TemplateConstants.TABLE_CONTAINER, TemplateConstants.TABLE_CONTAINER_XML_PATH.substring(PATH_INDEX));

		FILENAME_MAP.put(TemplateConstants.PARAGRAPH_SINGLE_ROW, TemplateConstants.PARAGRAPH_ROW_XML_PATH.substring(PATH_INDEX));
		FILENAME_MAP.put(TemplateConstants.PARAGRAPH_CONTAINER, TemplateConstants.PARAGRAPH_CONTAINER_XML_PATH.substring(PATH_INDEX));

		FILENAME_MAP.put(TemplateConstants.TOC_CONTAINER, TemplateConstants.TOC_XML_PATH.substring(PATH_INDEX));

		FILENAME_MAP.put("BASE_TEMPLATE", TemplateConstants.BASE_TEMPLATE_XML_PATH.substring(PATH_INDEX));
		FILENAME_MAP.put("QUERY_CONTEXT", TemplateConstants.QUERY_CONTEXT_TEXT_PATH.substring(PATH_INDEX));
		FILENAME_MAP.put("STATIC_TITLE", TemplateConstants.STATIC_TITLE_XML_PATH.substring(PATH_INDEX));
		FILENAME_MAP.put("QUERY_TITLE", TemplateConstants.QUERY_TITLE_XML_PATH.substring(PATH_INDEX));
	}

	public static void generate(String inputDataJson) throws FileNotFoundException, IOException,
			ParserConfigurationException, SAXException
	{
		@SuppressWarnings("unchecked")
		List<Map<String, String>> jsonMap = (List<Map<String, String>>) JSONUtils.readValue(inputDataJson, List.class);

		List<BaseTemplateInput> inputDataList = new ArrayList<BaseTemplateInput>();
		for (Map<String, String> map : jsonMap)
		{
			BaseTemplateInput data = (BaseTemplateInput) JSONUtils.readValue(JSONUtils.writeValue(map), BaseTemplateInput.class);
			data.setOutputFile(FILENAME_MAP.get(data.getReplacement()));
			inputDataList.add(data);
		}

		String workingDirectory = System.getProperty("java.io.tmpdir") + "base_templates_" + UUID.randomUUID().toString() + File.separator;

		String baseTemplatePath = workingDirectory + FILENAME_MAP.get("BASE_TEMPLATE");
		FileUtils.createFileParent(baseTemplatePath);
		System.out.println(">>>>> Working directory: " + workingDirectory);

		// System.out.println(">>>>> baseTemplatePath: " + baseTemplatePath);
		FileUtils.writeFile(baseTemplatePath, readTemplatsFile(TEMPLATE_XML_PATH));

		for (BaseTemplateInput data : inputDataList)
		{
			if (data.getElementId() != null)
			{
				createTemplateXmls(baseTemplatePath, data);
			}
			else
			{
				String baseTemplateXmlString = replace(ExportUtil.readFile(new File(baseTemplatePath)), data.getReplaceMap());
				baseTemplateXmlString = replaceSchemaUrl(baseTemplateXmlString);
				FileUtils.writeFile(baseTemplatePath, baseTemplateXmlString);
			}
		}

		// write context information
		writeContextInfo(inputDataList, workingDirectory + FILENAME_MAP.get("QUERY_CONTEXT"));

		// write title - static title
		FileUtils.writeFile(workingDirectory + FILENAME_MAP.get("STATIC_TITLE"), "<expression tag=\"content\" type=\"constant\">%TITLE%</expression>");

		// write title - query title
		FileUtils.writeFile(workingDirectory + FILENAME_MAP.get("QUERY_TITLE"), "<expression tag=\"content\" type=\"data\" context=\"%QUERY_CONTEXT%\">%TITLE%</expression>");
	}

	private static void createTemplateXmls(String inputFile, BaseTemplateInput data)
			throws ParserConfigurationException, SAXException, IOException
	{
		String directory = (new File(inputFile)).getParent() + File.separator;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;

		builder = factory.newDocumentBuilder();

		Document document = builder.parse(new File(inputFile));

		NodeList nlist = document.getElementsByTagName("element");
		if (nlist != null && nlist.getLength() > 0)
		{
			for (int i = 0; i < nlist.getLength(); i++)
			{
				Node node = nlist.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					Element ele = (Element) node;
					String attr = ele.getAttribute("id");

					if (attr.contentEquals(data.getElementId()))
					{
						Node newNode = document.createTextNode(data.getReplacement());
						node.getParentNode().insertBefore(newNode, node);
						try
						{
						}
						catch (Exception e1)
						{
							e1.printStackTrace();
						}
						Node removedNode = node.getParentNode().removeChild(node);
						// System.out.println(nodeToString(removedNode));
						String removed = nodeToString(removedNode);
						removed = replace(removed, data.getReplaceMap());
						if (data.isReplaceElementId())
						{
							removed = replaceElementId(removed);
						}
						document.normalize();
						FileUtils.writeFile(directory + data.getOutputFile(), removed);
						System.out.println("Removed : " + ((Element) removedNode).getAttribute("id"));

						try
						{
							// prettyPrint(document);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						break;
					}
				}
			}
			FileUtils.writeFile(inputFile, transformToString(document));
		}
	}

	private static String transformToString(Document document)
	{
		try
		{
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			StringWriter buffer = new StringWriter();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(document), new StreamResult(buffer));
			return buffer.toString();
		}
		catch (TransformerException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static String nodeToString(Node node)
	{
		StringWriter sw = new StringWriter();
		try
		{
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		}
		catch (TransformerException te)
		{
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}

	public final void prettyPrint(Document xml) throws Exception
	{
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(xml), new StreamResult(out));
		System.out.println(out.toString());
	}

	private static String replace(String result, Map<String, String> params)
	{
		if (result != null && params != null)
		{
			for (Entry<String, String> entry : params.entrySet())
			{
				result = result.replace(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	private static String replaceElementId(String str)
	{
		String replaceString = "<element id=\"%ELEMENT_ID%\"";
		String tmpReplace = "%TEMP_ELEMENT_ID%";

		final Pattern myPattern = Pattern.compile("<element id=\"\\d+\"");
		Matcher m = myPattern.matcher(str);
		while (m.find())
		{
			str = m.replaceFirst(tmpReplace);
			m = myPattern.matcher(str);
		}

		str = str.replaceAll(tmpReplace, replaceString);
		return str;
	}

	private static String replaceSchemaUrl(String str)
	{
		String replaceString = "\"schemas/schema/schema.xsd\"";

		final Pattern myPattern = Pattern.compile("\"schemas/url_.*.xsd\"");
		Matcher m = myPattern.matcher(str);
		if (m.find())
		{
			str = m.replaceFirst(replaceString);
		}

		return str;
	}

	private static void writeContextInfo(List<BaseTemplateInput> inputDataList, String contextFilePath)
			throws FileNotFoundException
	{
		StringBuffer contextBuf = new StringBuffer();
		if (inputDataList != null)
		{
			for (BaseTemplateInput data : inputDataList)
			{
				Map<String, String> params = data.getReplaceMap();
				if (params != null && params.containsKey(TemplateConstants.QUERY_CONTEXT))
				{
					String context = params.get(TemplateConstants.QUERY_CONTEXT);
					if (context != null && context.length() > 0)
					{
						contextBuf.append(data.getReplacement() == null ? TemplateConstants.DOCUMENT_TITLE : data.getReplacement()).append("=").append(context).append(";");
					}
				}
			}
			if (contextBuf.length() > 0)
			{
				FileUtils.writeFile(contextFilePath, contextBuf.toString());
			}
		}
	}

	private static String readTemplatsFile(String templateFile) throws FileNotFoundException
	{
		return ExportUtil.readFile(new FileInputStream(templatesHome + templateFile));
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, Exception
	{
		templatesHome = "D:\\ks\\work\\RPE_2012\\Repository\\ws_rpe_main_stream_jee\\ws\\TemplateBuilder\\WebContent\\template";
		String inputJson = readTemplatsFile("/data/inputData.json");
		generate(inputJson);
	}

}
