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

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import org.eclipse.riena.core.util.StringUtils;
import org.eclipse.riena.toolbox.Activator;
import org.eclipse.riena.toolbox.Util;
import org.eclipse.riena.toolbox.assemblyeditor.model.RCPView;
import org.eclipse.riena.toolbox.assemblyeditor.model.SubModuleNode;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IconSelectorText;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IdSelectorText;
import org.eclipse.riena.toolbox.assemblyeditor.ui.IdSelectorText.IDataProvider;
import org.eclipse.riena.toolbox.assemblyeditor.ui.OpenClassLink;
import org.eclipse.riena.toolbox.assemblyeditor.ui.TextButtonComposite;
import org.eclipse.riena.toolbox.assemblyeditor.ui.UIControlsFactory;
import org.eclipse.riena.toolbox.assemblyeditor.ui.VerifyTypeIdText;

@SuppressWarnings("restriction")
public class SubModuleComposite extends AbstractDetailComposite<SubModuleNode> {

	private Text txtName;
	private VerifyTypeIdText txtNodeId;
	private IdSelectorText txtView;
	private BrowseControllerComposite txtController;
	private OpenClassLink lnkController;
	private OpenClassLink lnkView;
	private Button btnShared;
	private IconSelectorText txtIcon;
	private Button btnSelectable;
	private Button btnRequiresPreparation;
	private Button btnVisible;
	private Button btnExpanded;

	public SubModuleComposite(final Composite parent) {
		super(parent, "submodule_li.png", "submodule_re.png"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void bind(final SubModuleNode node) {
		this.node = node;
		txtName.setText(getTextSave(node.getName()));
		txtNodeId.getText().setText(getTextSave(node.getNodeId()));
		txtNodeId.setIgnoreNode(node);

		if (null != node.getRcpView()) {
			txtView.getText().setText(getTextSave(node.getRcpView().getId()));
			txtView.setCurrentId(node.getRcpView().getId());
		} else {
			txtView.getText().setText(""); //$NON-NLS-1$
			txtView.setCurrentId(""); //$NON-NLS-1$
		}

		txtController.getText().setText(getTextSave(node.getController()));
		txtController.setControllerName(node.getController());
		txtController.setProject(node.getBundle().getProject());

		lnkController.setProject(node.getBundle().getProject());
		lnkController.setClassName(node.getController());

		lnkView.setProject(node.getBundle().getProject());
		if (node.getRcpView() != null) {
			lnkView.setClassName(node.getRcpView().getViewClass());
			if (null != node.getRcpView() && null != node.getRcpView().getBundle()) {
				lnkView.setProject(node.getRcpView().getBundle().getProject());
			}
		}

		btnShared.setSelection(node.isSharedView());
		txtIcon.getText().setText(getTextSave(node.getIcon()));
		txtIcon.setProject(node.getBundle().getProject());
		btnSelectable.setSelection(node.isSelectable());
		btnRequiresPreparation.setSelection(node.isRequiresPreparation());
		btnVisible.setSelection(node.isVisible());
		btnExpanded.setSelection(node.isExpanded());
	}

	@Override
	public boolean setFocus() {
		return txtName.setFocus();
	}

	@Override
	public void unbind() {
		node.setName(txtName.getText());
		node.setNodeId(txtNodeId.getText().getText());

		if (null == node.getRcpView()) {
			node.setRcpView(new RCPView());
		}

		node.getRcpView().setId(txtView.getText().getText());

		for (final RCPView view : Activator.getDefault().getAssemblyModel().getRcpViews()) {
			if (view.getId().equals(node.getRcpView().getId())) {
				node.setRcpView(view);
				break;
			}
		}

		node.setController(txtController.getText().getText());
		node.setSharedView(btnShared.getSelection());
		node.setIcon(txtIcon.getText().getText());
		node.setSelectable(btnSelectable.getSelection());
		node.setRequiresPreparation(btnRequiresPreparation.getSelection());
		node.setVisible(btnVisible.getSelection());
		node.setExpanded(btnExpanded.getSelection());
	}

	@Override
	protected void createWorkarea(final Composite parent) {
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
		txtName = createLabeledText(parent, "Name", true);
		txtName.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				if (null == node.getPrefix()) {
					return;
				}

				// if the node already has a RCPView, don't update the nodeId
				if (null != node.getRcpView() && Util.isGiven(node.getRcpView().getViewClass())) {
					return;
				}

				final String simpleName = Util.cleanNodeId(txtName.getText().trim(), false);
				txtNodeId.getText().setText(node.getPrefix() + simpleName + node.getSuffix());
			}
		});
		txtNodeId = createLabeledVerifyText(parent, "NodeId");

