/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.web.template.generate.utils.xsd1;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.inst2xsd.Inst2Xsd;
import org.apache.xmlbeans.impl.inst2xsd.Inst2XsdOptions;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;

public class XmltoXSD
{
	

	public static void convertXmlToXsd(String xmlPath, String pathToSaveXSD) throws XmlException, IOException
	{
		Inst2XsdOptions inst2XsdOptions = new Inst2XsdOptions();
		inst2XsdOptions.setDesign(Inst2XsdOptions.DESIGN_RUSSIAN_DOLL);
		// inst2XsdOptions.setUseEnumerations(Inst2XsdOptions.ENUMERATION_NEVER);
		File[] xmlFiles = new File[1];
		xmlFiles[0] = new File(xmlPath);

		XmlObject[] xmlInstance = new XmlObject[1];
		xmlInstance[0] = XmlObject.Factory.parse(xmlFiles[0]);

		SchemaDocument[] schemaDocuments = Inst2Xsd.inst2xsd(xmlInstance, inst2XsdOptions);
		if (schemaDocuments != null && schemaDocuments.length > 0)
		{
			SchemaDocument schema = schemaDocuments[0];
			if (pathToSaveXSD == null || pathToSaveXSD.isEmpty())
			{
				System.out.println(schema.toString());
			}
			else
			{
				FileUtils.writeStringToFile(new File(pathToSaveXSD), schema.toString());
			}
		}
	}

}
