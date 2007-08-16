/*******************************************************************************
 * Copyright (c) 2005-2006 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.statet.r.internal.ui;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.walware.eclipsecommons.preferences.PreferencesUtil;
import de.walware.eclipsecommons.ui.util.ImageRegistryUtil;

import de.walware.statet.base.ui.StatetUIServices;
import de.walware.statet.ext.ui.editors.StatextSourceViewerConfiguration;
import de.walware.statet.r.codegeneration.RCodeTemplatesContextType;
import de.walware.statet.r.codegeneration.RdCodeTemplatesContextType;
import de.walware.statet.r.ui.RUI;
import de.walware.statet.r.ui.editors.RDocumentProvider;
import de.walware.statet.r.ui.editors.RdDocumentProvider;
import de.walware.statet.r.ui.editors.templates.REditorTemplatesContextType;


/**
 * The main plugin class to be used in the desktop.
 */
public class RUIPlugin extends AbstractUIPlugin {

	
	public static final int INTERNAL_ERROR = 100;
	public static final int IO_ERROR = 101;
	

	public static final String IMG_WIZBAN_NEWRDFILE = RUI.PLUGIN_ID + "/image/wizban/new.rd-file"; //$NON-NLS-1$
	public static final String IMG_WIZBAN_NEWRFILE = RUI.PLUGIN_ID + "/image/wizban/new.r-file"; //$NON-NLS-1$
	public static final String IMG_WIZBAN_NEWRPROJECT = RUI.PLUGIN_ID + "/image/wizban/new.r-project"; //$NON-NLS-1$

	
	private static final String R_CODE_TEMPLATES_KEY  = "de.walware.statet.r.ui.text.r_code_templates"; //$NON-NLS-1$
	private static final String RD_CODE_TEMPLATES_KEY = "de.walware.statet.r.ui.text.rd_code_templates"; //$NON-NLS-1$
	private static final String R_EDITOR_TEMPLATES_KEY  = "de.walware.statet.r.ui.text.r_editor_templates"; //$NON-NLS-1$
	

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void logError(int code, String message, Throwable e) {
		log(new Status(IStatus.ERROR, RUI.PLUGIN_ID, code, message, e));
	}


	//The shared instance.
	private static RUIPlugin gPlugin;

	private RDocumentProvider fRDocumentProvider;
	private RdDocumentProvider fRdDocumentProvider;

	private IPreferenceStore fEditorPreferenceStore;
	
	private RIdentifierGroups fIdentifierGroups;
	
	private TemplateStore fRCodeTemplatesStore;
	private ContextTypeRegistry fRCodeTemplatesContextTypeRegistry;
	private TemplateStore fRdCodeTemplatesStore;
	private ContextTypeRegistry fRdCodeTemplatesContextTypeRegistry;

