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
package org.eclipse.wst.common.frameworks.datamodel.tests;

import java.util.Set;

import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;

public class C extends AbstractDataModelProvider {
	public static final String P = "C.P";

	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		propertyNames.add(P);
		return propertyNames;
	}

	public String getID() {
		return null;
	}
}
