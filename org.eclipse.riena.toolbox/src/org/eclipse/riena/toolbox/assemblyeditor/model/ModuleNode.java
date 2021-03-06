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
package org.eclipse.riena.toolbox.assemblyeditor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Module in the Riena-Navigation.
 * 
 */
public class ModuleNode extends AbstractTypedNode<SubModuleNode> {
	private final List<SubModuleNode> subModules;
	private String icon;
	private boolean closeable;

	public ModuleNode(final AbstractAssemblyNode parent) {
		super(parent);
		this.subModules = new ArrayList<SubModuleNode>();
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(final String icon) {
		this.icon = icon;
	}

	public boolean isCloseable() {
		return closeable;
	}

	public void setCloseable(final boolean uncloseable) {
		this.closeable = uncloseable;
	}

	@Override
	public List<SubModuleNode> getChildren() {
		return subModules;
	}

	@Override
	public boolean add(final SubModuleNode e) {
		return subModules.add(e);
	}

	@Override
	protected String getTreeLabelValue() {
		return name;
	}

	@Override
	public String toString() {
		return "ModuleNode [subModules=" + subModules + ", icon=" + icon + ", closeable=" + closeable + ", nodeId=" + nodeId + ", name=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				+ name + "]"; //$NON-NLS-1$
	}
}
