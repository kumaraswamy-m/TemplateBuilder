/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rpe.web.service.docgen.utils;

import java.io.File;
import java.io.FileOutputStream;

import com.ibm.web.template.generate.utils.xsd.XsdGen;

public class XsdGeneration
{
	public XsdGeneration()
	{
	}

	public static String generateXSD(String xmlPath, String outputFile) throws Exception
	{
		File file = new File(xmlPath);
		File output = new File(outputFile);
		FileOutputStream fos = new FileOutputStream(output);
		new XsdGen().parse(file).write(fos);
		return output.getAbsolutePath();
	}
}