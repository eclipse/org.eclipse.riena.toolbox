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
package org.eclipse.riena.toolbox.previewer;

import org.eclipse.swt.widgets.Composite;

/**
 * Interface to customize the Classload-lifecycle. 
 *
 */
public interface IPreviewCustomizer {
	void beforeClassLoad(ClassLoader classLoader);
	void afterCreation(Composite parent);
	Class<?> getParentClass();
}
