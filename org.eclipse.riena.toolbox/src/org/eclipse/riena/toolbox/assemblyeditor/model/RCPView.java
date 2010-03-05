/*******************************************************************************
 * Copyright (c) 2007, 2009 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.toolbox.assemblyeditor.model;

/**
 * This class represents a View in the Eclipse RCP.
 *
 */
public class RCPView {
	private String id;
	private String name;
	private String viewClass;
	private boolean allowMultiple = true;
	private boolean restorable = false;

	public RCPView() {
	}
	
	public RCPView(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getViewClass() {
		return viewClass;
	}

	public boolean isAllowMultiple() {
		return allowMultiple;
	}

	public boolean isRestorable() {
		return restorable;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setViewClass(String clazz) {
		this.viewClass = clazz;
	}

	@Override
	public String toString() {
		return "RCPView [allowMultiple=" + allowMultiple + ", clazz=" + viewClass
				+ ", id=" + id + ", name=" + name + ", restorable="
				+ restorable + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RCPView other = (RCPView) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}