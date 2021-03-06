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
package org.eclipse.riena.toolbox.assemblyeditor.ui;

import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;

/**
 * The listener interface for receiving dirty events.
 */
public interface IDirtyListener {
	/**
	 * @param node
	 *            the node that changed
	 * @param isDirty
	 *            <code>true</code> if the content changed otherwise
	 *            <code>false</code>
	 */
	void dirtyStateChanged(AbstractAssemblyNode<?> node, boolean isDirty);
}
