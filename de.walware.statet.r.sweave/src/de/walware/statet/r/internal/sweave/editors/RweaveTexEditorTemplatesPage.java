/*******************************************************************************
 * Copyright (c) 2008 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.statet.r.internal.sweave.editors;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.texteditor.templates.TemplatesView;

import de.walware.eclipsecommons.templates.TemplateVariableProcessor;

import de.walware.statet.base.ui.sourceeditors.ExtEditorTemplatesPage;
import de.walware.statet.base.ui.sourceeditors.SourceViewerConfigurator;

import de.walware.statet.r.core.RCore;
import de.walware.statet.r.internal.sweave.Rweave;
import de.walware.statet.r.internal.ui.RUIPlugin;
import de.walware.statet.r.ui.editors.RTemplateSourceViewerConfigurator;
import de.walware.statet.r.ui.editors.templates.REditorContext;
import de.walware.statet.r.ui.editors.templates.REditorTemplatesContextType;


/**
 * Page for {@link RweaveTexEditor} / {@link TemplatesView}
 */
public class RweaveTexEditorTemplatesPage extends ExtEditorTemplatesPage {
	
	
	private SourceViewerConfigurator fRPreviewConfigurator;
	
	
	public RweaveTexEditorTemplatesPage(final RweaveTexEditor editor, final ISourceViewer viewer) {
		super(editor, viewer);
	}
	
	
	@Override
	protected String getPreferencePageId() {
		return "de.walware.statet.r.preferencePages.REditorTemplates"; 
	}
	
	@Override
	protected IPreferenceStore getTemplatePreferenceStore() {
		return RUIPlugin.getDefault().getPreferenceStore();
	}
	
	@Override
	protected TemplateStore getTemplateStore() {
		return RUIPlugin.getDefault().getREditorTemplateStore();
	}
	
	@Override
	protected ContextTypeRegistry getContextTypeRegistry() {
		return RUIPlugin.getDefault().getREditorTemplateContextRegistry();
	}
	
	@Override
	protected String[] getContextTypeIds(final IDocument document, final int offset) {
		try {
			final String contentType = TextUtilities.getContentType(document, Rweave.R_TEX_PARTITIONING, offset, true);
			if (Rweave.isRPartition(contentType)) {
				return new String[] { REditorTemplatesContextType.RCODE_CONTEXTTYPE };
			}
		}
		catch (final BadLocationException e) {
		}
		return new String[0];
	}
	
	@Override
	protected DocumentTemplateContext createContext(final IDocument document, final Template template, final int offset, final int length) {
		final TemplateContextType contextType = getContextTypeRegistry().getContextType(template.getContextTypeId());
		if (contextType != null) {
			return new REditorContext(contextType, document, offset, length, getEditor());
		}
		return null;
	}
	
	@Override
	protected SourceViewerConfigurator getTemplatePreviewConfig(final Template template, final TemplateVariableProcessor templateProcessor) {
		if (fRPreviewConfigurator == null) {
			fRPreviewConfigurator = new RTemplateSourceViewerConfigurator(RCore.getWorkbenchAccess(), templateProcessor);
		}
		return fRPreviewConfigurator;
	}
	
	@Override
	protected SourceViewerConfigurator getTemplateEditConfig(final Template template, final TemplateVariableProcessor templateProcessor) {
		return new RTemplateSourceViewerConfigurator(RCore.getWorkbenchAccess(), templateProcessor);
	}
	
}
