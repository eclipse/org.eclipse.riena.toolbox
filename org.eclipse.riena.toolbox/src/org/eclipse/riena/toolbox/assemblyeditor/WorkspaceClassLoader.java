/*******************************************************************************
 * Copyright (c) 2007, 2011 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.toolbox.assemblyeditor;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.model.ViewPartInfo;

// FIXME move WorkspaceClassLoader to common-bundle
public final class WorkspaceClassLoader {

	private static final String EXTENSION_JAVA = ".java"; //$NON-NLS-1$
	private static WorkspaceClassLoader classFinder;
	private ISelectionService selectionService;

	private WorkspaceClassLoader() {

	}

	public static WorkspaceClassLoader getInstance() {
		if (null == classFinder) {
			classFinder = new WorkspaceClassLoader();
		}
		return classFinder;
	}

	public ICompilationUnit getSelectionFromPackageExplorer() {
		selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		final ITreeSelection selection = (ITreeSelection) selectionService
				.getSelection("org.eclipse.jdt.ui.PackageExplorer"); //$NON-NLS-1$

		final Object firstSelection = selection.getFirstElement();

		if (!(firstSelection instanceof ICompilationUnit)) {
			return null;
		}

		return (ICompilationUnit) firstSelection;
	}

	public ViewPartInfo loadClass(final ICompilationUnit comp) {

		final IPath path = comp.getPath();

		if (path.segmentCount() < 2) {
			return null;
		}

		final StringBuilder className = new StringBuilder();
		for (int i = 2; i < path.segmentCount(); i++) {
			String segment = path.segment(i);

			if (segment.endsWith(EXTENSION_JAVA)) {
				segment = segment.replace(EXTENSION_JAVA, ""); //$NON-NLS-1$
			}
			className.append(segment);

			if (i < path.segmentCount() - 1) {
				className.append("."); //$NON-NLS-1$
			}
		}

		final Class<?> parentClass = ViewPart.class;

		final URLClassLoader classLoader = createClassloader(parentClass.getClassLoader(), comp.getJavaProject());

		try {

			final Class<?> viewClass = classLoader.loadClass(className.toString());
			if (!isValidType(viewClass)) {
				return null;
			}
			return new ViewPartInfo(className.toString(), viewClass, comp);
		} catch (final ClassNotFoundException e) {
			Util.showError(e);
		} catch (final IllegalArgumentException e) {
			Util.showError(e);
		} catch (final SecurityException e) {
			Util.showError(e);
		}
		return null;
	}

	/**
	 * @param type
	 */
	public boolean isValidType(final Class<?> type) {
		return (Composite.class.isAssignableFrom(type) || ViewPart.class.isAssignableFrom(type));

	}

	private URLClassLoader createClassloader(final ClassLoader parentClass, final IJavaProject project) {
		try {

			String[] classPathEntries = null;
			try {
				classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(project);
			} catch (final JavaModelException ex) {
				Util.showError(ex);
				return null;
			}

			final List<URL> urlList = new ArrayList<URL>();
			for (final String entry : classPathEntries) {
				urlList.add(new Path(entry).toFile().toURI().toURL());
			}

			final URL[] urls = urlList.toArray(new URL[urlList.size()]);
			return new URLClassLoader(urls, parentClass);

		} catch (final CoreException e) {
			Util.showError(e);
		} catch (final MalformedURLException e) {
			Util.showError(e);
		}
		return null;
	}
}
