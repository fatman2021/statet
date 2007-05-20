/*******************************************************************************
 * Copyright (c) 2007 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.statet.r.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;

import de.walware.eclipsecommons.preferences.IPreferenceAccess;

import de.walware.statet.base.core.preferences.TaskTagsPreferences;
import de.walware.statet.ext.ui.editors.SourceViewerConfigurator;
import de.walware.statet.r.core.IRCoreAccess;
import de.walware.statet.r.core.RCodeStyleSettings;
import de.walware.statet.r.core.RCore;
import de.walware.statet.r.ui.text.r.RBracketPairMatcher;


/**
 * Configurator for R code source viewers.
 */
public class RSourceViewerConfigurator extends SourceViewerConfigurator
		implements IRCoreAccess, PropertyChangeListener {

	
	private static final Set<String> INPUT_CHANGE_CONTEXTS = new HashSet<String>(Arrays.asList(new String[] {
			RCodeStyleSettings.CONTEXT_ID, 
			TaskTagsPreferences.CONTEXT_ID,
	}));
	
	
	private REditor fEditor;
	private RSourceViewerConfiguration fConfig;
	
	private RCodeStyleSettings fRCodeStyleCopy;
	private IRCoreAccess fSourceCoreAccess;
	
	private boolean fUpdateCompleteConfig;
	private boolean fUpdateTextPresentation;
	private boolean fUpdateTabSize;
	private boolean fUpdateIndent;
	private boolean fUpdateQuickFix;
	
	
	public RSourceViewerConfigurator(IRCoreAccess core, IPreferenceStore store) {
		setSource(core);
		setPreferenceStore(store);
		fRCodeStyleCopy = new RCodeStyleSettings();
		fRCodeStyleCopy.load(fSourceCoreAccess.getRCodeStyle());
		fRCodeStyleCopy.resetDirty();
		fRCodeStyleCopy.addPropertyChangeListener(this);
		
		setPairMatcher(new RBracketPairMatcher());
	}
	
	@Override
	public IDocumentSetupParticipant getDocumentSetupParticipant() {
		return new RDocumentSetupParticipant();
	}
	
	public void setConfiguration(RSourceViewerConfiguration config) {
		fConfig = config;
		super.setConfiguration(config);
	}
	
	public void setSource(IRCoreAccess newAccess) {
		if (newAccess == null) {
			newAccess = RCore.getWorkbenchAccess();
		}
		if (fSourceCoreAccess != newAccess) {
			fSourceCoreAccess = newAccess;
			handleSettingsChanged(null, null);
		}
	}

	public void setTarget(REditor editor, ISourceViewer viewer) {
		fEditor = editor;
		fIsConfigured = true;
		setTarget(viewer, false);
	}
	
	@Override
	public void setTarget(ISourceViewer viewer, boolean configure) {
		fUpdateIndent = true;
		super.setTarget(viewer, configure);
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		if (name.equals(RCodeStyleSettings.PROP_TAB_SIZE)) {
			fUpdateTabSize = true;
			fUpdateIndent = true;
			return;
		}
		if (name.equals(RCodeStyleSettings.PROP_INDENT_SPACES_COUNT)
				|| name.equals(RCodeStyleSettings.PROP_REPLACE_TABS_WITH_SPACES)
				|| name.equals(RCodeStyleSettings.PROP_INDENT_DEFAULT_TYPE)) {
			fUpdateIndent = true;
			return;
		}
	}
	
	public boolean handleSettingsChanged(Set<String> contexts, Object options) {
		ISourceViewer viewer = getSourceViewer();
		if (viewer == null || fConfig == null) {
			return false;
		}
		if (contexts == null) {
			contexts = INPUT_CHANGE_CONTEXTS;
		}
		Point selectedRange = viewer.getSelectedRange();
		
		if (contexts.contains(RCodeStyleSettings.CONTEXT_ID)) {
			fRCodeStyleCopy.load(fSourceCoreAccess.getRCodeStyle());
		}
		if (contexts.contains(REditorOptions.CONTEXT_ID) && fEditor != null) {
			fUpdateCompleteConfig = true;
			fUpdateQuickFix = true;
			SpellingProblem.removeAllInActiveEditor(fEditor, null);
		}
		fUpdateTextPresentation = fConfig.handleSettingsChanged(contexts, viewer);
		
		updateSourceViewer(viewer);
		viewer.setSelectedRange(selectedRange.x, selectedRange.y);
		fRCodeStyleCopy.resetDirty();
		return false;
	}
	
	protected void updateSourceViewer(ISourceViewer viewer) {
		if (fUpdateCompleteConfig) {
			reconfigureSourceViewer();
		}
		else {
			if (fUpdateTabSize) {
				viewer.getTextWidget().setTabs(fConfig.getTabWidth(viewer));
			}
			if (fUpdateTextPresentation) {
				viewer.invalidateTextPresentation();
			}
			if (fUpdateIndent && fEditor != null) {
				fEditor.updateSettings(fUpdateIndent);
			}
		}
		if (fUpdateQuickFix && fEditor != null) {
			IAction quickAssistAction = fEditor.getAction(ITextEditorActionConstants.QUICK_ASSIST);
			if (quickAssistAction instanceof IUpdate) {
				((IUpdate) quickAssistAction).update();
			}
		}

		fUpdateCompleteConfig = false;
		fUpdateTextPresentation = false;
		fUpdateTabSize = false;
		fUpdateIndent = false;
		fUpdateQuickFix = false;
	}
	
	public RCodeStyleSettings getRCodeStyle() {
		return fRCodeStyleCopy;
	}

	public IPreferenceAccess getPrefs() {
		return fSourceCoreAccess.getPrefs();
	}

}
