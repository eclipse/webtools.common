/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: EMFWorkbenchResourceHandler.java,v $$
 *  $$Revision: 1.2 $$  $$Date: 2005/02/15 23:04:14 $$ 
 */
package org.eclipse.jem.internal.util.emf.workbench.nls;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class EMFWorkbenchResourceHandler {

	private static ResourceBundle fgResourceBundle;

	/**
	 * Returns the resource bundle used by all classes in this Project
	 */
	public static ResourceBundle getResourceBundle() {
		try {
			return ResourceBundle.getBundle("emfworkbench");//$NON-NLS-1$
		} catch (MissingResourceException e) {
			// does nothing - this method will return null and
			// getString(String, String) will return the key
			// it was called with
		}
		return null;
	}
	public static String getString(String key) {
		if (fgResourceBundle == null) {
			fgResourceBundle= getResourceBundle();
		}
		
		if (fgResourceBundle != null) {
			try {
				return fgResourceBundle.getString(key);
			} catch (MissingResourceException e) {
				return "!" + key + "!";//$NON-NLS-2$//$NON-NLS-1$
			}
		} else {
			return "!" + key + "!";//$NON-NLS-2$//$NON-NLS-1$
		}
	}
public static String getString(String key, Object[] args) {

	try {return MessageFormat.format(getString(key), args);}
	catch (IllegalArgumentException e) {return getString(key);}

}
public static String getString(String key, Object[] args, int x) {

		return getString(key);
	}
}
