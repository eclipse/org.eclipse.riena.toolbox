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
package org.eclipse.riena.toolbox.assemblyeditor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;

/**
 * Collects all addUIControl-Calls
 * 
 */
public class AddUIControlCallVisitor extends ASTVisitor{
	
	private Set<String> variables = new HashSet<String>();
	

	@Override
	public boolean visit(MethodInvocation node) {
		if ("addUIControl".equals(node.getName().getFullyQualifiedName())){
			if (!node.arguments().isEmpty()){
				Object obj = node.arguments().get(0);
				
				if (obj instanceof SimpleName){
					SimpleName sn  = (SimpleName) obj;
					String varName = sn.getFullyQualifiedName();
					variables.add(varName);
				}
			}
		}
		return true;
	}

	public Set<String> getVariables() {
		return variables;
	}
}