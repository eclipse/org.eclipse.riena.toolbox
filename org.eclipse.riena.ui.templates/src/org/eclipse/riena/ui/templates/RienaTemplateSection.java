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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.framework.Bundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.PluginReference;

public abstract class RienaTemplateSection extends OptionTemplateSection {

	public static final String KEY_PRODUCT_BRANDING = "productBranding"; //$NON-NLS-1$
	public static final String KEY_PRODUCT_NAME = "productName"; //$NON-NLS-1$

	public static final String VALUE_PRODUCT_ID = "product"; //$NON-NLS-1$
	public static final String VALUE_PRODUCT_NAME = "Riena Product"; //$NON-NLS-1$
	public static final String VALUE_PERSPECTIVE_NAME = "Riena Perspective"; //$NON-NLS-1$
	public static final String VALUE_APPLICATION_ID = "application"; //$NON-NLS-1$

	@Override
	protected ResourceBundle getPluginResourceBundle() {
		final Bundle bundle = Platform.getBundle(Activator.getDefault().getBundle().getSymbolicName());
		return Platform.getResourceBundle(bundle);
	}

	@Override
	protected URL getInstallURL() {
		return Activator.getDefault().getInstallURL();
	}

	@Override
	public URL getTemplateLocation() {
		try {
			final String[] candidates = getDirectoryCandidates();
			for (final String candidate2 : candidates) {
				if (Activator.getDefault().getBundle().getEntry(candidate2) != null) {
					final URL candidate = new URL(getInstallURL(), candidate2);
					return candidate;
				}
			}
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String[] getDirectoryCandidates() {
		final double version = getTargetVersion();
		final List<String> result = new ArrayList<String>();
		if (version >= 3.4) {
			result.add("templates_3.4" + "/" + getSectionId() + "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return result.toArray(new String[result.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.ui.templates.ITemplateSection#getFoldersToInclude()
	 */
	public String[] getNewFiles() {
		return new String[0];
	}

	protected String getFormattedPackageName(final String id) {
		final StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < id.length(); i++) {
			final char ch = id.charAt(i);
			if (buffer.length() == 0) {
				if (Character.isJavaIdentifierStart(ch)) {
					buffer.append(Character.toLowerCase(ch));
				}
			} else {
				if (Character.isJavaIdentifierPart(ch) || ch == '.') {
					buffer.append(ch);
				}
			}
		}
		return buffer.toString().toLowerCase(Locale.ENGLISH);
	}

	@Override
	protected void generateFiles(final IProgressMonitor monitor) throws CoreException {
		super.generateFiles(monitor);
		// Copy the default splash screen if the branding option is selected
		if (copyBrandingDirectory()) {
			super.generateFiles(monitor, Activator.getDefault().getBundle().getEntry("branding/")); //$NON-NLS-1$
		}
	}

	protected boolean copyBrandingDirectory() {
		return getBooleanOption(KEY_PRODUCT_BRANDING);
	}

	public IPluginReference[] getCoreDependencies(final String schemaVersion) {
		final IPluginReference[] dep = new IPluginReference[1];
		int i = 0;
		dep[i++] = new PluginReference("org.eclipse.core.runtime", null, 0); //$NON-NLS-1$
		return dep;
	}

	public IPluginReference[] getCommonDependencies(final String schemaVersion) {
		final IPluginReference[] dep = new IPluginReference[2];
		int i = 0;
		dep[i++] = new PluginReference("org.eclipse.core.runtime", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.riena.communication.core", null, 0); //$NON-NLS-1$
		return dep;
	}

	public IPluginReference[] getHeadlessClientDependencies(final String schemaVersion) {
		final IPluginReference[] dep = new IPluginReference[4];
		int i = 0;
		dep[i++] = new PluginReference("org.eclipse.core.runtime", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.riena.core", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.riena.communication.core", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.riena.communication.factory.hessian", null, 0); //$NON-NLS-1$
		return dep;
	}

	public IPluginReference[] getUIDependencies(final String schemaVersion) {
		final IPluginReference[] dep = new IPluginReference[2];
		int i = 0;
		dep[i++] = new PluginReference("org.eclipse.core.runtime", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.riena.client", null, 0); //$NON-NLS-1$
		return dep;
	}

	public IPluginReference[] getServiceDependencies(final String schemaVersion) {
		final IPluginReference[] dep = new IPluginReference[2];
		int i = 0;
		dep[i++] = new PluginReference("org.eclipse.core.runtime", null, 0); //$NON-NLS-1$
		dep[i++] = new PluginReference("org.eclipse.riena.server", null, 0); //$NON-NLS-1$
		return dep;
	}

	@Override
	protected boolean isOkToCreateFolder(final File sourceFolder) {
		if (sourceFolder.getName().equals("CVS")) {
			return false;
		} else {
			return super.isOkToCreateFolder(sourceFolder);
		}
	}

	@Override
	public String getReplacementString(final String fileName, final String key) {
		if ((fileName.endsWith(".launch") || fileName.endsWith(".java")) && key.startsWith("{") && key.contains("}")) {
			return "$" + key;
		}
		return super.getReplacementString(fileName, key);
	}

	// protected void createBrandingOptions() {
	// addOption(KEY_PRODUCT_BRANDING,
	// PDETemplateMessages.HelloRCPTemplate_productBranding, false, 0);
	// }

}
