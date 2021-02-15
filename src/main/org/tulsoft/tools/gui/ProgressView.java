// ProgressView.java
//
// Created: December 1999
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

import org.tulsoft.tools.gui.SwingUtils;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.BoxLayout;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Cursor;

/**
 * A simple progress bar displayed in a separate frame, used mostly
 * for reporting steps of a loading process. <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: ProgressView.java,v 1.1 2005/09/18 13:41:36 marsenger Exp $
 */

public class ProgressView
    extends JPanel {

    /** Version and date of the last update. */
    public static final String VERSION = "$Id: ProgressView.java,v 1.1 2005/09/18 13:41:36 marsenger Exp $";

    // some defaults
    protected boolean isInitialized = false;
    protected String msg = "Loading, please wait...";

    // graphical components
    protected JFrame frame;
    protected JLabel progressLabel = null;
    protected JProgressBar progressBar = null;

    // some look-and-feel
    public Insets getInsets() {
  	return new Insets (40,30,20,30);
    }

    // other properties
    protected int frameWidth = 400;
    protected int frameHeight = 200;

    /**
     * A static instance of this class. It is suitable if you measure
     * a progress activity from many different classes and it is
     * inconvenient to pass around a non-static instance.
     */
    public static ProgressView monitor = new ProgressView();

    /*********************************************************************
     * A constructor for sub-classes.
     ********************************************************************/
    protected ProgressView() {
	this (10);
    }

    /*********************************************************************
     * Default constructor.
     ********************************************************************/
    public ProgressView (int maxProgressValue) {

	isInitialized = true;

	// set progress bar
	progressBar = new JProgressBar (0, maxProgressValue);
	progressBar.setStringPainted (true);
	progressBar.setAlignmentX (CENTER_ALIGNMENT);

	// set progress label
	progressLabel = new JLabel (msg);
	progressLabel.setAlignmentX (CENTER_ALIGNMENT);

	// put it together
	setLayout (new BoxLayout (this, BoxLayout.Y_AXIS));
	add (progressLabel);
	add (Box.createRigidArea (new Dimension (1,20)));
	add (progressBar);
    }

    /*********************************************************************
     * Create and show the main frame.
     ********************************************************************/
    public void show (String title) {
	if (! isInitialized) return;
        frame = SwingUtils.createMainFrame (this, title);
        frame.setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
        SwingUtils.showMainFrame (frame, frameWidth, frameHeight);

    }

    /*********************************************************************
     * Hide...
     ********************************************************************/
    public void destroy() {
	isInitialized = false;
        if (frame != null) {
	    frame.setCursor (Cursor.getPredefinedCursor (Cursor.DEFAULT_CURSOR));
            frame.dispose();
	}
    }

    /*********************************************************************
     * Set progress properties
     ********************************************************************/
    public void setText (String msg) {
	if (isInitialized)
	    progressLabel.setText (msg);
    }

    public void setValue (int progressValue) {
	if (isInitialized)
	    progressBar.setValue (progressValue);
    }

    public void add() {
	if (isInitialized)
	    progressBar.setValue (progressBar.getValue() + 1);
    }

    public void setTextAndAdd (String msg) {
	if (isInitialized) {
  	    setText (msg);
  	    add();
  	}
    }

    /*********************************************************************
     * Set other properties
     ********************************************************************/
    public void setFrameWidth (int width) {
	if (width > 0) frameWidth = width;
    }

    public void setFrameHeight (int height) {
	if (height > 0) frameHeight = height;
    }

}
