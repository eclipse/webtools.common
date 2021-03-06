/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.operations;


import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.validation.internal.RegistryConstants;
import org.eclipse.wst.validation.internal.ValidatorMetaData;


/**
 * Implemented Validators must not be called directly by anyone other than instances of
 * ValidationOperation, because some initialization of the validator, and handling of error
 * conditions, is done in the operation. The initialization is separated because some of the
 * information needed to initialize the validator (i.e., the project) isn't known until runtime.
 * 
 * Instances of this operation run every enabled validator (both full and incremental) on the
 * project.
 * 
 * This operation is not intended to be subclassed outside of the validation framework.
 */
public class EnabledValidatorsOperation extends ValidatorSubsetOperation {
	

	/**
	 * @deprecated Will be removed in Milestone 3. Use EnabledValidatorsOperation(IProject, boolean)
	 */
	public EnabledValidatorsOperation(IProject project) {
		this(project, DEFAULT_ASYNC);
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use EnabledValidatorsOperation(IProject, int,
	 *             boolean, boolean)
	 */
	public EnabledValidatorsOperation(IProject project, int ruleGroup) {
		this(project, ruleGroup, DEFAULT_FORCE, DEFAULT_ASYNC);
	}

	/**
	 * @deprecated Will be removed in Milestone 3. Use EnabledValidatorsOperation(IProject, int,
	 *             boolean, boolean)
	 */
	public EnabledValidatorsOperation(IProject project, int ruleGroup, boolean force) {
		this(project, ruleGroup, force, DEFAULT_ASYNC);
	}

	/**
	 * Run all enabled validators on the project.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all validators implementing IValidatorJob interface 
	 * in the background validation thread, if async is false, it would run in the main thread. 
	 * All validators implementing IValidator interface will run in in the main thread regardless of this flag.
	 */
	public EnabledValidatorsOperation(IProject project, boolean async) {
		this(project, RegistryConstants.ATT_RULE_GROUP_DEFAULT, DEFAULT_FORCE, async);
	}
	
	/**
	 * Run all enabled validators on the project.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all validators implementing IValidatorJob interface 
	 * in the background validation thread, if async is false, it would run in the main thread. 
	 * All validators implementing IValidator interface will run in in the main thread regardless of this flag.
	 */
	public EnabledValidatorsOperation(IProject project, IWorkbenchContext aWorkbenchContext, boolean async) {
		this(project, aWorkbenchContext, RegistryConstants.ATT_RULE_GROUP_DEFAULT, DEFAULT_FORCE, async);
	}
	
	/**
	 * Run all enabled validators on the project with the identified ruleGroup.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all validators implementing IValidatorJob interface 
	 * in the background validation thread, if async is false, it would run in the main thread. 
	 * All validators implementing IValidator interface will run in in the main thread regardless of this flag.
	 */
	public EnabledValidatorsOperation(IProject project, IWorkbenchContext aWorkbenchContext, int ruleGroup, boolean force, boolean async) {
		this(project, aWorkbenchContext, ValidatorManager.getManager().getEnabledValidators(project), ruleGroup, force, async);
	}
	

	/**
	 * Run all enabled validators on the project with the identified ruleGroup.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all validators implementing IValidatorJob interface 
	 * in the background validation thread, if async is false, it would run in the main thread. 
	 * All validators implementing IValidator interface will run in in the main thread regardless of this flag.
	 */
	public EnabledValidatorsOperation(IProject project, int ruleGroup, boolean force, boolean async) {
		this(project, ValidatorManager.getManager().getEnabledValidators(project), ruleGroup, force, async);
	}

	/**
	 * Run the identified validators on the project.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all validators implementing IValidatorJob interface 
	 * in the background validation thread, if async is false, it would run in the main thread. 
	 * All validators implementing IValidator interface will run in in the main thread regardless of this flag.
	 */
	protected EnabledValidatorsOperation(IProject project, Set<ValidatorMetaData> enabledValidators, boolean async) {
		this(project, enabledValidators, RegistryConstants.ATT_RULE_GROUP_DEFAULT, DEFAULT_FORCE, async); 
		// true = force validation to run whether or not auto-validate is on
	}
	
	/**
	 * Run the identified validators on the project.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all validators implementing IValidatorJob interface 
	 * in the background validation thread, if async is false, it would run in the main thread. 
	 * All validators implementing IValidator interface will run in in the main thread regardless of this flag.
	 */
	protected EnabledValidatorsOperation(IProject project,IWorkbenchContext aWorkbenchContext, 
		Set<ValidatorMetaData> enabledValidators, boolean async) {
		this(project,aWorkbenchContext, enabledValidators, RegistryConstants.ATT_RULE_GROUP_DEFAULT, DEFAULT_FORCE, async); 
		// true = force validation to run whether or not auto-validate is on
	}

	/**
	 * Run the identified validators on the project with the ruleGroup.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all validators implementing IValidatorJob interface 
	 * in the background validation thread, if async is false, it would run in the main thread. 
	 * All validators implementing IValidator interface will run in in the main thread regardless of this flag.
	 */
	protected EnabledValidatorsOperation(IProject project, Set<ValidatorMetaData> enabledValidators, 
		int ruleGroup, boolean force, boolean async) {
		
		super(project, force, ruleGroup, async);
		setEnabledValidators(enabledValidators);
	}
	
	/**
	 * Run the identified validators on the project with the ruleGroup.
	 * 
	 * IProject must exist and be open.
	 * 
	 * If async is true, the validation will run all validators implementing IValidatorJob interface 
	 * in the background validation thread, if async is false, it would run in the main thread. 
	 * All validators implementing IValidator interface will run in in the main thread regardless of this flag.
	 */
	protected EnabledValidatorsOperation(IProject project, IWorkbenchContext aWorkbenchContext, 
		Set<ValidatorMetaData> enabledValidators, int ruleGroup, boolean force, boolean async) {
		super(project,aWorkbenchContext,force, ruleGroup, async);
		setEnabledValidators(enabledValidators);
	}
}