	private TemplateStore fREditorTemplatesStore;
	private ContextTypeRegistry fREditorContextTypeRegistry;
	
	
	/**
	 * The constructor.
	 */
	public RUIPlugin() {
		gPlugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			fRDocumentProvider = null;
			fRdDocumentProvider = null;
			fEditorPreferenceStore = null;
			fRCodeTemplatesStore = null;
			fRCodeTemplatesContextTypeRegistry = null;
			fRdCodeTemplatesStore = null;
			fRdCodeTemplatesContextTypeRegistry = null;
			fREditorTemplatesStore = null;
			fREditorContextTypeRegistry = null;
		} finally {
			gPlugin = null;
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static RUIPlugin getDefault() {
		
		return gPlugin;
	}

	@Override
	protected ImageRegistry createImageRegistry() {
		
		return StatetUIServices.getSharedImageRegistry();
	}
	
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {

		ImageRegistryUtil util = new ImageRegistryUtil(this);
		util.register(IMG_WIZBAN_NEWRPROJECT, ImageRegistryUtil.T_WIZBAN, "new_r-project.png"); //$NON-NLS-1$
		util.register(IMG_WIZBAN_NEWRFILE, ImageRegistryUtil.T_WIZBAN, "new_r-file.png"); //$NON-NLS-1$
		util.register(IMG_WIZBAN_NEWRDFILE, ImageRegistryUtil.T_WIZBAN, "new_rd-file.png"); //$NON-NLS-1$
		
		util.register(RUI.IMG_OBJ_R_ENVIRONMENT, ImageRegistryUtil.T_OBJ, "r-env.png"); //$NON-NLS-1$
	}
	
	
    public synchronized RDocumentProvider getRDocumentProvider() {
		if (fRDocumentProvider == null) {
			fRDocumentProvider = new RDocumentProvider();
		}
		return fRDocumentProvider;
	}

    public synchronized RdDocumentProvider getRdDocumentProvider() {
		if (fRdDocumentProvider == null) {
			fRdDocumentProvider = new RdDocumentProvider();
		}
		return fRdDocumentProvider;
	}
    
    
    public IPreferenceStore getEditorPreferenceStore() {
    	if (fEditorPreferenceStore == null) {
    		fEditorPreferenceStore = StatextSourceViewerConfiguration.createCombinedPreferenceStore(
    			RUIPlugin.getDefault().getPreferenceStore());
    	}
    	return fEditorPreferenceStore;
    }
	
	public synchronized RIdentifierGroups getRIdentifierGroups() {
		if (fIdentifierGroups == null) {
			fIdentifierGroups = new RIdentifierGroups(PreferencesUtil.getInstancePrefs());
		}
		return fIdentifierGroups;
	}

	/**
	 * Returns the template context type registry for the code generation
	 * templates.
	 * 
	 * @return the template context type registry
	 */
	public synchronized ContextTypeRegistry getRCodeGenerationTemplateContextRegistry() {
		if (fRCodeTemplatesContextTypeRegistry == null) {
			fRCodeTemplatesContextTypeRegistry = new ContributionContextTypeRegistry();
			
			RCodeTemplatesContextType.registerContextTypes(fRCodeTemplatesContextTypeRegistry);
		}
		return fRCodeTemplatesContextTypeRegistry;
	}

	/**
	 * Returns the template store for the code generation templates.
	 * 
	 * @return the template store
	 */
	public synchronized TemplateStore getRCodeGenerationTemplateStore() {
		if (fRCodeTemplatesStore == null) {
			fRCodeTemplatesStore = new ContributionTemplateStore(
					getRCodeGenerationTemplateContextRegistry(), getPreferenceStore(), R_CODE_TEMPLATES_KEY);
			try {
				fRCodeTemplatesStore.load();
			} catch (IOException e) {
				RUIPlugin.logError(IO_ERROR, "Error occured when loading 'R code generation' template store.", e); //$NON-NLS-1$
			}
		}
		return fRCodeTemplatesStore;
	}
	
	/**
	 * Returns the template context type registry for the code generation
	 * templates.
	 * 
	 * @return the template context type registry
	 */
	public synchronized ContextTypeRegistry getRdCodeGenerationTemplateContextRegistry() {
		if (fRdCodeTemplatesContextTypeRegistry == null) {
			fRdCodeTemplatesContextTypeRegistry = new ContributionContextTypeRegistry();

			RdCodeTemplatesContextType.registerContextTypes(fRdCodeTemplatesContextTypeRegistry);
		}
		return fRdCodeTemplatesContextTypeRegistry;
	}

	/**
	 * Returns the template store for the code generation templates.
	 * 
	 * @return the template store
	 */
	public synchronized TemplateStore getRdCodeGenerationTemplateStore() {
		if (fRdCodeTemplatesStore == null) {
			fRdCodeTemplatesStore = new ContributionTemplateStore(
					getRdCodeGenerationTemplateContextRegistry(), getPreferenceStore(), RD_CODE_TEMPLATES_KEY);
			try {
				fRdCodeTemplatesStore.load();
			} catch (IOException e) {
				RUIPlugin.logError(IO_ERROR, "Error occured when loading 'Rd code generation' template store.", e); //$NON-NLS-1$
			}
		}
		return fRdCodeTemplatesStore;
	}


	/**
	 * Returns the template context type registry for the code generation
	 * templates.
	 * 
	 * @return the template context type registry
	 */
	public synchronized ContextTypeRegistry getREditorTemplateContextRegistry() {
		if (fREditorContextTypeRegistry == null) {
			fREditorContextTypeRegistry = new ContributionContextTypeRegistry();
			
			REditorTemplatesContextType.registerContextTypes(fREditorContextTypeRegistry);
		}
		return fREditorContextTypeRegistry;
	}

	/**
	 * Returns the template store for the code generation templates.
	 * 
	 * @return the template store
	 */
	public synchronized TemplateStore getREditorTemplateStore() {
		if (fREditorTemplatesStore == null) {
			fREditorTemplatesStore = new ContributionTemplateStore(
					getREditorTemplateContextRegistry(), getPreferenceStore(), R_EDITOR_TEMPLATES_KEY);
			try {
				fREditorTemplatesStore.load();
			} catch (IOException e) {
				RUIPlugin.logError(IO_ERROR, "Error occured when loading 'R Editor' template store.", e); //$NON-NLS-1$
			}
		}
		return fREditorTemplatesStore;
	}
	
}
