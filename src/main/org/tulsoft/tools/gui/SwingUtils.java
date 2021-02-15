// SwingUtils.java
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

import org.tulsoft.shared.StringUtils;
import org.tulsoft.tools.gui.AwtUtils;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.JButton;
// import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.util.Enumeration;

/**
 * A utility class helping to write Swing-based GUIs. <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: SwingUtils.java,v 1.6 2007/03/25 18:55:18 marsenger Exp $
 */

public abstract class SwingUtils {

    /** Version and date of the last update. */
    public static final String VERSION = "$Id: SwingUtils.java,v 1.6 2007/03/25 18:55:18 marsenger Exp $";

    /*********************************************************************
     * Check Java version. <p>
     *
     * @return true if the version of Java virtual machine is good
     *         enough for using Swing
     ********************************************************************/
    static public boolean checkJavaVersion() {
        String vers = System.getProperty ("java.version");
        if (vers.compareTo ("1.1.2") < 0) {
            System.err.println ("!!!WARNING: Swing must be run with a " +
                                "1.1.2 or higher version VM!!!");
            return false;
        } else {
            return true;
	}
    }

    /*********************************************************************
     * Display a plain message (in a separate window). <p>
     *
     * @param parent a parent component where to display the message
     * @param title a message window title
     * @param msg a message to be displayed
     * @param icon an icon to be displayed together with the message
     *             (if it is null no icon is displayed)
     ********************************************************************/
    static public void msg (Component parent, String title, Object msg,
                            Icon icon) {
        if (msg instanceof String)
            msg = StringUtils.customizeMsg ((String)msg, 25);
        if (icon == null)
            JOptionPane.showMessageDialog (parent, msg, title,
                                           JOptionPane.PLAIN_MESSAGE);
        else 
            JOptionPane.showMessageDialog (parent, msg, title,
                                           JOptionPane.PLAIN_MESSAGE, icon);
    }

    /*********************************************************************
     * Display a confirmation box and return true if confirmation
     * dialog passed. <p>
     *
     * @param parent a parent component where to display the message
     * @param msg a message to be displayed
     * @param icon an icon to be displayed together with the message
     *             (if it is null no icon is displayed)
     ********************************************************************/
    static public boolean confirm (Component parent, Object msg, Icon icon) {
        int result;
        if (icon == null)
	    result = JOptionPane.showConfirmDialog (parent, msg, "Confirmation dialog",
                                                    JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.QUESTION_MESSAGE);
        else
	    result = JOptionPane.showConfirmDialog (parent, msg, "Confirmation dialog",
                                                    JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.QUESTION_MESSAGE, icon);
        return (result == JOptionPane.YES_OPTION);
    }

    /*********************************************************************
     * Create a frame listening to a window closing events and with
     * the given panel component. It calls <tt>exit(0)</tt> when the
     * window frame is being closed. <p>
     *
     * @param component a main component creating the result frame
     * @param title window title
     * @return a new frame
     * @see AwtUtils#setWindowClosing
     ********************************************************************/
    static public JFrame createMainFrame (JComponent component,
					  String title) {
        JFrame frame = _createMainFrame (component, title);
	frame.addWindowListener (AwtUtils.setWindowClosing());
        return frame;
    }

    /*********************************************************************
     * Create a frame listening to a window closing events and with
     * the given panel component. It calls <tt>dispose()</tt> when the
     * window frame is being closed. <p>
     *
     * @param component a main component creating the result frame
     * @param title window title
     * @return a new frame
     ********************************************************************/
    static public JFrame createSoftMainFrame (JComponent component,
					      String title) {
        JFrame frame = _createMainFrame (component, title);
	frame.addWindowListener (AwtUtils.setSoftWindowClosing());
        return frame;
    }

    //
    static JFrame _createMainFrame (JComponent component, String title) {
  	JFrame frame = new JFrame (title);
	JOptionPane.setRootFrame (frame);
	frame.getContentPane().setLayout (new BorderLayout());
	frame.getContentPane().add (component, BorderLayout.CENTER);
        return frame;
    }

    /*********************************************************************
     * Display a given frame using the given size. <p>
     *
     * @param frame a frame to be shown
     * @param dim how big should the frame be
     ********************************************************************/
    static public void showMainFrame (JFrame frame, Dimension dim) {
        showMainFrameRelativeTo (null, frame, dim.width, dim.height);
    }

