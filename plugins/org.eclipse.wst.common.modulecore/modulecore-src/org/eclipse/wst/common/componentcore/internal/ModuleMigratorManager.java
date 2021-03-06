/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.datamodel.ProjectMigratorDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IProjectMigratorDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.IWorkspaceRunnableWithStatus;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;

/**
 * This has been deprecated since WTP 3.1.2 and will be deleted post WTP 3.2.
 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=292934
 * @deprecated 
 * @author jsholl
 */
public class ModuleMigratorManager {

	private static HashMap managerCache = new HashMap();
	private static HashSet migrated = new HashSet();
	private boolean migrating;
	public ModuleMigratorManager() {
		super();
	}
	public static ModuleMigratorManager getManager(IProject proj) {
		ModuleMigratorManager manager = (ModuleMigratorManager)managerCache.get(proj);
		if (manager == null) {
			manager = new ModuleMigratorManager();
			managerCache.put(proj,manager);
		}
		return manager;
	}
	private void migrateComponentsIfNecessary(IProject project, boolean multiComps) {
		
		setupAndMigrateComponentProject(project);
		
	}
	private void setupAndMigrateComponentProject(IProject proj) {
		migrated.add(proj);
		IDataModel dm = DataModelFactory.createDataModel(new ProjectMigratorDataModelProvider());
		dm.setStringProperty(IProjectMigratorDataModelProperties.PROJECT_NAME,proj.getName());
		try {
			dm.getDefaultOperation().execute(null,null);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void migrateOldMetaData(IProject aProject, final boolean multiComps) throws CoreException {
		migrating = true;
		IWorkspaceRunnableWithStatus workspaceRunnable = new IWorkspaceRunnableWithStatus(aProject) {
			public void run(IProgressMonitor pm) throws CoreException {
				IProject aProj = (IProject)this.getInfo();
				try {
					if (aProj.isAccessible() && ModuleCoreNature.isFlexibleProject(aProj)) {
						if (aProj.findMember(".wtpmodules") != null) {
							moveOldMetaDataFile(aProj);
						}
						if (needsComponentMigration(aProj,multiComps))
							migrateComponentsIfNecessary(aProj,multiComps);
					}
				} finally {
					migrating = false;
				}
			}

			private boolean needsComponentMigration(IProject aProj,boolean multiComps) throws CoreException {
				
			boolean needs = true;
			if (multiComps)
				return (needs && multiComps);
			else
				return ((aProj.findMember(StructureEdit.MODULE_META_FILE_NAME) == null) && (aProj.findMember(".settings/.component") == null)) || 
						(ProjectFacetsManager.create(aProj) == null) && needs;
			}
		};
		
		ResourcesPlugin.getWorkspace().run(workspaceRunnable, null,IWorkspace.AVOID_UPDATE,null);
		
		
		
		
	}
	private void moveMetaDataFile(IProject project) {
		IResource oldfile = project.findMember(".wtpmodules");
		if (oldfile != null && oldfile.exists()) {
			
			try {
					IFolder settingsFolder = project.getFolder(".settings");
					if (!settingsFolder.exists())
						settingsFolder.create(true,true,null);
					oldfile.move(new Path(StructureEdit.MODULE_META_FILE_NAME),true,null);
			} catch (CoreException e) {
				Platform.getLog(ModulecorePlugin.getDefault().getBundle()).log(new Status(IStatus.ERROR, ModulecorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
			}
		} 
	}
	private void moveOldMetaDataFile(IProject project) {

		try {
				moveMetaDataFile(project);
				IFolder depFolder = project.getFolder(".deployables");
				if (depFolder.exists())
					depFolder.delete(true, null);
				project.refreshLocal(IResource.DEPTH_INFINITE, null);

		} catch (Exception e) {
		}
	}
	public boolean isMigrating() {
		return migrating;
	}

}
