/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.modulecore;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelNature;
import org.eclipse.wst.common.modulecore.internal.impl.ArtifactEditModelFactory;
import org.eclipse.wst.common.modulecore.internal.impl.ModuleCoreURIConverter;
import org.eclipse.wst.common.modulecore.internal.impl.ModuleStructuralModelFactory;
import org.eclipse.wst.common.modulecore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.modulecore.internal.impl.WTPResourceFactoryRegistry;
import org.eclipse.wst.common.modulecore.internal.util.IModuleConstants;

import com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase;
import com.ibm.wtp.emf.workbench.ProjectResourceSet;
import com.ibm.wtp.emf.workbench.ProjectUtilities;

/**
 * <p>
 * Allows projects to support flexible project structures. The ModuleCoreNature manages the
 * configuration of a module structural builder that prepares WorkbenchModules for deployment.
 * </p>
 * <p>
 * To determine if a project supports flexible project structures, check for the existence of the
 * ModuleCoreNature:
 * <p>
 * <code>(ModuleCoreNature.getModuleCoreNature(project) != null)</code>
 * </p>
 * <p>
 * If the project has a ModuleCoreNature, then the project supports flexible module structures.
 * </p>
 * <p>
 * In general, clients are expected to use the utility methods available on this class to acquire
 * the ModuleCoreNature instance from a given project ({@see #getModuleCoreNature(IProject)}
 * &nbsp;or to make a flexible project flexible by adding a ModuleCoreNature (
 * {@see #addModuleCoreNatureIfNecessary(IProject, IProgressMonitor)}).
 * </p> 
 * <a name="model-discussion"/>
* <a name="module-structural-model"/>
* <p>
* Each ModuleCoreNature from a given project can provide access to the
* {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}&nbsp; of the project.
* {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}&nbsp; is a subclass of
* {@see org.eclipse.wst.common.internal.emfworkbench.integration.EditModel}&nbsp;that manages
* resources associated with the Module Structural Metamodel. As an EditModel, the
* {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel}&nbsp; references EMF resources,
* that contain EMF models -- in this case, the EMF model of <i>.wtpmodules </i> file.
* </p>
* <p>
* Clients are encouraged to use the Edit Facade pattern (via
* {@see org.eclipse.wst.common.modulecore.ArtifactEdit}&nbsp; or one if its relevant subclasses)
* to work directly with the Module Structural Metamodel.
* </p>
* <a name="artifact-editmodel"/>
* <p>
* Each ModuleCoreNature from a given project can also provide access to the
* {@see org.eclipse.wst.common.modulecore.ArtifactEditModel}&nbsp; for each
* {@see org.eclipse.wst.common.modulecore.WorkbenchModule}&nbsp; contained by the project. Like
* {@see org.eclipse.wst.common.modulecore.ModuleStructuralModel},
* {@see org.eclipse.wst.common.modulecore.ArtifactEditModel}&nbsp; is a subclass of
* {@see org.eclipse.wst.common.internal.emfworkbench.integration.EditModel}&nbsp; that contains
* EMF resources, which in turn contain the EMF models of module metadata files (such as J2EE
* deployment descriptors).
* </p>
* <p>
* The following diagram highlights the relationships of these subclasses of EditModel, and the
* relationship of the EditModel to the EMF resources. In the diagram, "MetamodelResource" and
* "MetamodelObject" are used as placeholders for the specific subclasses of
* {@see org.eclipse.emf.ecore.resource.Resource}&nbsp;and {@see org.eclipse.emf.ecore.EObject}&nbsp;
* respectively.
* </p>
* <table cellspacing="10" cellpadding="10">
* <tr>
* <td>
* <p>
* <img src="../../../../../overview/metamodel_components.jpg" />
* </p>
* </td>
* </tr>
* <tr>
* <td>
* <p>
* <i>Figure 1: A component diagram of the Module Edit Models. </i>
* </p>
* </td>
* </tr>
* </table>
* <p>
* Clients are encouraged to use the Edit Facade pattern (via
* {@see org.eclipse.wst.common.modulecore.ArtifactEdit}&nbsp; or what if its relevant subclasses)
* to work directly with the Module Structural Metamodel.
* </p>
* <a name="accessor-key"/>
* <p>
* All EditModels have a lifecycle that must be enforced to keep the resources loaded that are in
* use, and to unload resources that are not in use. To access an EditModel, clients are required to
* supply an object token referred to as an accessor key. The accessor key allows the framework to
* better track which clients are using the EditModel, and to ensure that only a client which has
* accessed the EditModel with an accessor key may invoke save*()s on that EditModel.
* </p>
 * <p>
 * The following class is experimental until fully documented.
 * </p> 
 * @see org.eclipse.wst.common.modulecore.ModuleCore
 * @see org.eclipse.wst.common.modulecore.ModuleCore#getModuleCoreForRead(IProject)
 * @see org.eclipse.wst.common.modulecore.ModuleCore#getModuleCoreForWrite(IProject)
 * @see org.eclipse.wst.common.modulecore.ArtifactEdit
 * @see org.eclipse.wst.common.modulecore.ArtifactEdit#getArtifactEditForRead(WorkbenchModule)
 * @see org.eclipse.wst.common.modulecore.ArtifactEdit#getArtifactEditForWrite(WorkbenchModule)
 */
