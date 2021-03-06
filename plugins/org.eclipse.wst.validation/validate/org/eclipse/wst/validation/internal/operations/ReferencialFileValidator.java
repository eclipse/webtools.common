/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on May 3, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.wst.validation.internal.operations;

import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * @author vijayb
 */
public interface ReferencialFileValidator {
	
	/**
	 * Answer the referenced files.
	 * 
	 * @param inputFiles
	 * @return a list of IFile's.
	 */
	public List<IFile> getReferencedFile(List inputFiles);
}
