/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;

import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;

public class AdapterFactoryUtil {

	/**
	 * Constructor for AdapterFactoryUtil.
	 */
	public AdapterFactoryUtil() {
		super();
	}

	public static void adaptNew(Notifier newObject, List factories) {
		AdapterFactory fact;
		for (int i = 0; i < factories.size(); i++) {
			fact = (AdapterFactory) factories.get(i);
			fact.adaptNew(newObject, null);
		}
	}

	public static void createAdapterFactories(List descriptors, List factories) {
		AdapterFactoryDescriptor desc;
		AdapterFactory adapterFact;
		for (int i = 0; i < descriptors.size(); i++) {
			desc = (AdapterFactoryDescriptor) descriptors.get(i);
			adapterFact = desc.createAdapterFactory();
			if (adapterFact != null)
				factories.add(adapterFact);
		}
		descriptors.clear();
	}

}
