/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.internal.propertypage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.core.util.RegistryReader;

public class ResourceMappingFilterExtensionRegistry extends RegistryReader {
	
	static final String EXTENSION_NAME = "resourceMappingFilter"; //$NON-NLS-1$
	static final String RESOURCE_MAPPING = "resourceMapping"; //$NON-NLS-1$
	static final String SOURCE_PATH = "source-path"; //$NON-NLS-1$	
	private static ResourceMappingFilterExtensionRegistry instance = null;
	private static HashMap<String, Pattern> resourceMappingFilters = null;
	
	
	public ResourceMappingFilterExtensionRegistry() {
		super(ModuleCoreUIPlugin.PLUGIN_ID, EXTENSION_NAME);
	}
	
	public static ResourceMappingFilterExtensionRegistry getInstance() {
		if( instance == null ) {
			instance = new ResourceMappingFilterExtensionRegistry();
			instance.readRegistry();
		}
		return instance;
	}

	@Override
	public boolean readElement(IConfigurationElement element) {
		if (!element.getName().equals(RESOURCE_MAPPING))
			return false;
		String sourcePath = element.getAttribute(SOURCE_PATH);
		if(sourcePath != null)
			addResourceMappingFilter(sourcePath);
		return true;
	}


	private static void addResourceMappingFilter(String filterRegExp) {
		if(resourceMappingFilters == null)
			resourceMappingFilters = new HashMap<String, Pattern>();
		if(filterRegExp != null) {
			try {
				Pattern pattern = Pattern.compile(filterRegExp);
				resourceMappingFilters.put(filterRegExp, pattern);
			} catch(PatternSyntaxException e) {
				ModuleCoreUIPlugin.logError(e);
			}
		}
	}
	
	
	
	public static boolean shouldFilter(IPath path) {
		ResourceMappingFilterExtensionRegistry.getInstance();
		if(path != null) {
			String relativePath = path.makeRelative().toString();
			for(Iterator iterator = resourceMappingFilters.values().iterator(); iterator.hasNext();) {
				Pattern pattern = (Pattern) iterator.next();
				Matcher matcher = pattern.matcher(relativePath);
				if (matcher.matches()) {
					return true;
				} else {
					Pattern childrenPattern = Pattern.compile(pattern.pattern()+"/.*"); //$NON-NLS-1$
					matcher = childrenPattern.matcher(relativePath);
					if (matcher.matches()) {
						return true;
					} 
				}
			}
		}
		return false;
	}
	
	public static boolean shouldFilter(String path) {
		if(path != null) {
			return shouldFilter(new Path(path));
		}
		return false;
	}
	
	public static String[] getResourceMappingFiltersRegularExpressions() {
		return resourceMappingFilters.keySet().toArray(new String[resourceMappingFilters.keySet().size()]);
	}
	
	public static Pattern[] getResourceMappingFiltersRelativePatterns() {
		return resourceMappingFilters.values().toArray(new Pattern[resourceMappingFilters.values().size()]);
	}
}
