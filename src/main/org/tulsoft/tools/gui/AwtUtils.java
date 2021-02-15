// AwtUtils.java
//
// Created: April 1999
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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.Frame;
import java.awt.Component;
import java.awt.Window;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.applet.Applet;


/**
 * A utility class helping to write Awt-based (or Awt-related-based)
 * GUIs. <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: AwtUtils.java,v 1.1 2005/09/13 07:31:44 marsenger Exp $
 */

public abstract class AwtUtils {

    /** Version and date of the last update. */
    public static final String VERSION = "$Id: AwtUtils.java,v 1.1 2005/09/13 07:31:44 marsenger Exp $";

    /*********************************************************************
     * Return a window events listener listening to the closing event
     * and calling <tt>exit(0)</tt> when a window with this listener
     * is being closed. <p>
     *
     * @return a window closing event listener
     * @see #setSoftWindowClosing
     ********************************************************************/
    static public WindowListener setWindowClosing() {
	WindowListener l = new WindowAdapter() {
	    public void windowClosing (WindowEvent e) {
                System.exit (0);
	    }
	};
        return l;
    }

    /*********************************************************************
     * Return a window events listener listening to the closing event
     * and calling <tt>dispose()</tt> when a window with this listener
     * is being closed. <p>
     *
     * @return a window closing event listener
     * @see #setWindowClosing
     ********************************************************************/
    static public WindowListener setSoftWindowClosing() {
	WindowListener l = new WindowAdapter() {
	    public void windowClosing (WindowEvent e) {
                ((Frame)e.getSource()).dispose();
	    }
	};
        return l;
    }

    /*********************************************************************
     * Set a location of a given component relatively to another
     * component.<P>
     *
     * This is a slightly modified version of code from Swing (JDialog
     * class). <p>
     *
     * @param newC a component to be put on the screen
     * @param c a component in relation to which the "newC" is determined
     ********************************************************************/
    static public void setLocationRelativeTo (Component newC, Component c) {
        Container root = null;

        if (c != null) {
            if (c instanceof Window || c instanceof Applet) {
               root = (Container)c;
            } else {
                Container parent;
                for (parent = c.getParent() ; parent != null ; parent = parent.getParent()) {
                    if (parent instanceof Window || parent instanceof Applet) {
                        root = parent;
                        break;
                    }
                }
            }
        }

        if ((c != null && !c.isShowing()) || root == null || !root.isShowing()) {
            Dimension         paneSize = newC.getSize();
            Dimension         screenSize = newC.getToolkit().getScreenSize();

            newC.setLocation ((screenSize.width - paneSize.width) / 2,
                              (screenSize.height - paneSize.height) / 2);
        } else {
            Dimension           invokerSize = c.getSize();
            Point               invokerScreenLocation = c.getLocationOnScreen();
            Rectangle           dialogBounds = newC.getBounds();
            int                 dx = invokerScreenLocation.x+((invokerSize.width-dialogBounds.width)>>1);
            int                 dy = invokerScreenLocation.y+((invokerSize.height - dialogBounds.height)>>1);
            Dimension           ss = newC.getToolkit().getScreenSize();

            if (dy+dialogBounds.height>ss.height) {
                dy = ss.height-dialogBounds.height;
                dx = invokerScreenLocation.x<(ss.width>>1) ? invokerScreenLocation.x+invokerSize.width :
                    invokerScreenLocation.x-dialogBounds.width;
            }
            if (dx+dialogBounds.width>ss.width) dx = ss.width-dialogBounds.width;
            if (dx<0) dx = 0;
            if (dy<0) dy = 0;
            newC.setLocation(dx, dy);
        }
    }

    /*********************************************************************
     * Return a top-level frame of the given component. <p>
     *
     * @param component whose top-level frame is looked for
     * @return a top-level frame or null if it not possible to find it
     ********************************************************************/
    static public Frame getTopLevelParent (Component component) {
        Component c = component;
        while (c.getParent() != null)
            c = c.getParent();
        if (c instanceof Frame)
            return (Frame)c;
        else
            return null;
    }

    /*********************************************************************
     * Redisplay the given component (which includes invalidation,
     * validation and repainting). <p>
     *
     * @param comp a component to be redisplayed
     ********************************************************************/
    static public void redisplay (Component comp) {
        if (comp != null) {
            comp.invalidate();
            comp.validate();
            comp.repaint();
	}
    }