    /*********************************************************************
     * Display a given frame using the given size. <p>
     *
     * @param frame a frame to be shown
     * @param width how wide the frame should be
     * @param height how high the frame should be
     ********************************************************************/
    static public void showMainFrame (JFrame frame, int width, int height) {
        showMainFrameRelativeTo (null, frame, width, height);
    }

    /*********************************************************************
     * Display a given frame using the given size, show it close to a
     * given component. <p>
     *
     * @param frame a frame to be shown
     * @param width how wide the frame should be
     * @param height how high the frame should be
     * @param parent a component where the frame is displayed close to
     ********************************************************************/
    static public void showMainFrameRelativeTo (Component parent, JFrame frame,
						int width, int height) {
	frame.setSize (width, height);
        AwtUtils.setLocationRelativeTo (frame, parent);

	frame.show();
	frame.validate();
	frame.repaint();
    }

    /******************************************************************************
     * Add a component to the <tt>parent</tt> using <tt>GridBagLayout</tt>
     * and various paramaters. <p>
     ******************************************************************************/
    public static void addComponent (JComponent parent, Component component,
                                     int gridx, int gridy,
				     int gridwidth, int gridheight,
                                     int fill,
				     int anchor) {
        GridBagConstraints gbc = createConstraints (gridx, gridy, gridwidth, gridheight, fill, anchor);
        parent.add (component, gbc);
      }

    /******************************************************************************
     * Add a component to the <tt>parent</tt> using <tt>GridBagLayout</tt>
     * and various paramaters.
     ******************************************************************************/
    public static void addComponent (JComponent parent, Component component,
                                     int gridx, int gridy,
				     int gridwidth, int gridheight,
                                     int fill,
				     int anchor,
				     Insets insets) {
        GridBagConstraints gbc = createConstraints (gridx, gridy, gridwidth, gridheight, fill, anchor);
        gbc.insets = insets;
        parent.add (component, gbc);
    }

    /******************************************************************************
     * Add a component to the <tt>parent</tt> using <tt>GridBagLayout</tt>
     * and various paramaters. <p>
     ******************************************************************************/
    public static void addComponent (JComponent parent, Component component,
                                     int gridx, int gridy,
				     int gridwidth, int gridheight,
                                     int fill,
				     int anchor,
				     double weightx, double weighty) {
        GridBagConstraints gbc = createConstraints (gridx, gridy, gridwidth, gridheight, fill, anchor);
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        parent.add (component, gbc);
    }

    /******************************************************************************
     * Add a component to the <tt>parent</tt> using <tt>GridBagLayout</tt>
     * and various paramaters. <p>
     ******************************************************************************/
    public static void addComponent (JComponent parent, Component component,
                                     int gridx, int gridy,
				     int gridwidth, int gridheight,
                                     int fill,
				     int anchor,
				     double weightx, double weighty,
				     Insets insets) {
        GridBagConstraints gbc = createConstraints (gridx, gridy, gridwidth, gridheight, fill, anchor);
        gbc.weightx = weightx;
        gbc.weighty = weighty;
	gbc.insets = insets;
        parent.add (component, gbc);
    }