public class ModuleCoreNature extends EditModelNature implements IProjectNature, IModuleConstants {

	/**
	 * <p>
	 * Find and return the ModuleCoreNature of aProject, if available.
	 * <p>
	 * <b>This method may return null. </b>
	 * </p>
	 * 
	 * @param aProject
	 *            An accessible project
	 * @return The ModuleCoreNature of aProject, if it exists
	 */
	public static ModuleCoreNature getModuleCoreNature(IProject aProject) {
		try {
			if (aProject.isAccessible())
				return (ModuleCoreNature) aProject.getNature(IModuleConstants.MODULE_NATURE_ID);
		} catch (CoreException e) {
		}
		return null;
	}

	/**
	 * <p>
	 * Adds a ModuleCoreNature to the project.
	 * </p>
	 * <p>
	 * <b>This method may return null. </b>
	 * </p>
	 * 
	 * @param aProject
	 *            A accessible project.
	 * @param aMonitor
	 *            A progress monitor to track the time to completion
	 * @return The ModuleCoreNature of aProject, if it exists
	 */
	public static ModuleCoreNature addModuleCoreNatureIfNecessary(final IProject aProject, IProgressMonitor aMonitor) {
		try {
			if (aProject.hasNature(IModuleConstants.MODULE_NATURE_ID))
				return getModuleCoreNature(aProject);

			/* To avoid potential deadlocks, we need to execute the following as a Job */
			Job addNatureJob = new Job("Add ModuleCore Nature") {
				protected IStatus run(IProgressMonitor monitor) {
					try {
						IProjectDescription description = aProject.getDescription();

						String[] currentNatureIds = description.getNatureIds();
						String[] newNatureIds = new String[currentNatureIds.length + 1];
						System.arraycopy(currentNatureIds, 0, newNatureIds, 0, currentNatureIds.length);
						newNatureIds[currentNatureIds.length] = IModuleConstants.MODULE_NATURE_ID;
						description.setNatureIds(newNatureIds);
						aProject.setDescription(description, monitor);
					} catch (CoreException e) {
						e.printStackTrace();
					}
					return Status.OK_STATUS;
				}
			};
			/* Because we want to return the nature, we will wait for the Job to finish executing */
			final boolean[] mutex = new boolean[]{true};
			addNatureJob.addJobChangeListener(new JobChangeAdapter() {
				public void done(IJobChangeEvent event) {
					mutex[0] = false;
				}
			});
			addNatureJob.schedule();
			aMonitor.beginTask("Add ModuleCore Nature", 5);
			while (mutex[0]) {
				try {
					Thread.sleep(200);
					aMonitor.worked(1);
				} catch (InterruptedException ie) {
				}
			}
			aMonitor.done();

		} catch (CoreException e) {
			e.printStackTrace();
		}
		/* Return the new nature */
		return getModuleCoreNature(aProject);
	}