    /*********************************************************************
     * Update properties weight[x,y] of those components of the given
     * container which are at either the most right position or the
     * most bottom position. <p>
     *
     * This may be useful if you want to make a container to expand or
     * to shrink when its parent is resized. Also when your container
     * is filled with components dynamically - depending on other
     * properties, this method can be called once a container is
     * ready, and you do not need to think about the weights (in other
     * words, during building the container you do not need to know
     * which components become the "marginal" components). <p>
     * 
     * The following rules apply:
     * <UL>
     *  <LI> Changes will be made only if the container has the
     *       GridBagLayout layout.
     *  <LI> If there is at least one component with a non-zero weightx
     *       then weightx of no component is going to be changed.
     *  <LI> Similarly for columns and weighty.
     *  <LI> The rightmost components get assigned weightx = 1.0 and
     *       the bottom components get assigned weighty = 1.0.
     *       They also get assigned gridwidth and/or gridheight to the
     *       value GridBagConstraints.REMAINDER.
     * </UL>
     * <P>
     * <h4>Bug, to-be-done:</h4>
     *    All components must have assigned gridx and gridy explicitly.<BR>
     *    It does not work with value GridBagConstraints.RELATIVE.
     * <P>
     * @param container whose components are checked
     ********************************************************************/
    static public void updateComponentsOnEdges (Container container) {
//    	System.out.println ("\n[-- new --]");

	// this makes sense only for GridBagLayout
	if (! (container.getLayout() instanceof GridBagLayout)) return;
	GridBagLayout gridbag = (GridBagLayout)container.getLayout();

	// find which components may need to change their weight[x,y];
	// the rule: if there is at least one non-zero weightx then
	// do not add any weights (and similarly for weighty)
	GridBagConstraints gbc;
	boolean weightxFound = false;
	boolean weightyFound = false;
	int maxGridX = -1;
	int maxGridY = -1;
	Component[] comps = container.getComponents();

	// find the number of columns and rows in our layout;
	// find if there are some already assigned weight[x,y]
	for (int i = 0; i < comps.length; i++) {
            gbc = gridbag.getConstraints (comps[i]);
	    if (gbc.gridx == GridBagConstraints.RELATIVE ||
		gbc.gridy == GridBagConstraints.RELATIVE) return; // TBD better
	    if (gbc.gridx > maxGridX) maxGridX = gbc.gridx;
	    if (gbc.gridy > maxGridY) maxGridY = gbc.gridy;
            if (gbc.weightx > 0) weightxFound = true;
            if (gbc.weighty > 0) weightyFound = true;
	}
	if (maxGridX == -1) return;   // an empty container is not interesting

	// make a matrix from the components
	// (some components may appear in more cells if their grid[width,height] is > 1)
	int maxJ, maxK;
	Component[][] matrix = new Component [maxGridX+1][maxGridY+1];
	for (int i = 0; i < comps.length; i++) {
            gbc = gridbag.getConstraints (comps[i]);
	    maxJ = Math.min (gbc.gridx + gbc.gridwidth, maxGridX+1);
            for (int j = gbc.gridx;
		 j < (gbc.gridwidth == GridBagConstraints.REMAINDER ? maxGridX+1 : maxJ);
		 j++) {
		maxK = Math.min (gbc.gridy + gbc.gridheight, maxGridY+1);
//  		System.out.println ("maxJ: " + maxJ + ", maxK: " + maxK);
		for (int k = gbc.gridy;
		     k < (gbc.gridheight == GridBagConstraints.REMAINDER ? maxGridY+1 : maxK);
		     k++)
		    matrix [j][k] = comps[i];
	    }
	}

	// change weight[x,y] of the components which are on the edges
    	Component comp;
	if (! weightyFound)
	    for (int i = 0; i <= maxGridX; i++)
		for (int j = maxGridY; j >= 0; j--) {
		    comp = matrix[i][j];
		    if (comp != null) {
			//  		    System.out.println ("bottom: " + comp.getClass().getName());
			gbc = gridbag.getConstraints (comp);
			gbc.weighty = 1.0;
			gbc.gridheight = GridBagConstraints.REMAINDER;
			gridbag.setConstraints (comp, gbc);
			break;
		    }
		}

	if (! weightxFound)
	    for (int i = 0; i <= maxGridY; i++)
		for (int j = maxGridX; j >= 0; j--) {
		    comp = matrix[j][i];
		    if (comp != null) {
			//  		    System.out.println ("right: " + comp.getClass().getName());
			gbc = gridbag.getConstraints (comp);
			gbc.weightx = 1.0;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gridbag.setConstraints (comp, gbc);
			break;
		    }
		}
    }

}
