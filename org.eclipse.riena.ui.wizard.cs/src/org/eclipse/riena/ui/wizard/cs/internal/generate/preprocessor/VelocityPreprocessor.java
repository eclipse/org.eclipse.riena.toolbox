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
package org.eclipse.riena.ui.wizard.cs.internal.generate.preprocessor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

import org.eclipse.riena.ui.wizard.cs.internal.RienaWizardPlugin;

public class VelocityPreprocessor implements Preprocessor {
	private final Properties properties;

	private String changedName;

	private static VelocityEngine ve = new VelocityEngine();

	static {
		ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, new EclipseLogChute());
		try {
			ve.init();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	public VelocityPreprocessor(final Properties properties) {
		this.properties = properties;

		properties.put("d", "$"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public InputStream process(final InputStream input, final String tag) throws CoreException {
		try {
			try {
				changedName = null;

				final VelocityContext context = new VelocityContext(properties);

				final ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
				final OutputStreamWriter osw = new OutputStreamWriter(output);

				ve.evaluate(context, osw, tag, new InputStreamReader(input));
				osw.flush();

				changedName = (String) context.get("filename"); //$NON-NLS-1$
				if (changedName != null) {
					context.remove("filename"); //$NON-NLS-1$
				}

				return new ByteArrayInputStream(output.toByteArray());
			} finally {
				input.close();
			}
		} catch (final Exception ex) {
			throw new CoreException(new Status(IStatus.ERROR, RienaWizardPlugin.getDefault().getBundle()
					.getSymbolicName(), ex.getMessage(), ex));
		}
	}

	static private class EclipseLogChute implements LogChute {
		public void init(final RuntimeServices runtime) throws Exception {
		}

		public boolean isLevelEnabled(final int level) {
			return convertLevel(level) != null;
		}

		public void log(final int level, final String message) {
			log(level, message, null);
		}

		public void log(final int level, final String message, final Throwable throwable) {
			final Integer converted = convertLevel(level);
			if (converted != null) {
				StatusManager.getManager().handle(
						new Status(converted, RienaWizardPlugin.getDefault().getBundle().getSymbolicName(), message,
								throwable), StatusManager.LOG);
			}
		}

		private Integer convertLevel(final int level) {
			switch (level) {
			case LogChute.ERROR_ID:
				return IStatus.ERROR;

			case LogChute.INFO_ID:
				return IStatus.INFO;

			case LogChute.WARN_ID:
				return IStatus.WARNING;
			default:
				return null;
			}
		}
	}

	public String getChangedFileName() {
		return changedName;
	}
}
