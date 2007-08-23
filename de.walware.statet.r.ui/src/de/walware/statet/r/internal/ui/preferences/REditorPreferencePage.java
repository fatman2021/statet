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

package de.walware.statet.r.internal.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import de.walware.eclipsecommons.preferences.Preference;
import de.walware.eclipsecommons.ui.dialogs.IStatusChangeListener;
import de.walware.eclipsecommons.ui.preferences.ConfigurationBlockPreferencePage;
import de.walware.eclipsecommons.ui.util.LayoutUtil;

import de.walware.statet.ext.ui.preferences.ManagedConfigurationBlock;
import de.walware.statet.r.internal.ui.RUIPreferenceInitializer;
import de.walware.statet.r.ui.editors.REditorOptions;


/**
 * Preference page for 'R Editor Options'
 */
public class REditorPreferencePage extends ConfigurationBlockPreferencePage<REditorConfigurationBlock> {

	
	public REditorPreferencePage() {
	}
	
	@Override
	protected REditorConfigurationBlock createConfigurationBlock() {
		return new REditorConfigurationBlock(createStatusChangedListener());
	}
	
}


class REditorConfigurationBlock extends ManagedConfigurationBlock {
	

	private Button fSmartInsertControl;
	private Button[] fSmartInsertOnPasteControl;
	private Button[] fSmartInsertCloseCurlyBracketsControl;
	private Button[] fSmartInsertCloseRoundBracketsControl;
	private Button[] fSmartInsertCloseSquareBracketsControl;
	private Button[] fSmartInsertCloseSpecialControl;
	private Button[] fSmartInsertCloseStringsControl;
	private Button fFoldingEnableControl;
	private Button fSpellEnableControl;
	
	
	public REditorConfigurationBlock(IStatusChangeListener statusListener) {
		super(null, statusListener);
	}
	
	@Override
	protected String[] getChangedContexts() {
		return new String[] {
				REditorOptions.CONTEXT_ID,
		};
	}
	