	/**
	 * <p>
	 * Return a {@see ModuleStructuralModel}&nbsp;for read-only access.
	 * </p>
	 * <p>
	 * Clients are encouraged to use {@see ModuleCore#getModuleCoreForRead(IProject)}&nbsp;to work
	 * with the Module Structural Metamodels of flexible projects.
	 * </p>
	 * <p>
	 * See the discussion what a {@see ModuleStructuralModel}&nbsp; is and <a
	 * href="#module-structural-model">how it relates to the Module Structural Metamodel </a>.
	 * </p>
	 * <p>
	 * Also see the discussion of <a href="#accessor-key">the purpose of an accessor key </a>.
	 * </p>
	 * 
	 * @param anAccessorKey
	 *            Typically client supplies the object that invoked this method, or a proxy (
	 *            <code>new Object()</code>) in the case of other static methods requesting a
	 *            {@see ModuleStructuralModel}.
	 * @return A {@see ModuleStructuralModel}for the project of the current nature.
	 */
	public ModuleStructuralModel getModuleStructuralModelForRead(Object anAccessorKey) {
		return (ModuleStructuralModel) getEditModelForRead(ModuleStructuralModelFactory.MODULE_STRUCTURAL_MODEL_ID, anAccessorKey);
	}

	/**
	 * <p>
	 * Return a {@see ModuleStructuralModel}&nbsp;for write access.
	 * </p>
	 * <p>
	 * Clients are encouraged to use {@see ModuleCore#getModuleCoreForWrite(IProject)}&nbsp;to work
	 * with the Module Structural Metamodels of flexible projects.
	 * </p>
	 * <p>
	 * See the discussion what a {@see ModuleStructuralModel}&nbsp; is and <a
	 * href="#module-structural-model">how it relates to the Module Structural Metamodel </a>.
	 * </p>
	 * <p>
	 * Also see the discussion of <a href="#accessor-key">the purpose of an accessor key </a>.
	 * </p>
	 * 
	 * @param anAccessorKey
	 *            Typically client supplies the object that invoked this method, or a proxy (
	 *            <code>new Object()</code>) in the case of other static methods requesting a
	 *            {@see ModuleStructuralModel}.
	 * @return A {@see ModuleStructuralModel}for the project of the current nature.
	 */
	public ModuleStructuralModel getModuleStructuralModelForWrite(Object anAccessorKey) {
		return (ModuleStructuralModel) getEditModelForWrite(ModuleStructuralModelFactory.MODULE_STRUCTURAL_MODEL_ID, anAccessorKey);
	}

	/**
	 * <p>
	 * Returns an {@see ArtifactEditModel}&nbsp; to work with the underlying content of an
	 * individual {@see WorkbenchModule}&nbsp; contained in the project. {@see ArtifactEditModel}s
	 * are used to manipulate the content models for individual {@see WorkbenchModule}s. In
	 * general, a content model will contain an EMF representation of the module's relevant
	 * deployment descriptor, and possibly other EMF resources as well.
	 * </p>
	 * <p>
	 * {@see ArtifactEditModel}s that are returned from this method may not be used to modify and
	 * persist changes to the underlying Module Content Metamodel. Clients that need to make changes
	 * to the underlying Module Content Module, and that choose to work directly with the
	 * {@see ArtifactEditModel}&nbsp; should use {@see #getArtifactEditModelForWrite(URI, Object)}.
	 * </p>
	 * <p>
	 * Clients are encouraged to use {@see ArtifactEdit}&nbsp;or one of its relevant subclasses to
	 * work with the module content model, instead of working with directly with the EditModel:
	 * </p>
	 * <p>
	 * <code>ArtifactEdit editFacade = ArtifactEdit.getArtifactEditForRead(aWorkbenchModule);</code>
	 * </p>
	 * <p>
	 * When a client is aware of the underlying type of module, more specific Edit Facades may be
	 * acquired:
	 * </p>
	 * <p>
	 * <code>WebEdit editFacade = WebEdit.getWebEditForRead(aWorkbenchModule);</code>
	 * </p>
	 * <p>
	 * If a particular Edit Facade is not applicable to the supplied {@see WorkbenchModule}, then
	 * <b>null </b> will be returned.
	 * </p>
	 * 
	 * <p>
	 * See the discussion what a {@see ArtifactEditModel}&nbsp; is and <a
	 * href="#artifact-editmodel">how it relates to the Module Content Metamodel </a>.
	 * </p>
	 * <p>
	 * Also see the discussion of <a href="#accessor-key">the purpose of an accessor key </a>.
	 * </p>
	 * 
	 * @param aModuleURI
	 *            A fully qualified URI of the form "module:/resource/ <project-name>/
	 *            <module-deployed-name>"
	 * @param anAccessorKey
	 *            Typically client supplies the object that invoked this method, or a proxy (
	 *            <code>new Object()</code>) in the case of other static methods requesting a
	 *            {@see ModuleStructuralModel}.
	 * @return
	 * @see ArtifactEdit
	 * @see ArtifactEdit#getArtifactEditForRead(WorkbenchModule)
	 */
	public ArtifactEditModel getArtifactEditModelForRead(URI aModuleURI, Object anAccessorKey) {
		Map params = new HashMap();
		params.put(ArtifactEditModelFactory.PARAM_MODULE_URI, aModuleURI);
		return (ArtifactEditModel) getEditModelForRead(getArtifactEditModelId(aModuleURI), anAccessorKey, params);
	}

