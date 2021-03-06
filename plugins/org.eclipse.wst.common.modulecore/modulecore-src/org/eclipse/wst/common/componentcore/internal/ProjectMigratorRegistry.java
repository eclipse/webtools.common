/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.componentcore.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.RegistryReader;

/**
 * This has been deprecated since WTP 3.1.2 and will be deleted post WTP 3.2.
 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=292934
 * @deprecated 
 * @author jsholl
 */
public class ProjectMigratorRegistry extends RegistryReader {

	static final String EXTENSION_NAME = "ComponentProjectMigrator"; //$NON-NLS-1$
	static final String ELEMENT_MIGRATOR_EXTENSION = "migratorExtension"; //$NON-NLS-1$
	static final String MIGRATOR_CLASS = "class"; //$NON-NLS-1$
	private List projectMigrators = new ArrayList();
	private static ProjectMigratorRegistry INSTANCE = null;

	public ProjectMigratorRegistry() {
		super(ModulecorePlugin.PLUGIN_ID, EXTENSION_NAME);
	}

	public static ProjectMigratorRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ProjectMigratorRegistry();
			INSTANCE.readRegistry();
		}
		return INSTANCE;
	}

	/**
	 * readElement() - parse and deal w/ an extension like: <earModuleExtension extensionClass =
	 * "com.ibm.etools.web.plugin.WebModuleExtensionImpl"/>
	 */
	public boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(ELEMENT_MIGRATOR_EXTENSION))
			return false;

		IComponentProjectMigrator migrator = null;
		try {
			migrator = (IComponentProjectMigrator) element.createExecutableExtension(MIGRATOR_CLASS);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (migrator != null)
			addModuleExtension(migrator);
		return true;
	}

	public IComponentProjectMigrator[] getProjectMigrators(){
		return (IComponentProjectMigrator[])projectMigrators.toArray(new IComponentProjectMigrator[projectMigrators.size()]);
	}
	private void addModuleExtension(IComponentProjectMigrator ext) {
		projectMigrators.add(ext);
	}

}
