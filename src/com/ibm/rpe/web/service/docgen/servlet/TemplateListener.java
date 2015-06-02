/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © © Copyright IBM Corporation 2014, 2015. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
package com.ibm.rpe.web.service.docgen.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.ibm.rpe.web.service.docgen.utils.CommonUtils;
import com.ibm.rpe.web.service.docgen.utils.TemplateConstants;

/**
 * Application Lifecycle Listener implementation class L
 * 
 */
@WebListener
public class TemplateListener implements ServletContextListener
{
	public TemplateListener()
	{

	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		String home = sce.getServletContext().getRealPath("/"); //$NON-NLS-1$

		home = CommonUtils.removeTrailingCharacter(home, "/"); //$NON-NLS-1$
		home = CommonUtils.removeTrailingCharacter(home, "\\"); //$NON-NLS-1$

		sce.getServletContext().setAttribute(TemplateConstants.TEMPLATES_HOME, home);

		System.out.println("TEMPLATES HOME is: " + home); //$NON-NLS-1$
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
	}

}