	/**
	 * <p>
	 * Returns an {@see ArtifactEditModel}&nbsp; to work with the underlying content of an
	 * individual {@see WorkbenchModule}&nbsp; contained in the project. {@see ArtifactEditModel}s
	 * are used to manipulate the content models for individual {@see WorkbenchModule}s. In
	 * general, a content model will contain an EMF representation of the module's relevant
	 * deployment descriptor, and possibly other EMF resources as well.
	 * </p>
	 * 
	 * <p>
	 * {@see ArtifactEditModel}s that are returned from this method may be used to modify and
	 * persist changes to the underlying Module Content Metamodel. For clients that do not expect to
	 * make modifications are encouraged to use {@see #getArtifactEditModelForRead(URI, Object)}
	 * &nbsp; instead.
	 * </p>
	 * <p>
	 * Clients are encouraged to use {@see ArtifactEdit}&nbsp;or one of its relevant subclasses to
	 * work with the module content model, instead of working with directly with the EditModel:
	 * </p>
	 * <p>
	 * <code>ArtifactEdit editFacade = ArtifactEdit.getArtifactEditForWrite(aWorkbenchModule);</code>
	 * </p>
	 * <p>
	 * When a client is aware of the underlying type of module, more specific Edit Facades may be
	 * acquired:
	 * </p>
	 * <p>
	 * <code>WebEdit editFacade = WebEdit.getWebEditForWrite(aWorkbenchModule);</code>
	 * </p>
	 * <p>
	 * If a particular Edit Facade is not applicable to the supplied {@see WorkbenchModule}, then
	 * <b>null </b> will be returned.
	 * </p>
	 * 
	 * <p>
	 * See the discussion what a {@see ArtifactEditModel}&nbsp; is and <a
	 * href="#artifact-editmodel">how it relates to the Module Content Metamodel </a>.
	 * </p>
	 * <p>
	 * Also see the discussion of <a href="#accessor-key">the purpose of an accessor key </a>.
	 * </p>
	 * 
	 * @param aModuleURI
	 *            A fully qualified URI of the form "module:/resource/ <project-name>/
	 *            <module-deployed-name>"
	 * @param anAccessorKey
	 *            Typically client supplies the object that invoked this method, or a proxy (
	 *            <code>new Object()</code>) in the case of other static methods requesting a
	 *            {@see ModuleStructuralModel}.
	 * @return
	 * @see ArtifactEdit
	 * @see ArtifactEdit#getArtifactEditForRead(WorkbenchModule)
	 */
	public ArtifactEditModel getArtifactEditModelForWrite(URI aModuleURI, Object anAccessorKey) {
		Map params = new HashMap();
		params.put(ArtifactEditModelFactory.PARAM_MODULE_URI, aModuleURI);
		return (ArtifactEditModel) getEditModelForWrite(getArtifactEditModelId(aModuleURI), anAccessorKey, params);
	}

	public String getNatureID() {
		return MODULE_NATURE_ID;
	}


