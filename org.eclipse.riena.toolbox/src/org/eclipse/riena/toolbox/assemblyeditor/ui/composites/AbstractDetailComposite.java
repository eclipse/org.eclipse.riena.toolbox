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
package org.eclipse.riena.toolbox.assemblyeditor.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.assemblyeditor.model.AbstractAssemblyNode;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IDirtyListener;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IconSelectorText;
import org.eclipse.riena.toolbox.assemblyeditor.ui.UIControlsFactory;
import org.eclipse.riena.toolbox.assemblyeditor.ui.VerifyTypeIdText;
import org.eclipse.riena.toolbox.assemblyeditor.ui.ViewSelectorText;

public abstract class AbstractDetailComposite<T extends AbstractAssemblyNode<?>> extends Composite {
	protected T node;
	protected Color workareaBackground;
	private final Color headerBackground;
	private final DirtyChecker dirtyChecker;
	private final List<IDirtyListener> dirtyListener;
	private final Composite cmpWorkarea;

	public AbstractDetailComposite(final Composite parent, final String headerImageLeft, final String headerImageRight) {
		super(parent, SWT.None);

		dirtyListener = new ArrayList<IDirtyListener>();
		dirtyChecker = new DirtyChecker();
		workareaBackground = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		headerBackground = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(this);

		final Composite cmpHeader = new Composite(this, SWT.None);
		GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 95).grab(true, false).applyTo(cmpHeader);
		createHeader(cmpHeader, headerImageLeft, headerImageRight);

		cmpWorkarea = new Composite(this, SWT.None);
		cmpWorkarea.setBackground(workareaBackground);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(cmpWorkarea);
		createWorkarea(cmpWorkarea);
		addModifyListener(cmpWorkarea);
	}

	/**
	 * @return the node
	 */
	public T getNode() {
		return node;
	}

	public boolean isValid() {
		for (final Control cont : cmpWorkarea.getChildren()) {
			if (cont instanceof VerifyTypeIdText) {
				final VerifyTypeIdText verifier = (VerifyTypeIdText) cont;
				if (!verifier.isValid()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean addDirtyListener(final IDirtyListener e) {
		return dirtyListener.add(e);
	}

	public boolean removeDirtyListener(final IDirtyListener e) {
		return dirtyListener.remove(e);
	}

	private void fireDirtyChanged(final boolean isDirty) {
		for (final IDirtyListener l : dirtyListener) {
			l.dirtyStateChanged(null, isDirty);
		}
	}

	private class DirtyChecker implements SelectionListener, KeyListener {
		public void keyPressed(final KeyEvent e) {
			fireDirtyChanged(true);
		}

		public void keyReleased(final KeyEvent e) {
		}

		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		public void widgetSelected(final SelectionEvent e) {
			fireDirtyChanged(true);
		}
	}

	private void addModifyListener(final Composite parent) {
		for (final Control child : parent.getChildren()) {
			if (child instanceof Text) {
				((Text) child).addKeyListener(dirtyChecker);
			} else if (child instanceof Button) {
				((Button) child).addSelectionListener(dirtyChecker);
			} else if (child instanceof Composite) {
				addModifyListener(((Composite) child));
			}
		}
	}

	private void createHeader(final Composite parent, final String headerImageLeft, final String headerImageRight) {
		parent.setBackground(headerBackground);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(parent);

		final Label lblImageLeft = UIControlsFactory.createLabel(parent, ""); //$NON-NLS-1$

		ImageDescriptor desc = null;
		if (headerImageLeft != null && headerImageLeft.length() > 0) {
			desc = Activator.getImageDescriptor("/icons/" + headerImageLeft); //$NON-NLS-1$
		}
		if (null != desc) {
			lblImageLeft.setImage(desc.createImage());
		}

		GridDataFactory.swtDefaults().indent(5, 5).grab(false, false).align(SWT.LEFT, SWT.CENTER).applyTo(lblImageLeft);

		final Label lblImageRight = UIControlsFactory.createLabel(parent, ""); //$NON-NLS-1$

		ImageDescriptor desc2 = null;
		if (headerImageRight != null && headerImageRight.length() > 0) {
			desc2 = Activator.getImageDescriptor("/icons/" + headerImageRight); //$NON-NLS-1$
		}
		if (null != desc2) {
			lblImageRight.setImage(desc2.createImage());
		}

		GridDataFactory.fillDefaults().grab(true, true).align(SWT.RIGHT, SWT.CENTER).applyTo(lblImageRight);
	}

	/**
	 * This method is called once to create the workarea.
	 */
	protected abstract void createWorkarea(Composite parent);

	/**
	 * This method is called everytime a call to DetailSection.showDetails oder
	 * DetailSection.update occurs.
	 */
	public abstract void bind(T node);

	public abstract void unbind();

	protected VerifyTypeIdText createLabeledVerifyText(final Composite parent, final String labelText) {
		final Label lbl = UIControlsFactory.createLabel(parent, labelText);
		lbl.setBackground(workareaBackground);
		GridDataFactory.swtDefaults().applyTo(lbl);
		final VerifyTypeIdText text = UIControlsFactory.createCheckTypeIdText(parent);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
		return text;
	}

	protected Text createLabeledText(final Composite parent, final String labelText) {
		return createLabeledText(parent, labelText, false);
	}

	protected Text createLabeledText(final Composite parent, final String labelText, final boolean mandatory) {
		final Label lbl = UIControlsFactory.createLabel(parent, labelText);
		lbl.setBackground(workareaBackground);
		GridDataFactory.swtDefaults().applyTo(lbl);
		final Text text = UIControlsFactory.createText(parent, mandatory);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
		return text;
	}

	protected IconSelectorText createLabeledIconSelector(final Composite parent, final String labelText) {
		final Label lbl = UIControlsFactory.createLabel(parent, labelText);
		lbl.setBackground(workareaBackground);
		GridDataFactory.swtDefaults().applyTo(lbl);

		final IconSelectorText cmpControl = new IconSelectorText(parent, workareaBackground);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(cmpControl);
		return cmpControl;
	}

	protected ViewSelectorText createLabeledViewSelector(final Composite parent, final String labelText) {
		final Label lbl = UIControlsFactory.createLabel(parent, labelText);
		lbl.setBackground(workareaBackground);
		GridDataFactory.swtDefaults().applyTo(lbl);

		final ViewSelectorText cmpControl = new ViewSelectorText(parent, workareaBackground);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(cmpControl);
		return cmpControl;
	}

	protected Button createLabeledCheckbox(final Composite parent, final String labelText) {
		final Label lbl = UIControlsFactory.createLabel(parent, labelText);
		lbl.setBackground(workareaBackground);
		GridDataFactory.swtDefaults().applyTo(lbl);
		final Button butt = UIControlsFactory.createCheckbox(parent);
		butt.setBackground(workareaBackground);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(butt);
		return butt;
	}

	protected String getTextSave(final Object in) {
		if (null == in) {
			return ""; //$NON-NLS-1$
		}

		if (in instanceof String) {
			return (String) in;
		}

		return ""; //$NON-NLS-1$
	}
}
