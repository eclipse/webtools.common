/*******************************************************************************
 * Copyright (c) 2009, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     
 *
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.common.componentcore.ui.messages"; //$NON-NLS-1$
	public static String ModuleAssemblyRootPageDescription;
	public static String ErrorCheckingFacets;
	public static String ErrorNotVirtualComponent;
	public static String DeploymentAssemblyVerifierHelper_0;
	public static String DeployPathColumn;
	public static String SourceColumn;
	public static String InternalLibJarWarning;
	public static String AddFolder;
	public static String AddFolderElipses;
	public static String AddFolderMappings;
	public static String AddEllipsis;
	public static String EditEllipsis;
	public static String RemoveSelected;
	public static String JarTitle;
	public static String JarDescription;
	public static String ExternalJarTitle;
	public static String ExternalJarDescription;
	public static String Browse;
	public static String NewReferenceTitle;
	public static String NewReferenceDescription;
	public static String NewReferenceWizard;
	public static String ProjectReferenceTitle;
	public static String ProjectReferenceDescription;
	public static String VariableReferenceTitle;
	public static String VariableReferenceDescription;
	public static String WizardError;
	public static String ProjectConversionError;
	public static String ExistingFolderError;
	public static String Revert;
	public static String ErrorEntryNotFound;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
