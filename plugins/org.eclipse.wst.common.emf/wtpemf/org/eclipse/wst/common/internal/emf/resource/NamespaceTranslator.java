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
 * Created on Aug 14, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.internal.emf.utilities.Namespace;
import org.eclipse.wst.common.internal.emf.utilities.NamespaceAdapter;


/**
 * @author schacher
 */
public class NamespaceTranslator extends Translator {

	protected String prefix;

	/**
	 * @param domNameAndPath
	 * @param aFeature
	 */
	public NamespaceTranslator(String domName) {
		super(domName, NamespaceAdapter.NOTIFICATION_FEATURE, DOM_ATTRIBUTE);
		initPrefix();
	}

	private void initPrefix() {
		String dName = getDOMName(null);
		prefix = dName.substring(DefaultTranslatorFactory.XMLNS.length());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#setMOFValue(org.eclipse.emf.ecore.EObject,
	 *      java.lang.Object)
	 */
	@Override
	public void setMOFValue(EObject emfObject, Object value) {
		NamespaceAdapter.addNamespace(prefix, (String) value, emfObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#isSetMOFValue(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean isSetMOFValue(EObject emfObject) {
		return getMOFValue(emfObject) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#getMOFValue(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public Object getMOFValue(EObject mofObject) {
		return NamespaceAdapter.getNamespaceURIAtThisLevel(prefix, mofObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#unSetMOFValue(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public void unSetMOFValue(EObject emfObject) {
		NamespaceAdapter.removeNamespace(prefix, emfObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#featureExists(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean featureExists(EObject emfObject) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#isDataType()
	 */
	@Override
	public boolean isDataType() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#isMapFor(java.lang.Object, java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public boolean isMapFor(Object aFeature, Object oldValue, Object newValue) {
		if (aFeature == feature) {
			Namespace namespace = (Namespace) (oldValue == null ? newValue : oldValue);
			if (namespace != null)
				return prefix.equals(namespace.getPrefix());
		}
		return false;
	}
}
