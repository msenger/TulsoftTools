// JFileChooserWithHistory.java
//
// Created: September 2005
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
import org.tulsoft.tools.gui.SwingUtils;

import javax.swing.JPanel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

/**
 * A component combining together a JFileChooser and a text field
 * remembering what files were already selected. The file chooser is
 * represented by a chooser button and is open by clicking on the
 * button. User has access to all these three components using methods
 * {@link #getTextField}, {@link #getFileChooser} and {@link
 * #getChooserButton}. <p>
 *
 * All constructors create a file chooser component using an empty
 * constructor, and using the other parameters (if any) to initialize
 * the text field component. See {@link JTextFieldWithHistory}
 * explaining more about various constructors parameters. The most
 * useful one is probably the <tt>preferenceNodeName</tt> that allows
 * (when used) to store and reload selected file names in/from a user
 * preferences files (a persistent storage).<p>

 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: JFileChooserWithHistory.java,v 1.4 2007/03/25 18:55:18 marsenger Exp $
 */

public class JFileChooserWithHistory
    extends JPanel {

    /** Version and date of the last update.*/
    public static final String VERSION = "$Id: JFileChooserWithHistory.java,v 1.4 2007/03/25 18:55:18 marsenger Exp $";

    // components
    protected JTextFieldWithHistory textField;
    protected JFileChooser chooser;
    protected JButton chooserButton;
    protected Icon chooserButtonIcon;

    /*********************************************************************
     * An empty contructor. No initial value, no history values used.
     ********************************************************************/
    public JFileChooserWithHistory() {
	super();
	createItself();
    }

    /*********************************************************************
     * A constructor that initializes current directory. <p>
     *
     * @param initValue is a value to be set as current directory
     ********************************************************************/
    public JFileChooserWithHistory (String initValue) {
	super();
	textField = new JTextFieldWithHistory (initValue);
	createItself();
    }

    /*********************************************************************
     * A constructor taking history texts from user's preferences. <p>
     *
     * @param initValue is a value to be set as current directory
     * @param preferenceNodeName a name of the user Preferences node
     * where this class stores/finds a history list
     ********************************************************************/
    public JFileChooserWithHistory (String initValue,
				    String preferenceNodeName) {
	super();
	textField = new JTextFieldWithHistory (initValue, preferenceNodeName);
	createItself();
    }

    /*********************************************************************
     * A constructor taking history texts from user's preferences. <p>
     *
     * @param initValue is a value to be set as current directory
     * @param c is a class whose name will be used for creating a
     * preference node name (usually this is a class that called this
     * method)
     * @param preferenceNodeName a name that will be added to a name
     * created from 'c'; together they create a user Preferences node
     * where this class stores/finds a list with history texts
     ********************************************************************/
    public JFileChooserWithHistory (String initValue,
				    Class c,
				    String preferenceNodeName) {
	super();
	textField = new JTextFieldWithHistory (initValue, c, preferenceNodeName);
	createItself();
    }

    /*********************************************************************
     * Another constructor taking history texts from user's
     * preferences. This is only a convenient shortcut to the previous
     * constructor {@link
     * #JFileChooserWithHistory(String,Class,String)}. <p>
     *
     * @param initValue is a value to be set as current directory
     * @param owner is used just to give its class name - and from
     * that a preference node name
     * @param preferenceNodeName a name that will be added to a name
     * created from 'owner'; together they create a user Preferences
     * node where this class stores/finds a list with history texts
     ********************************************************************/
    public JFileChooserWithHistory (String initValue,
				    Object owner,
				    String preferenceNodeName) {
	super();
	textField = new JTextFieldWithHistory (initValue, owner, preferenceNodeName);
	createItself();
    }

    /*********************************************************************
     * An another constructor taking history values as an array. <p>
     *
     * @param initValue is a value to be set as current directory
     * @param historyTexts is a list of history texts
     ********************************************************************/
    public JFileChooserWithHistory (String initValue,
				    String[] historyTexts) {
	super();
	textField = new JTextFieldWithHistory (initValue, historyTexts);
	createItself();
    }

    /*********************************************************************
     * This is called from several constructors in order to build
     * itself. In time of caling this, the 'textField' should be
     * already created.
     ********************************************************************/
    protected void createItself() {
	chooser = new JFileChooser();
	String initValue = textField.getText();
	if (UUtils.notEmpty (initValue)) {
	    File file = new File (initValue);
	    chooser.setSelectedFile (file);
	}
	textField.addActionListener (new ActionListener() {
		public void actionPerformed (ActionEvent e) {
		    String fileName = textField.getText();
		    if (UUtils.notEmpty (fileName))
			chooser.setSelectedFile (new File (fileName));
		}
	    });

   	chooserButton = getChooserButton();

	Dimension dim = new Dimension (chooserButton.getPreferredSize().width,
				       textField.getPreferredSize().height);
	chooserButton.setPreferredSize (dim);

	setLayout (new GridBagLayout());
 	SwingUtils.addComponent (this, textField,
				 0, 0, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, 1.0, 0.0);
  	SwingUtils.addComponent (this, chooserButton,
				 1, 0, 1, 1, GridBagConstraints.NONE,       GridBagConstraints.WEST, 0.0, 0.0);
    }

    /*********************************************************************
     * 
     ********************************************************************/
    protected Icon getIcon() {
	if (chooserButtonIcon == null)
	    chooserButtonIcon = SwingUtils.createIcon ("images/open.gif", this);
	return chooserButtonIcon;
    }

    /*********************************************************************
     * Get the underlying JFileChooser component.
     ********************************************************************/
    public JFileChooser getFileChooser() {
	return chooser;
    }

    /*********************************************************************
     * Get the underlying JComboBox component.
     ********************************************************************/
    public JTextFieldWithHistory getTextField() {
	return textField;
    }

    /*********************************************************************
     * Get the underlying button that invokes the file chooser.
     ********************************************************************/
    public JButton getChooserButton() {
	if (chooserButton != null)
	    return chooserButton;

   	JButton button = new JButton();
	button.setIcon (getIcon());
        button.setFocusPainted (false);
	button.addActionListener (new ActionListener() {
		public void actionPerformed (ActionEvent e) {
		    if (chooser.showDialog (null, null) != JFileChooser.APPROVE_OPTION)
			return;
		    File file = chooser.getSelectedFile();
		    textField.setText (file.getAbsolutePath());
		}
	    });
	SwingUtils.compact (button);
	return button;
    }

    /*********************************************************************
     * Get...
     ********************************************************************/
    public File getSelectedFile() {
	return chooser.getSelectedFile();
    }

    /*********************************************************************
     * Set...
     ********************************************************************/
    public void setSelectedFile (File file) {
	chooser.setSelectedFile (file);
	textField.setText (file.getAbsolutePath());
    }

    /*********************************************************************
     *
     ********************************************************************/
    public void setEnabled (boolean enabled) {
	chooserButton.setEnabled (enabled);
	textField.setEnabled (enabled);
	AwtUtils.redisplay (this);  // TBD: why is this necessary?
    }


}
