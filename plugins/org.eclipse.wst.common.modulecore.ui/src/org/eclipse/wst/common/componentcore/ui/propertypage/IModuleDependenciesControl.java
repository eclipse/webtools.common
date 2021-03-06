/******************************************************************************
 * Copyright (c) 2009, 2012 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 *    
 *
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.propertypage;

import org.eclipse.swt.widgets.Composite;

/**
 * This represents one control which may be present on a page
 * and requires to listen in to the specific lifecycle 
 * events of the page. 
 */
public interface IModuleDependenciesControl {
	/**
	 * Creates the Composite associated with this control.
	 * @param parent Parent Composite.
	 * @return Composite for the control.
	 */
	Composite createContents(Composite parent);
	
	/**
	 * Called when the property page's <code>performOk()</code> method is called.
	 * @return
	 */
	boolean performOk();
	
	/**
	 * Called when the property page's <code>performDefaults()</code> method is called.
	 * @return
	 */
	void performDefaults();
	
	/**
	 * Called when the property page's <code>performCancel()</code> method is called.
	 * @return
	 */
	boolean performCancel();
	
	/**
	 * Called when the property page's <code>performApply()</code> method is called.
	 * @return
	 */
	void performApply();
	
	/**
	 * Called when the property page's <code>setVisible()</code> method is called.
	 * @return
	 */
	void setVisible(boolean visible);
	
	/**
	 * Called when the property page's <code>dispose()</code> method is called.
	 * @return
	 */
	void dispose();
}
