/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rpe.web.service.docgen.utils;

@SuppressWarnings("nls")
public class TemplateConstants
{
	
	public static String TEMPLATES_HOME = "TEMPLATES_HOME";
	public static String PATH_TEMPLATE = "template";
	
	// XML file constants
	public static String BASE_TEMPLATE_XML_PATH = "/baseTemplates/base_template.xml";

	// paragraph
	public static String PARAGRAPH_CONTAINER_XML_PATH = "/baseTemplates/paragraphContainer.xml";
	public static String PARAGRAPH_ROW_XML_PATH = "/baseTemplates/paragraphRow.xml";

	// table
	public static String TABLE_CONTAINER_XML_PATH = "/baseTemplates/tableContainer.xml";
	public static String TABLE_HEADER_CELL_XML_PATH = "/baseTemplates/tableHeaderCell.xml";
	public static String TABLE_QUERY_CELL_XML_PATH = "/baseTemplates/tableQueryCell.xml";

	// title
	public static String STATIC_TITLE_XML_PATH = "/baseTemplates/staticTitle.xml";
	public static String QUERY_TITLE_XML_PATH = "/baseTemplates/queryTitle.xml";

	// table of contents
	public static String TOC_XML_PATH = "/baseTemplates/tocContainer.xml";
	
	public static String QUERY_CONTEXT_TEXT_PATH = "/baseTemplates/queryContextInfo.txt";

	// document specification
	public static String BASE_DOCSPEC_PATH = "/baseTemplates/base_docspec.dsx"; //$NON-NLS-1$

	// place holder constants
	// query
	public static final String TITLE_LEVEL_QUERY = "%TITLE_LEVEL_QUERY%"; //$NON-NLS-1$
	public static final String DATA_LEVEL_QUERY = "%DATA_LEVEL_QUERY%"; //$NON-NLS-1$

	// schema
	public static final String SCHEMA_LABEL = "%SCHEMA_LABEL%"; //$NON-NLS-1$

	// title
	public static final String TITLE_EXPRESSION = "%TITLE_EXPRESSION%";
	public static final String TITLE = "%TITLE%"; //$NON-NLS-1$
	public static final String DOCUMENT_TITLE = "%DOCUMENT_TITLE%"; //$NON-NLS-1$
	public static final String QUERY_CONTEXT = "%QUERY_CONTEXT%"; //$NON-NLS-1$

	public static final String STYLE_NAME = "%STYLE_NAME%";

	// master page
	public static final String MASTERPAGE_HEADER_TEXT = "%MASTERPAGE_HEADER_TEXT%";
	public static final String MASTERPAGE_FOOTER_TEXT = "%MASTERPAGE_FOOTER_TEXT%";
	public static final String MASTERPAGE_ORIENTATION = "%MASTERPAGE_ORIENTATION%";
	public static final String ELEMENT_ID = "%ELEMENT_ID%"; //$NON-NLS-1$

	public static final String TOC_CONTAINER = "%TOC_CONTAINER%";
	public static final String TABLE_CONTAINER = "%TABLE_CONTAINER%";
	public static final String PARAGRAPH_CONTAINER = "%PARAGRAPH_CONTAINER%";

	public static final String TEMPLATE_NAME_PATH = "%TEMPLATE_PATH_NAME%"; //$NON-NLS-1$
	public static final String DATA_SOURCE_URL = "%DATA_SOURCE_URL%"; //$NON-NLS-1$

	// table
	public static final String STATIC_TITLE = "%STATIC_TITLE%";
	public static final String TABLE_CELL_ATTRIBUTE_LABEL = "%TABLE_CELL_ATTRIBUTE_LABEL%";
	public static final String TABLE_CELL_ATTRIBUTE_QUERY = "%TABLE_CELL_ATTRIBUTE_QUERY%";
	public static final String TABLE_SINGLE_CELL_LABEL = "%TABLE_SINGLE_CELL_LABEL%";
	public static final String TABLE_SINGLE_CELL_QUERY = "%TABLE_SINGLE_CELL_QUERY%";

	// paragraph
	public static final String PARAGRAPH_ATTRIBUTE_LABEL = "%PARAGRAPH_ATTRIBUTE_LABEL%";
	public static final String PARAGRAPH_ATTRIBUTE_QUERY = "%PARAGRAPH_ATTRIBUTE_QUERY%";
	public static final String PARAGRAPH_SINGLE_ROW = "%PARAGRAPH_SINGLE_ROW%";

	public static final String PARAGRAPH_TYPE = "paragraph";
	public static final String TABLE_TYPE = "table";
}