		buildViewSection(parent);
		buildControllerSection(parent);

		txtIcon = createLabeledIconSelector(parent, "Icon");
		btnShared = createLabeledCheckbox(parent, "SharedView");
		btnSelectable = createLabeledCheckbox(parent, "Selectable");
		btnRequiresPreparation = createLabeledCheckbox(parent, "RequiresPreparation");
		btnVisible = createLabeledCheckbox(parent, "Visible");
		btnExpanded = createLabeledCheckbox(parent, "Expanded");
	}

	private void buildViewSection(final Composite parent) {
		lnkView = new OpenClassLink(parent, "ViewId");
		lnkView.setBackground(workareaBackground);
		GridDataFactory.swtDefaults().applyTo(lnkView);
		txtView = new IdSelectorText(parent, workareaBackground, "View Selection",
				"Select a View (* = any string, ? = any char):", false);

		txtView.getText().addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				final String viewId = txtView.getText().getText();

				if (!StringUtils.isGiven(viewId)) {
					lnkView.setClassName(null);
					lnkView.setProject(null);
					return;
				}

				for (final RCPView view : Activator.getDefault().getAssemblyModel().getRcpViews()) {
					if (viewId.equals(view.getId())) {
						lnkView.setClassName(view.getViewClass());
						lnkView.setProject(view.getBundle().getProject());
						break;
					}
				}
			}
		});

		// FIXME
		// use
		// RCPViews
		txtView.setDataProvider(new IDataProvider() {
			public List<String> getData() {
				return Activator.getDefault().getAssemblyModel().getRcpViewIds();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtView);
	}

	private void buildControllerSection(final Composite parent) {
		lnkController = UIControlsFactory.createOpenClassLink(parent, "Controller");
		txtController = new BrowseControllerComposite(parent, workareaBackground, lnkController);
		txtController.getText().addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				if (!StringUtils.isGiven(txtController.getText().getText())) {
					lnkController.setClassName(null);
					lnkController.setProject(null);
				}
			}
		});

		GridDataFactory.swtDefaults().applyTo(lnkController);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(txtController);
	}

	private static class BrowseControllerComposite extends TextButtonComposite {

		private IProject project;
		private String controllerName;

		public BrowseControllerComposite(final Composite parent, final Color background, final OpenClassLink link) {
			super(parent, background, false);

			getBrowseButton().addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
					final IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();

					final FilteredTypesSelectionDialog dia = new FilteredTypesSelectionDialog(shell, false,
							(IRunnableContext) null, searchScope, IJavaSearchConstants.CLASS_AND_ENUM);

					dia.setTitle("Controller Selection");

					if (Util.isGiven(controllerName)) {
						dia.setInitialPattern(controllerName, FilteredItemsSelectionDialog.FULL_SELECTION);
					} else {
						dia.setInitialPattern(project.getName() + ".controller.", //$NON-NLS-1$
								FilteredItemsSelectionDialog.FULL_SELECTION);
					}

					dia.open();
					final Object[] result = dia.getResult();

					if (null != result) {
						for (final Object obj : result) {
							final SourceType source = (SourceType) obj;
							getText().setText(source.getFullyQualifiedName());
							link.setClassName(source.getFullyQualifiedName());
							link.setProject(source.getJavaProject().getProject());

						}
					}
				}
			});
		}

		@SuppressWarnings("unused")
		public String getControllerName() {
			return controllerName;
		}

		public void setControllerName(final String controllerName) {
			this.controllerName = controllerName;
		}

		@SuppressWarnings("unused")
		public IProject getProject() {
			return project;
		}

		public void setProject(final IProject project) {
			this.project = project;
		}
	}
}
