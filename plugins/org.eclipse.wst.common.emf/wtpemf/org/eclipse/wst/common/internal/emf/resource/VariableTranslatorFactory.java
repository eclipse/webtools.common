/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Aug 20, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.resource;

import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;


/**
 * @author schacher
 */
public interface VariableTranslatorFactory {
	/** Returns whether a Translator should be created for the given DOM name */
	boolean accepts(String domName);

	/** Returns whether a Translator should be created for the given feature */
	boolean accepts(Notification notif);

	Translator create(Notification notif);

	/** Creates a new Translator for the DOM name */
	Translator create(String domName);

	/** Creates variable translators for an EMF object to be rendered */
	List create(Notifier target);


}