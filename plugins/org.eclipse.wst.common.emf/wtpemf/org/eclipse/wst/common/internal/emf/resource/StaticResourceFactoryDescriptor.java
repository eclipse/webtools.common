/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Declares a subclass to create Resource.Factory(ies) from an extension. 
 */
class StaticResourceFactoryDescriptor extends ResourceFactoryDescriptor {
	
	private final String shortSegment;
	private final Resource.Factory factory;
	
	/**
	 * 
	 * @param shortSegment A non-null name of the file associated with the given factory
	 * @param factory A non-null Resource.Factory that can load files of the given name
	 */
	public StaticResourceFactoryDescriptor(String shortSegment, Resource.Factory factory) {
		Assert.isNotNull(shortSegment);
		Assert.isNotNull(factory);
		this.shortSegment = shortSegment;
		this.factory = factory;
	}  

	@Override
	public boolean isEnabledFor(URI fileURI) {
		/* shortSegment must be non-null for the descriptor to be created, 
		 * a validation check in init() verifies this requirement */
		if(fileURI != null && fileURI.lastSegment() != null)
			return shortSegment.equals(fileURI.lastSegment());
		return false;
	} 
	
	@Override
	public Resource.Factory createFactory() {
		 return factory;			
	}

	@Override
	public String getShortSegment() {
		return shortSegment;
	}  
}
