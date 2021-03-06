/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.common.componentcore.datamodel.properties.ICreateReferenceComponentsDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.AbstractDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

public class CreateReferenceComponentsDataModelProvider extends AbstractDataModelProvider implements ICreateReferenceComponentsDataModelProperties {

	public CreateReferenceComponentsDataModelProvider() {
		super();
	}

	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();
		propertyNames.add(SOURCE_COMPONENT);
		propertyNames.add(TARGET_COMPONENT_LIST);
		propertyNames.add(TARGET_COMPONENTS_DEPLOY_PATH);
		propertyNames.add(TARGET_COMPONENTS_DEPLOY_PATH_MAP);
		propertyNames.add(TARGET_COMPONENTS_TO_URI_MAP);
		return propertyNames;
	}

	public IDataModelOperation getDefaultOperation() {
		return new CreateReferenceComponentsOp(model);
	}

	public Object getDefaultProperty(String propertyName) {
		if (TARGET_COMPONENTS_TO_URI_MAP.equals(propertyName)) {
			Map map = new HashMap();
			setProperty(propertyName, map);
			return map;
		}
		else if (TARGET_COMPONENTS_DEPLOY_PATH_MAP.equals(propertyName)) {
			Map map = new HashMap();
			setProperty(propertyName, map);
			return map;
		}
		
		if (propertyName.equals(TARGET_COMPONENT_LIST))
			return new ArrayList();
		else if (propertyName.equals(TARGET_COMPONENTS_DEPLOY_PATH)){
			return "/"; //$NON-NLS-1$
		}
		return super.getDefaultProperty(propertyName);
	}
}
