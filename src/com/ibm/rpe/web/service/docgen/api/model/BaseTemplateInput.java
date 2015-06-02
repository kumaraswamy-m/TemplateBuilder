/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rpe.web.service.docgen.api.model;

import java.util.Map;

public class BaseTemplateInput
{

	private String elementId;
	private String outputFile;
	private String replacement;
	private boolean replaceElementId;
	private Map<String, String> replaceMap;

	public Map<String, String> getReplaceMap()
	{
		return replaceMap;
	}

	public void setReplaceMap(Map<String, String> replaceMap)
	{
		this.replaceMap = replaceMap;
	}

	public String getElementId()
	{
		return elementId;
	}

	public void setElementId(String elementId)
	{
		this.elementId = elementId;
	}

	public String getOutputFile()
	{
		return outputFile;
	}

	public void setOutputFile(String outputFile)
	{
		this.outputFile = outputFile;
	}

	public String getReplacement()
	{
		return replacement;
	}

	public void setReplacement(String replacement)
	{
		this.replacement = replacement;
	}

	public boolean isReplaceElementId()
	{
		return replaceElementId;
	}

	public void setReplaceElementId(boolean replaceElementId)
	{
		this.replaceElementId = replaceElementId;
	}
}