	@Override
	public void createContents(Composite pageComposite, IWorkbenchPreferenceContainer container, IPreferenceStore preferenceStore) {
		super.createContents(pageComposite, container, preferenceStore);
		// Preferences
		List<Preference> prefs = new ArrayList<Preference>();
		prefs.add(REditorOptions.PREF_SMARTINSERT_BYDEFAULT_ENABLED);
		prefs.add(REditorOptions.PREF_SMARTINSERT_ONPASTE_ENABLED);
		prefs.add(REditorOptions.PREF_SMARTINSERT_CLOSECURLY_ENABLED);
		prefs.add(REditorOptions.PREF_SMARTINSERT_CLOSEROUND_ENABLED);
		prefs.add(REditorOptions.PREF_SMARTINSERT_CLOSESQUARE_ENABLED);
		prefs.add(REditorOptions.PREF_SMARTINSERT_CLOSESPECIAL_ENABLED);
		prefs.add(REditorOptions.PREF_SMARTINSERT_CLOSESTRINGS_ENABLED);
		
		prefs.add(RUIPreferenceInitializer.CONSOLE_SMARTINSERT_CLOSECURLY_ENABLED);
		prefs.add(RUIPreferenceInitializer.CONSOLE_SMARTINSERT_CLOSEROUND_ENABLED);
		prefs.add(RUIPreferenceInitializer.CONSOLE_SMARTINSERT_CLOSESQUARE_ENABLED);
		prefs.add(RUIPreferenceInitializer.CONSOLE_SMARTINSERT_CLOSESPECIAL_ENABLED);
		prefs.add(RUIPreferenceInitializer.CONSOLE_SMARTINSERT_CLOSESTRINGS_ENABLED);
		
		prefs.add(REditorOptions.PREF_FOLDING_ASDEFAULT_ENABLED);
		prefs.add(REditorOptions.PREF_SPELLCHECKING_ENABLED);

		
		setupPreferenceManager(container, prefs.toArray(new Preference[prefs.size()]));
		
		// Controls
		Link link;
		Label label;
		Group group;
		GridData gd;
		int n;
		
		group = new Group(pageComposite, SWT.NONE);
		group.setText(Messages.REditorOptions_SmartInsert_label+':');
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		n = 5;
		group.setLayout(LayoutUtil.applyGroupDefaults(new GridLayout(), n));
		fSmartInsertControl = new Button(group, SWT.CHECK);
		fSmartInsertControl.setText(Messages.REditorOptions_SmartInsert_AsDefault_label);
		fSmartInsertControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, n, 1));
		link = addLinkControl(group, Messages.REditorOptions_SmartInsert_description);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false, n, 1);
		gd.widthHint = 140;
		link.setLayoutData(gd);

		LayoutUtil.addGDDummy(group);
		LayoutUtil.addGDDummy(group);
		label = new Label(group, SWT.CENTER);
		label.setText(Messages.REditorOptions_SmartInsert_ForEditor_header);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		label = new Label(group, SWT.CENTER);
		label.setText(Messages.REditorOptions_SmartInsert_ForConsole_header);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		Label dummy = new Label(group, SWT.NONE);
		dummy.setVisible(false);
		dummy.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 7));
		
		fSmartInsertOnPasteControl = createOption(group, Messages.REditorOptions_SmartInsert_OnPaste_label, null, false);
		fSmartInsertCloseCurlyBracketsControl = createOption(group, Messages.REditorOptions_SmartInsert_CloseAuto_label, Messages.REditorOptions_SmartInsert_CloseCurly_label, true);
		fSmartInsertCloseRoundBracketsControl = createOption(group, null, Messages.REditorOptions_SmartInsert_CloseRound_label, true);
		fSmartInsertCloseSquareBracketsControl = createOption(group, null, Messages.REditorOptions_SmartInsert_CloseSquare_label, true);
		fSmartInsertCloseSpecialControl = createOption(group, null, Messages.REditorOptions_SmartInsert_ClosePercent_label, true);
		fSmartInsertCloseStringsControl = createOption(group, null, Messages.REditorOptions_SmartInsert_CloseString_label, true);

		LayoutUtil.addSmallFiller(pageComposite, false);
		fFoldingEnableControl = new Button(pageComposite, SWT.CHECK);
		fFoldingEnableControl.setText(Messages.REditorOptions_Folding_Enable_label);
		fFoldingEnableControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		LayoutUtil.addSmallFiller(pageComposite, false);
		fSpellEnableControl = new Button(pageComposite, SWT.CHECK);
		fSpellEnableControl.setText(Messages.REditorOptions_SpellChecking_Enable_label);
		fSpellEnableControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		link = addLinkControl(pageComposite, Messages.REditorOptions_SpellChecking_note);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.widthHint = 140;
		gd.horizontalIndent = LayoutUtil.defaultIndent();
		link.setLayoutData(gd);

		// Binding
		createDbc();
		updateControls();
	}
	
	private Button[] createOption(Composite composite, String text1, String text2, boolean console) {
		GridData gd;
		if (text1 != null) {
			Label label = new Label(composite, SWT.NONE);
			if (text2 == null) {
				label.setText(text1+':');
				gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
			}
			else {
				label.setText(text1);
				gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			}
			label.setLayoutData(gd);
		}
		else {
			LayoutUtil.addGDDummy(composite);
		}
		if (text2 != null) {
			Label label = new Label(composite, SWT.NONE);
			label.setText(text2+':');
			gd = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
			label.setLayoutData(gd);
		}
		Button button0 = new Button(composite, SWT.CHECK);
		gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		button0.setLayoutData(gd);
		
		Button button1 = new Button(composite, SWT.CHECK);
		gd = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		button1.setLayoutData(gd);
		if (!console) {
			button1.setEnabled(false);
		}
		return new Button[] { button0, button1 };
	}
	
	@Override
	protected void addBindings(DataBindingContext dbc, Realm realm) {
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertControl),
				createObservable(REditorOptions.PREF_SMARTINSERT_BYDEFAULT_ENABLED),
				null, null);
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertOnPasteControl[0]),
				createObservable(REditorOptions.PREF_SMARTINSERT_ONPASTE_ENABLED),
				null, null);
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertCloseCurlyBracketsControl[0]),
				createObservable(REditorOptions.PREF_SMARTINSERT_CLOSECURLY_ENABLED),
				null, null);
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertCloseRoundBracketsControl[0]),
				createObservable(REditorOptions.PREF_SMARTINSERT_CLOSEROUND_ENABLED),
				null, null);
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertCloseSquareBracketsControl[0]),
				createObservable(REditorOptions.PREF_SMARTINSERT_CLOSESQUARE_ENABLED),
				null, null);
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertCloseSpecialControl[0]),
				createObservable(REditorOptions.PREF_SMARTINSERT_CLOSESPECIAL_ENABLED),
				null, null);
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertCloseStringsControl[0]),
				createObservable(REditorOptions.PREF_SMARTINSERT_CLOSESTRINGS_ENABLED),
				null, null);
		
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertCloseCurlyBracketsControl[1]),
				createObservable(RUIPreferenceInitializer.CONSOLE_SMARTINSERT_CLOSECURLY_ENABLED),
				null, null);
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertCloseRoundBracketsControl[1]),
				createObservable(RUIPreferenceInitializer.CONSOLE_SMARTINSERT_CLOSEROUND_ENABLED),
				null, null);
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertCloseSquareBracketsControl[1]),
				createObservable(RUIPreferenceInitializer.CONSOLE_SMARTINSERT_CLOSESQUARE_ENABLED),
				null, null);
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertCloseSpecialControl[1]),
				createObservable(RUIPreferenceInitializer.CONSOLE_SMARTINSERT_CLOSESPECIAL_ENABLED),
				null, null);
		dbc.bindValue(SWTObservables.observeSelection(fSmartInsertCloseStringsControl[1]),
				createObservable(RUIPreferenceInitializer.CONSOLE_SMARTINSERT_CLOSESTRINGS_ENABLED),
				null, null);

		dbc.bindValue(SWTObservables.observeSelection(fFoldingEnableControl),
				createObservable(REditorOptions.PREF_FOLDING_ASDEFAULT_ENABLED),
				null, null);
		dbc.bindValue(SWTObservables.observeSelection(fSpellEnableControl),
				createObservable(REditorOptions.PREF_SPELLCHECKING_ENABLED),
				null, null);
	}

}