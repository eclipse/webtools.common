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
 * Created on Mar 19, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.xmi.XMIResource;

/**
 * @author schacher
 */
public class IDTranslator extends Translator {
	public class NoResourceException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8633201291288237530L;

		public NoResourceException() {
			super();
		}

		public NoResourceException(String s) {
			super(s);
		}
	}

	static final public EStructuralFeature ID_FEATURE = EcorePackage.eINSTANCE.getEClass_EIDAttribute();
	static final public IDTranslator INSTANCE = new IDTranslator();

	public IDTranslator() {
		super("id", ID_FEATURE, DOM_ATTRIBUTE); //$NON-NLS-1$
	}

	@Override
	public void setMOFValue(EObject emfObject, Object value) {
		if (emfObject != null) {
			XMIResource res = (XMIResource) emfObject.eResource();
			if (res == null)
				throw new NoResourceException();
			String id = res.getID(emfObject);
			if (id == null && value == null)
				return;
			if ((id != null && !id.equals(value)) || (value != null && !value.equals(id)))
				res.setID(emfObject, (String) value);
		}
	}

	@Override
	public Object getMOFValue(EObject emfObject) {
		if (emfObject == null)
			throw new NoResourceException();
		XMIResource res = (XMIResource) emfObject.eResource();
		if (res == null)
			throw new NoResourceException();
		return res.getID(emfObject);
	}


	@Override
	public boolean featureExists(EObject emfObject) {
		return true;
	}

	@Override
	public boolean isIDMap() {
		return true;
	}



}
