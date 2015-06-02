/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rpe.web.service.docgen.api.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement
public class Template
{
	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "xmlPath")
	private String xmlPath;

	@XmlElement(name = "xsdPath")
	private String xsdPath;

	@XmlElement(name = "schema")
	private String schema;

	@XmlElement(name = "query")
	private String query;

	@XmlElement(name = "isTitleQuery")
	private boolean isTitleQuery;

	@XmlElement(name = "title")
	private String title;

	@XmlElement(name = "toc")
	private boolean toc;

	@XmlElement(name = "headerText")
	private String headerText;

	@XmlElement(name = "footerText")
	private String footerText;

	@XmlElement(name = "pageOrientation")
	private String pageOrientation;

	@XmlElement(name = "container")
	List<Container> container = null;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getXmlPath()
	{
		return xmlPath;
	}

	public void setXmlPath(String xmlPath)
	{
		this.xmlPath = xmlPath;
	}

	public String getXsdPath()
	{
		return xsdPath;
	}

	public void setXsdPath(String xsdPath)
	{
		this.xsdPath = xsdPath;
	}

	public String getSchema()
	{
		return schema;
	}

	public void setSchema(String schema)
	{
		this.schema = schema;
	}

	public String getQuery()
	{
		return query;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}

	public boolean isTitleQuery()
	{
		return isTitleQuery;
	}

	public void setTitleQuery(boolean isTitleQuery)
	{
		this.isTitleQuery = isTitleQuery;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public boolean isToc()
	{
		return toc;
	}

	public void setToc(boolean toc)
	{
		this.toc = toc;
	}

	public String getHeaderText()
	{
		return headerText;
	}

	public void setHeaderText(String headerText)
	{
		this.headerText = headerText;
	}

	public String getFooterText()
	{
		return footerText;
	}

	public void setFooterText(String footerText)
	{
		this.footerText = footerText;
	}

	public String getPageOrientation()
	{
		return pageOrientation;
	}

	public void setPageOrientation(String pageOrientation)
	{
		this.pageOrientation = pageOrientation;
	}

	public List<Container> getContainer()
	{
		return container;
	}

	public void setContainer(List<Container> container)
	{
		this.container = container;
	}

	@JsonIgnore
	public void addContainer(Container container)
	{
		if (this.container == null)
		{
			this.container = new ArrayList<Template.Container>();
		}

		this.container.add(container);
	}

	@XmlRootElement(name = "container")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Container
	{
		@XmlElement(name = "type")
		private String type;

		@XmlElement(name = "title")
		private String title;

		@XmlElement(name = "titleStyle")
		private String titleStyle;

		@XmlElement(name = "isTitleQuery")
		private boolean isTitleQuery;

		@XmlElement(name = "query")
		private String query;

		@XmlElementWrapper(name = "headerLabels")
		@XmlElement(name = "label")
		private List<String> headerLabels;

		@XmlElementWrapper(name = "queryAttributes")
		@XmlElement(name = "attribute")
		private List<String> queryAttributes;

		public String getType()
		{
			return type;
		}

		public void setType(String type)
		{
			this.type = type;
		}

		public String getTitle()
		{
			return title;
		}

		public void setTitle(String title)
		{
			this.title = title;
		}

		public String getTitleStyle()
		{
			return titleStyle;
		}

		public void setTitleStyle(String titleStyle)
		{
			this.titleStyle = titleStyle;
		}

		public boolean isTitleQuery()
		{
			return isTitleQuery;
		}

		public void setTitleQuery(boolean isTitleQuery)
		{
			this.isTitleQuery = isTitleQuery;
		}

		public String getQuery()
		{
			return query;
		}

		public void setQuery(String query)
		{
			this.query = query;
		}

		public List<String> getHeaderLabels()
		{
			return headerLabels;
		}

		public void setHeaderLabels(List<String> headerLabels)
		{
			this.headerLabels = headerLabels;
		}

		public List<String> getQueryAttributes()
		{
			return queryAttributes;
		}

		public void setQueryAttributes(List<String> queryAttributes)
		{
			this.queryAttributes = queryAttributes;
		}

	}
}
