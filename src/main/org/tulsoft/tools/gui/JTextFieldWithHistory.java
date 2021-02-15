// JTextFieldWithHistory.java
//
// Created: November 2000
//
// Copyright 2005 Martin Senger (martin.senger@gmail.com)
//
// Licensed under the Apache License, Version 2.0 (the "License"); you
// may not use this file except in compliance with the License. You
// may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
// implied. See the License for the specific language governing
// permissions and limitations under the License.
//

package org.tulsoft.tools.gui;

import org.tulsoft.shared.UUtils;
import org.tulsoft.shared.PrefsUtils;

import javax.swing.JTextField;
import javax.swing.JComboBox;

import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.util.prefs.Preferences;

import java.awt.Dimension;

/**
 * An extension to the editable JComboBox remembering what texts were
 * already typed. <p>
 *
 * The other features include: <ul>
 *
 *  <li> It makes sure that the same item is not added the second
 *  time.
 *
 *  <li> It can fill an initial history (texts which are in the combo
 *  box from the very beginning) from an array.
 *
 *  <li> Or, it can read an initial history from the user Preferences
 *  (see package <tt>java.util.prefs</tt> for explanation what user
 *  Preferences are and where they are usually stored).
 *
 *  <li> It can stay initially empty even if it has some history
 *  texts.
 *
 *  <li> It uses {@link #setText} and {@link #getText} method
 *  similarly as in JTextComponent, (instead of the native JComboBox
 *  methods, which are also available, {@link #getSelectedItem} and
 *  {@link #setSelectedItem}).
 *
 * </ul><p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: JTextFieldWithHistory.java,v 1.6 2007/03/25 18:55:18 marsenger Exp $
 */

