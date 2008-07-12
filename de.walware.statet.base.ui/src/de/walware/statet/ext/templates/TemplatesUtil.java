/*******************************************************************************
 * Copyright (c) 2005 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.statet.ext.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.text.edits.RangeMarker;
import org.eclipse.text.edits.TextEdit;

import de.walware.eclipsecommons.preferences.PreferencesUtil;

import de.walware.statet.base.core.StatetProject;
import de.walware.statet.base.core.preferences.TaskTagsPreferences;


public class TemplatesUtil {
	
	
	public static String searchIndentation(final IDocument document, final int offset) {
		try {
			final IRegion region = document.getLineInformationOfOffset(offset);
			final String lineContent = document.get(region.getOffset(), region.getLength());
			return searchIndentation(lineContent);
		} 
		catch (final BadLocationException e) {
			return ""; //$NON-NLS-1$
		}
	}	
	
	private static String searchIndentation(final String text) throws BadLocationException {
		int i = 0;
		for (; i < text.length(); i++) {
			final char c = text.charAt(i);
			if (!(c == ' ' || c == '\t'))
				break;
		}
		return text.substring(0, i);
	}
	
	public static void positionsToVariables(final List<TextEdit> positions, final TemplateVariable[] variables) {
		final Iterator iterator = positions.iterator();
		
		for (int i= 0; i != variables.length; i++) {
			final TemplateVariable variable = variables[i];
			
			final int[] offsets= new int[variable.getOffsets().length];
			for (int j= 0; j != offsets.length; j++)
				offsets[j]= ((TextEdit) iterator.next()).getOffset();
			
			variable.setOffsets(offsets);
		}
	}
	
	public static List<TextEdit> variablesToPositions(final TemplateVariable[] variables) {
		final List<TextEdit> positions = new ArrayList<TextEdit>(5);
		for (int i= 0; i != variables.length; i++) {
			final int[] offsets= variables[i].getOffsets();
			
			// trim positions off whitespace
			final String value = variables[i].getDefaultValue();
			int wsStart = 0;
			while (wsStart < value.length() && Character.isWhitespace(value.charAt(wsStart)) && !isLineDelimiterChar(value.charAt(wsStart)))
				wsStart++;
			
			variables[i].getValues()[0]= value.substring(wsStart);
			
			for (int j= 0; j != offsets.length; j++) {
				offsets[j] += wsStart;
				positions.add(new RangeMarker(offsets[j], 0));
			}
		}
		return positions;
	}
	
	public static String evaluateTemplate(final StatextCodeTemplatesContext context, final Template template) throws CoreException {
		TemplateBuffer buffer;
		try {
			buffer = context.evaluate(template);
		} catch (final BadLocationException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		} catch (final TemplateException e) {
			throw new CoreException(Status.CANCEL_STATUS);
		}
		if (buffer == null)
			return null;
		final String str = buffer.getString();
//		if (Strings.containsOnlyWhitespaces(str)) {
//			return null;
//		}
		return str;
	}
	
	public static String getTodoTaskTag(final StatetProject project) {
		final TaskTagsPreferences taskPrefs = (project != null) ?
				new TaskTagsPreferences(project) :
				new TaskTagsPreferences(PreferencesUtil.getInstancePrefs());
		
		final String[] markers = taskPrefs.getTags();
		
		if (markers == null || markers.length == 0)
			return null;
		return markers[0];
	}
	
	
	private static boolean isLineDelimiterChar(final char c) {
		return (c == '\r' || c == '\n');
	}
	
}
