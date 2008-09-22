/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.ui.templates.rienaCustomerSample;

import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;

public class RienaCommonWizard extends NewPluginTemplateWizard {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.ui.templates.AbstractNewPluginTemplateWizard#init(org
	 * .eclipse.pde.ui.IFieldData)
	 */
	public void init(IFieldData data) {
		super.init(data);
		setWindowTitle("Hello World Riena");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.pde.ui.templates.NewPluginTemplateWizard#createTemplateSections
	 * ()
	 */
	public ITemplateSection[] createTemplateSections() {
		return new ITemplateSection[] { new RienaCommonTemplate() };
	}
}