public class JTextFieldWithHistory
    extends SteppedComboBox {

    /** Version and date of the last update.*/
    public static final String VERSION = "$Id: JTextFieldWithHistory.java,v 1.6 2007/03/25 18:55:18 marsenger Exp $";

    protected Preferences myNode;
    JTextField editorComponent;

    final static Font plainFont10 = new Font ("Dialog",  Font.PLAIN, 10);
    final static String HISTORY_NODE_NAME = "AAQnNpPKBSRk-history";

    /*********************************************************************
     * An empty contructor. An initial value of the text field will be
     * empty and no history values will be used. The newly entered
     * values will not be stored in user preferences persistent
     * storage.
     ********************************************************************/
    public JTextFieldWithHistory() {
	this ("");
    }

    /*********************************************************************
     * A constructor that initializes the text field. The newly
     * entered values will not be stored in user preferences
     * persistent storage. <p>
     *
     * @param initValue is a value to be set into the text field
     ********************************************************************/
    public JTextFieldWithHistory (String initValue) {
	super();
	createItself (initValue, null);
    }

    /*********************************************************************
     * A constructor specifying an initial value and all history texts
     * as an array. The newly entered values will not be stored in
     * user preferences persistent storage. <p>
     *
     * @param initValue is a value to be set into the text field
     * @param historyTexts is a list of history texts
     ********************************************************************/
    public JTextFieldWithHistory (String initValue,
				  String[] historyTexts) {
	super();
	createItself (initValue, historyTexts);
    }

    /*********************************************************************
     * A constructor specifying an initial value and a user's
     * preferences node where the history texts will be taken
     * from (and new ones stored into). <p>
     *
     * @param initValue is a value to be set into the text field
     * @param c is a class whose name will be used for creating a
     * preference node name (usually this is a class that called this
     * method)
     * @param preferenceNodeName a name that will be added to a name
     * created from 'c'; together they create a user Preferences node
     * where this class stores/finds a list with history texts
     * @see #JTextFieldWithHistory(String,String) a note about the
     * full resulting node name
     ********************************************************************/
    public JTextFieldWithHistory (String initValue,
				  Class c,
				  String preferenceNodeName) {
	super();
	if (c == null)
	    myNode = PrefsUtils.getNode (preferenceNodeName + "/" + HISTORY_NODE_NAME);
	else
	    myNode = PrefsUtils.getNode (c, preferenceNodeName + "/" + HISTORY_NODE_NAME);
	String[] historyTexts = PrefsUtils.getValues (myNode);
	PrefsUtils.removeKeys (myNode);
	createItself (initValue, historyTexts);
    }

    /*********************************************************************
     * A constructor specifying an initial value and a user's
     * preferences node where the history texts will be taken
     * from (and new ones stored into). <p>
     *
     * Note that the resulting node name will have (additionally to
     * what is given to this method) a separate sub-node created just
     * for this class. Therefore, all keys containing history texts
     * will not be ever mixed with any other keys. <p>
     *
     * @param initValue is a value to be set into the text field
     * @param preferenceNodeName a name of the user Preferences node
     * where this class stores/finds a history list
     ********************************************************************/
    public JTextFieldWithHistory (String initValue,
				  String preferenceNodeName) {
	this (initValue, null, preferenceNodeName);
    }

    /*********************************************************************
     * A constructor specifying an initial value and a user's
     * preferences node where the history texts will be taken from
     * (and new ones stored into). This is only a convenient shortcut
     * to the constructor {@link
     * #JTextFieldWithHistory(String,Class,String)}. <p>
     *
     * @param initValue is a value to be set into the text field
     * @param owner is used just to give its class name - and from
     * that a preference node name
     * @param preferenceNodeName a name that will be added to a name
     * created from 'owner'; together they create a user Preferences
     * node where this class stores/finds a list with history texts
     * @see #JTextFieldWithHistory(String,String) a note about the
     * full resulting node name
     ********************************************************************/
    public JTextFieldWithHistory (String initValue,
				  Object owner,
				  String preferenceNodeName) {
	this (initValue, owner.getClass(), preferenceNodeName);
    }

    /*********************************************************************
     * This is called from several constructors in order to build
     * itself.
     ********************************************************************/
    protected void createItself (String initValue, String[] historyTexts) {
	// set some characteristics
	setEditable (true);
	setFont (plainFont10);

	Dimension d = getPreferredSize();
	setPreferredSize (new Dimension (50, d.height));
	setMinimumSize (getPreferredSize());

	// add history items
	if (historyTexts != null) {
	    for (int i = 0 ; i < historyTexts.length; i++)
		addItem (historyTexts[i]);
 	    if (initValue == null && historyTexts.length > 0)
 		initValue = historyTexts[0];
	}

	editorComponent = (JTextField)getEditor().getEditorComponent();

	// add and select the initial value (if any)
	if (UUtils.isEmpty (initValue)) {
	    setSelectedIndex (-1);
	    editorComponent.setText ("");
	} else {
	    addItem (initValue);
	    setSelectedItem (initValue);
	}

	// catch 'focus lost' to be able automatically add what was typed
	editorComponent.addFocusListener (new FocusListener() {
		public void focusGained (FocusEvent e) {}
		public void focusLost (FocusEvent e) {
		    addItem (editorComponent.getText());
		}
	    });


    }

    /*********************************************************************
     * Add an item to the history but check if it is not already
     * there. If it is there do not duplicate it but move it to the
     * beggining of the list. <p>
     *
     * Only String items are accepted. <p>
     *
     * @param item to be added to the history
     ********************************************************************/
    public void addItem (Object item) {
	synchronized (this) {
	    try {
		String newItem = (String)item;
		if (UUtils.isEmpty (newItem)) return;
		int maxItemLen = 0;
		int indexOfAlreadyExisting = -1;
		for (int i = 0; i < getItemCount(); i++) {
		    String anItem = (String)getItemAt (i);
		    int len = anItem.length();
		    if (len > maxItemLen) maxItemLen = len;
		    if (newItem.equals (anItem)) {
			indexOfAlreadyExisting = i;
			break;
		    }
		}
		if (indexOfAlreadyExisting == -1) {
		    super.addItem (item);
		} else {
		    removeItemAt (indexOfAlreadyExisting);
		    insertItemAt (item, 0);
		}
		setSelectedItem (item);
		int newItemLen = newItem.length();
		if (newItemLen > maxItemLen) {
		    setPopupWidth (getFontMetrics (getFont()).stringWidth (newItem));
		}
		if (myNode != null)
		    PrefsUtils.updateKey (myNode, "" + UUtils.unique(), newItem);
	    } catch (ClassCastException e) {
	    }
	}
    }

    /*********************************************************************
     * Sets a text into this component.
     * It expects only strings, everything else will break.
     *<P>
     * @param t value to be set
     ********************************************************************/
    public void setText (String t) {
	addItem (t);
	setSelectedItem (t);   // needed?
    }

    /*********************************************************************
     * Returns content of this component.
     ********************************************************************/
    public String getText() {
	return (String)getSelectedItem();
    }

    /*********************************************************************
     * Register key listeners directly to the underlying text field.
     ********************************************************************/
    public void addKeyListener (KeyListener l) {
	if (editorComponent != null)
	    editorComponent.addKeyListener (l);
    }

    /*********************************************************************
     * Unegister key listeners directly from the underlying text field.
     ********************************************************************/
    public void removeKeyListener (KeyListener l) {
	if (editorComponent != null)
	    editorComponent.removeKeyListener (l);
    }

    /*********************************************************************
     * Register focus listeners directly to the underlying text field.
     ********************************************************************/
    public void addFocusListener (FocusListener l) {
	if (editorComponent != null)
	    editorComponent.addFocusListener (l);
    }

    /*********************************************************************
     * Unregister focus listeners directly from the underlying text field.
     ********************************************************************/
    public void removeFocusListener (FocusListener l) {
	if (editorComponent != null)
	    editorComponent.removeFocusListener (l);
    }

}