    //
    static GridBagConstraints createConstraints (int gridx, int gridy,
                                                 int gridwidth, int gridheight,
                                                 int fill, int anchor) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        gbc.gridheight = gridheight;
        gbc.fill = fill;
        gbc.anchor = anchor;
        return gbc;
    }

    /******************************************************************************
     * Returns an Icon, or null if the path was invalid.
     ******************************************************************************/
    public static Icon createIcon (String path) {
	return createIcon (path, null);
    }
    public static Icon createIcon (String path, Object owner) {
	return createIcon (path, owner.getClass());
    }
    public static Icon createIcon (String path, Class c) {

	// path can be empty
	if (path == null)
	    return null;

	// path can be absolute...
	File iconFile = new File (path);
	if (iconFile.isAbsolute())
	    return new ImageIcon (path);

	// ...or it can be a valid URL
	try {
	    return new ImageIcon (new URL (path));
	} catch (MalformedURLException e) {
	}

	// ...or consider it a resource and load it as a resource of
	// the given class
	URL imageURL = null;
	if (c != null) {
	    imageURL = c.getClassLoader().getResource (path);
	    if (imageURL != null)
		return new ImageIcon (imageURL);

	    // ...or extend the path by the package name of the given
	    // class
	    String className = c.getName();
	    int pkgEndIndex = className.lastIndexOf ('.');
	    if (pkgEndIndex > 0) {
		String packageName = className.substring (0, pkgEndIndex);
		String newPath = packageName.replace ('.', '/') + "/" + path;
		imageURL = c.getClassLoader().getResource (newPath);
		if (imageURL != null)
		    return new ImageIcon (imageURL);
	    }
	}

	// ...or (finally) try some general class loader
	imageURL = Thread.currentThread().getContextClassLoader().getResource (path);
	if (imageURL != null)
	    return new ImageIcon (imageURL);

	// sorry, I cannot do more
	return null;
    }

    /*********************************************************************
     * Make an icon-only button smaller (just around its icon). Do
     * nothing if the button does not have any icon, or if it has also
     * a non-empty label. <p>
     *
     * @param button whose left anf right margins will be changed to
     * its top margin
     ********************************************************************/
    public static void compact (JButton button) {
	if ( button.getIcon() != null && "".equals (button.getText()) ) {
	    Insets margin = button.getMargin();
	    margin.left = margin.right = margin.top;
	    button.setMargin (margin);
	}
    }

    /*********************************************************************
     * Expand all nodes in the given 'tree', starting from the given
     * 'fromNode'. <p>
     *
     * @param tree a component to be expanded
     * @param fromNode is a starting node
     ********************************************************************/
    public static void expandTree (JTree tree, DefaultMutableTreeNode fromNode) {
        Enumeration en = fromNode.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)en.nextElement();
            tree.expandPath (new TreePath (node.getPath()));
	}
    }

    /*********************************************************************
     * Collapse all nodes in the given 'tree', starting from the given
     * 'fromNode'. <p>
     *
     * @param tree a component to be collapsed
     * @param fromNode is a starting node
     ********************************************************************/
    public static void collapseTree (JTree tree, DefaultMutableTreeNode fromNode) {
        Enumeration en = fromNode.children();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)en.nextElement();
            tree.collapsePath (new TreePath (node.getPath()));
	}
    }

//     // If expand is true, expands all nodes in the tree.
//     // Otherwise, collapses all nodes in the tree.
//     public void expandAll(JTree tree, boolean expand) {
//         TreeNode root = (TreeNode)tree.getModel().getRoot();
    
//         // Traverse tree from root
//         expandAll(tree, new TreePath(root), expand);
//     }
//     private void expandAll(JTree tree, TreePath parent, boolean expand) {
//         // Traverse children
//         TreeNode node = (TreeNode)parent.getLastPathComponent();
//         if (node.getChildCount() >= 0) {
//             for (Enumeration e=node.children(); e.hasMoreElements(); ) {
//                 TreeNode n = (TreeNode)e.nextElement();
//                 TreePath path = parent.pathByAddingChild(n);
//                 expandAll(tree, path, expand);
//             }
//         }
    
//         // Expansion or collapse must be done bottom-up
//         if (expand) {
//             tree.expandPath(parent);
//         } else {
//             tree.collapsePath(parent);
//         }
//     }


//     // If expand is true, expands all nodes in the tree.
//     // Otherwise, collapses all nodes in the tree.
//     public static void expandAll(JTree tree, boolean expand) {
//         TreeNode root = (TreeNode)tree.getModel().getRoot();
// 	// Traverse tree from root
//         expandAll(tree, new TreePath(root), expand);
//     }

//     private static void expandAll (JTree tree, TreePath parent, boolean expand) {
//         // Traverse children
//         TreeNode node = (TreeNode)parent.getLastPathComponent();
//         if (node.getChildCount() >= 0) {
//             for (Enumeration e=node.children(); e.hasMoreElements(); ) {
//                 TreeNode n = (TreeNode)e.nextElement();
//                 TreePath path = parent.pathByAddingChild(n);
//                 expandAll(tree, path, expand);
//             }
//         }
// 	// Expansion or collapse must be done bottom-up
//         if (expand) {
//             tree.expandPath(parent);
//         } else {
//             tree.collapsePath(parent);
//         }
//     }

}
