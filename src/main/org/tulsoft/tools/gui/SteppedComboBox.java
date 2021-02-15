// SteppedComboBox.java
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

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.ComboBoxModel;

import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import java.awt.Dimension;
import java.awt.Rectangle;

import java.util.Vector;

/**
 * Control the width of the combo-box popup window. <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>,
 * based on open-source from Nobuo Tamemasa (posted on 7-Jan-1999 to
 * http://www.codeguru.com/).
 * @version $Id: SteppedComboBox.java,v 1.1 2006/02/12 16:30:59 marsenger Exp $
 */

public class SteppedComboBox
    extends JComboBox {

    protected int popupWidth;
  
    /*********************************************************************
     *
     ********************************************************************/
    public SteppedComboBox () {
	super();
	init();
    }

    /*********************************************************************
     *
     ********************************************************************/
    public SteppedComboBox (ComboBoxModel aModel) {
	super (aModel);
	init();
    }

    /*********************************************************************
     *
     ********************************************************************/
    public SteppedComboBox (final Object[] items) {
	super (items);
	init();
    }
  
    /*********************************************************************
     *
     ********************************************************************/
    public SteppedComboBox (Vector items) {
	super (items);
	init();
    }
  
    /*********************************************************************
     * Set the new size for the popup windows of this combo box. <p>
     *
     * @param width new size
     ********************************************************************/
    public void setPopupWidth (int width) {
	popupWidth = width;
    }
    
    /*********************************************************************
     * Get the size of the popup windows of this combo box.
     ********************************************************************/
    public Dimension getPopupSize() {
	Dimension size = getSize();
	if (popupWidth < 1) popupWidth = size.width;
	return new Dimension (popupWidth, size.height);
    }

    /*********************************************************************
     * Called from constructors...
     ********************************************************************/
    protected void init() {
	setUI (new SteppedComboBoxUI());
	popupWidth = 0;
    }

    /*********************************************************************
     *
     *
     *
     ********************************************************************/
    class SteppedComboBoxUI
	extends MetalComboBoxUI {

	protected ComboPopup createPopup() {
	    BasicComboPopup newPopup = new BasicComboPopup (comboBox) {
        
		    public void show() {
			Dimension popupSize = ((SteppedComboBox)comboBox).getPopupSize();
			popupSize.setSize
			    (popupSize.width,
			     getPopupHeightForRowCount (comboBox.getMaximumRowCount()));
			Rectangle popupBounds =
			    computePopupBounds (0,
						comboBox.getBounds().height,
						popupSize.width,
						popupSize.height);
			scroller.setMaximumSize (popupBounds.getSize());
			scroller.setPreferredSize (popupBounds.getSize());
			scroller.setMinimumSize (popupBounds.getSize());
			list.invalidate();
			int selectedIndex = comboBox.getSelectedIndex();
			if (selectedIndex == -1) {
			    list.clearSelection();
			} else {
			    list.setSelectedIndex (selectedIndex);
			}            
			list.ensureIndexIsVisible (list.getSelectedIndex());
			setLightWeightPopupEnabled (comboBox.isLightWeightPopupEnabled());
			
			show (comboBox, popupBounds.x, popupBounds.y);
		    }
		};
	    newPopup.getAccessibleContext().setAccessibleParent (comboBox);
	    return newPopup;
	}
    }

}
