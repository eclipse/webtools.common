/*******************************************************************************
 * Copyright (c) 2001, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.text.MessageFormat;

import org.eclipse.core.resources.IResource;

/**
 * Represents a type filter tag in a validator's plugin.xml file. e.g. &lt;filter
 * objectClass="com.ibm.foo.MyClass"> Then this class would store the "com.ibm.foo.MyClass", and
 * provide the "instanceof" matching functionality.
 */
public class ValidatorTypeFilter {
	private Class _typeFilterClass;
	private String _mustImplementClass; // the type set in setTypeFilter must implement the

	// class/interface identified by this fully-qualified
	// Java string.

	ValidatorTypeFilter() {
		//default
	}

	ValidatorTypeFilter(String mustImplementClass) {
		setMustImplementClass(mustImplementClass);
	}

	/**
	 * Type filters are allowed only for certain types of classes. This method returns the name of
	 * the class which this type must implement before it can be a filter.
	 */
	String getMustImplementClass() {
		return _mustImplementClass;
	}

	/**
	 * Return the type filter as a java.lang.Class object.
	 */
	Class getTypeFilterClass() {
		return _typeFilterClass;
	}

	/**
	 * Returns true if the resource passed in either an instance of the type filter class, or if
	 * there is no type filter class defined in plugin.xml.
	 */
	public boolean isApplicableType(IResource resource) {
		// If type filter is null, means filter out no types.
		// Otherwise, return true only if the given type is an instance of
		// the type filter.
		if (_typeFilterClass == null)
			return true;

		// If the resource is an instance of the type filter class.
		return isInstance(resource.getClass(), _typeFilterClass);
	}

	/**
	 * Checks if filterClass is a parent (interface or superclass) of objectClass.
	 */
	boolean isInstance(Class objectClass, Class filterClass) {
		// The java.lang.Class.isInstance call doesn't check interfaces fully.
		// i.e., if I have two interfaces, A and B, and B extends A but
		// doesn't implement A, then the isInstance call will return false.
		//
		// So, instead of using Class.isInstance, do the checking myself.
		final String filterClassName = filterClass.getName();
		for (Class cl = objectClass; cl != null; cl = cl.getSuperclass()) {
			if (cl.getName().equals(filterClassName))return true;
			Class[] clInterfaces = cl.getInterfaces();
			for (int i = 0; i < clInterfaces.length; i++) {
				if (clInterfaces[i].getName().equals(filterClassName))return true;
				if (isInstance(clInterfaces[i], filterClass))return true;
			}
		}
		return false;
	}

	/**
	 * Type filters are allowed only for certain types of classes. This method sets the name of the
	 * class which this type must implement before it can be a filter.
	 */
	void setMustImplementClass(String className) {
		_mustImplementClass = className;
	}

	/**
	 * If the filter implements the mustImplementClass (in ValidatorFilter's case, IResource), then
	 * this is a valid filter, and store the filter value.
	 */
	void setTypeFilter(String filter) {
		Class filterClass = null;
		Class mustImplementClass = null;

		if (filter == null) {
			_typeFilterClass = null;
			return;
		}

		try {
			filterClass = Class.forName(filter);
			if (getMustImplementClass() != null) {
				mustImplementClass = Class.forName(getMustImplementClass());

				// If the filter class is not an instance of mustImplementClass
				if (!isInstance(filterClass, mustImplementClass)) {
					_typeFilterClass = null;
					if (Tracing.isLogging()) {
						String result = MessageFormat.format(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INVALID_TYPE_FILTER), 
							new Object[]{filter, getMustImplementClass()});
						Tracing.log("ValidatorTypeFilter-01: ", result); //$NON-NLS-1$
					}
				}
			}
		} catch (ClassNotFoundException exc) {
			_typeFilterClass = null;
			if (Tracing.isLogging()) {
				Tracing.log("ValidatorTypeFilter-02: The class named " + filter +  //$NON-NLS-1$
					" cannot be instantiated because it does not exist. Check the spelling of the name, " + //$NON-NLS-1$
					"in the validator's plugin.xml contribution, and try restarting eclipse again."); //$NON-NLS-1$
			}
			return;
		}
		_typeFilterClass = filterClass;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("TypeFilter:"); //$NON-NLS-1$
		buffer.append("     _typeFilterClass = " + _typeFilterClass.getName()); //$NON-NLS-1$
		buffer.append("     _mustImplementClass = " + _mustImplementClass); //$NON-NLS-1$
		return buffer.toString();
	}
}