	/**
	 * <p>
	 * This method should not be invoked by clients.
	 * </p>
	 * 
	 * @see com.ibm.wtp.emf.workbench.IEMFContextContributor#primaryContributeToContext(com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase)
	 */
	public void primaryContributeToContext(EMFWorkbenchContextBase aNature) {
		if (emfContext == aNature)
			return;
		emfContext = aNature;
		getEmfContext().setDefaultToMOF5Compatibility(true);
		// Overriding superclass to use our own URI converter, which knows about binary projects
		ProjectResourceSet projectResourceSet = aNature.getResourceSet();
		projectResourceSet.setResourceFactoryRegistry(WTPResourceFactoryRegistry.INSTANCE);
		createURIConverter(getProject(), projectResourceSet);
		// initializeCacheEditModel();
		// addAdapterFactories(set);
		// set.getSynchronizer().addExtender(this); // added so we can be informed of closes to the
		// new J2EEResourceDependencyRegister(set); // This must be done after the URIConverter is

	}
	
	/**
	 * @param project
	 * @return
	 */
	private URIConverter createURIConverter(IProject aProject, ProjectResourceSet aResourceSet ) {
		ModuleCoreURIConverter uriConverter = new ModuleCoreURIConverter(aProject, aResourceSet.getSynchronizer()); 
		uriConverter.addInputContainer(getProject());
		return uriConverter;
	}
	

	/**
	 * <p>
	 * This method should not be invoked by clients.
	 * </p>
	 */
	public ResourceSet getResourceSet() {
		return getEmfContextBase().getResourceSet();
	}

	/**
	 * <p>
	 * This method should not be invoked by clients.
	 * </p>
	 * 
	 * @see com.ibm.wtp.emf.workbench.IEMFContextContributor#secondaryContributeToContext(com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase)
	 */
	public void secondaryContributeToContext(EMFWorkbenchContextBase aNature) {
	}

	/**
	 * <p>
	 * This method should not be invoked by clients.
	 * </p>
	 * 
	 * @see com.ibm.wtp.emf.workbench.nature.EMFNature#configure()
	 */
	public void configure() throws CoreException {
		super.configure();
		addDeployableProjectBuilder();
		addLocalDependencyResolver();
	}

	protected String getPluginID() {
		return MODULE_PLUG_IN_ID;
	}

	private void addDeployableProjectBuilder() throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] builderCommands = description.getBuildSpec();
		boolean previouslyAdded = false;

		for (int i = 0; i < builderCommands.length; i++) {
			if (builderCommands[i].getBuilderName().equals(DEPLOYABLE_MODULE_BUILDER_ID))
				// builder already added no need to add again
				previouslyAdded = true;
			break;
		}
		if (!previouslyAdded) {
			// builder not found, must be added
			ICommand command = description.newCommand();
			command.setBuilderName(DEPLOYABLE_MODULE_BUILDER_ID);
			ICommand[] updatedBuilderCommands = new ICommand[builderCommands.length + 1];
			System.arraycopy(builderCommands, 0, updatedBuilderCommands, 1, builderCommands.length);
			updatedBuilderCommands[0] = command;
			description.setBuildSpec(updatedBuilderCommands);
			project.setDescription(description, null);
		}
	}

	private void addLocalDependencyResolver() throws CoreException {
		ProjectUtilities.addToBuildSpec(LOCAL_DEPENDENCY_RESOLVER_ID, getProject());
	}

	private String getArtifactEditModelId(URI aModuleURI) {
		ModuleStructuralModel structuralModel = null;
		try {
			structuralModel = getModuleStructuralModelForRead(Thread.currentThread());
			ModuleCore editUtility = (ModuleCore) structuralModel.getAdapter(ModuleCore.ADAPTER_TYPE);
			WorkbenchModule module = editUtility.findWorkbenchModuleByDeployName(ModuleURIUtil.getDeployedName(aModuleURI));
			return module.getModuleType().getModuleTypeId();
		} catch (UnresolveableURIException uurie) {
			// Ignore
		} finally {
			if (structuralModel != null)
				structuralModel.releaseAccess(Thread.currentThread());
		}
		return null;
	}


}