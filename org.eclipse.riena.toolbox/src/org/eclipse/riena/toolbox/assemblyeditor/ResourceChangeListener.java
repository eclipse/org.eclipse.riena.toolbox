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

import org.eclipse.core.resources.IProject;

/**
 * Listener that will be notified, when Changes in the workbench occur like a
 * plugin.xml in of the bundles changes or a project is added/removed.
 * 
 */
public interface ResourceChangeListener {
	void pluginXmlChanged(IProject project);

	void projectAdded(IProject project);
}
