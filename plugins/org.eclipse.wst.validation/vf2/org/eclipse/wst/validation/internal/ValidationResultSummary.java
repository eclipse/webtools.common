/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

/**
 * An immutable object that holds a summary of the validation.
 * @author karasiuk
 *
 */
public final class ValidationResultSummary {
	
	private final int 	_error;
	private final int 	_warning;
	private final int	_info;
	
	public ValidationResultSummary(int error, int warning, int info){
		_error = error;
		_warning = warning;
		_info = info;
	}

	public int getSeverityError() {
		return _error;
	}

	public int getSeverityWarning() {
		return _warning;
	}

	public int getSeverityInfo() {
		return _info;
	}

}
