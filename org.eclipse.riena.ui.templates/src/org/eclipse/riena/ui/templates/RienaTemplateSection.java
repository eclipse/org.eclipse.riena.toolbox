/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.riena.ui.templates;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.PluginReference;
import org.osgi.framework.Bundle;

public abstract class RienaTemplateSection extends OptionTemplateSection {

	public static final String KEY_PRODUCT_BRANDING = "productBranding"; //$NON-NLS-1$
	public static final String KEY_PRODUCT_NAME = "productName"; //$NON-NLS-1$

	public static final String VALUE_PRODUCT_ID = "product"; //$NON-NLS-1$
	public static final String VALUE_PRODUCT_NAME = "Riena Product"; //$NON-NLS-1$
	public static final String VALUE_PERSPECTIVE_NAME = "Riena Perspective"; //$NON-NLS-1$
	public static final String VALUE_APPLICATION_ID = "application"; //$NON-NLS-1$

	protected ResourceBundle getPluginResourceBundle() {
		Bundle bundle = Platform.getBundle(Activator.getDefault().getBundle()
				.getSymbolicName());
		return Platform.getResourceBundle(bundle);
	}

	protected URL getInstallURL() {
		return Activator.getDefault().getInstallURL();
	}

	public URL getTemplateLocation() {
		try {
			String[] candidates = getDirectoryCandidates();
			for (int i = 0; i < candidates.length; i++) {
				if (Activator.getDefault().getBundle().getEntry(candidates[i]) != null) {
					URL candidate = new URL(getInstallURL(), candidates[i]);
					return candidate;
				}
			}
		} catch (MalformedURLException e) { // do nothing
		}
		return null;
	}

	private String[] getDirectoryCandidates() {
		double version = getTargetVersion();
		ArrayList result = new ArrayList();
		if (version >= 3.4)
			result.add("templates_3.4" + "/" + getSectionId() + "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (version >= 3.3)
			result.add("templates_3.3" + "/" + getSectionId() + "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return (String[]) result.toArray(new String[result.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.ui.templates.ITemplateSection#getFoldersToInclude()
	 */
	public String[] getNewFiles() {
		return new String[0];
	}

	protected String getFormattedPackageName(String id) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < id.length(); i++) {
			char ch = id.charAt(i);
			if (buffer.length() == 0) {
				if (Character.isJavaIdentifierStart(ch))
					buffer.append(Character.toLowerCase(ch));
			} else {
				if (Character.isJavaIdentifierPart(ch) || ch == '.')
					buffer.append(ch);
			}
		}
		return buffer.toString().toLowerCase(Locale.ENGLISH);
	}

	protected void generateFiles(IProgressMonitor monitor) throws CoreException {
		super.generateFiles(monitor);
		// Copy the default splash screen if the branding option is selected
		if (copyBrandingDirectory()) {
			super.generateFiles(monitor, Activator.getDefault().getBundle()
					.getEntry("branding/")); //$NON-NLS-1$
		}
	}

	protected boolean copyBrandingDirectory() {
		return getBooleanOption(KEY_PRODUCT_BRANDING);
	}
	
	public IPluginReference[] getUIDependencies(String schemaVersion) {
		IPluginReference[] dep = new IPluginReference[15];
		int i = 0;
		dep[i++] = new PluginReference("org.eclipse.core.runtime", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.riena.navigation", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference(
				"org.eclipse.riena.navigation.ui", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference(
				"org.eclipse.riena.navigation.ui.swt", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.ui", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.ui.workbench", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.riena.core", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference(
				"org.eclipse.riena.sample.app.common", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference(
				"org.eclipse.riena.communication.core", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.core.databinding", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference(
				"org.eclipse.core.databinding.beans", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.jface.databinding", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference(
				"org.eclipse.riena.ui.ridgets.swt", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.riena.ui.ridgets", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.riena.ui.core", null, 0); //$NON-NLS-1$
		return dep;
	}

	@Override
	protected boolean isOkToCreateFolder(File sourceFolder) {
		if (sourceFolder.getName().equals("CVS")) {
			return false;
		} else {
			return super.isOkToCreateFolder(sourceFolder);}
	}
	
	

//	protected void createBrandingOptions() {
//		addOption(KEY_PRODUCT_BRANDING,
//				PDETemplateMessages.HelloRCPTemplate_productBranding, false, 0);
//	}

}
