// PrefsUtils.java
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

package org.tulsoft.shared;

import org.tulsoft.shared.UUtils;

import java.util.prefs.Preferences;
import java.util.prefs.BackingStoreException;
import java.util.Arrays;

/**
 * A utility class helping with Java Preferences (see package
 * <tt>java.util.prefs</tt>). <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 */
public abstract class PrefsUtils {

    /** Version and date of the last update. */
    public static final String VERSION = "$Id: PrefsUtils.java,v 1.1 2005/09/18 09:25:41 marsenger Exp $";

    /**************************************************************************
     * Return a Preferences node represented by 'nodeName' in a user
     * preferences tree. If such node does not exist it will be
     * created. <p>
     *
     * @param nodeName defines what node will be returned
     * @return a node specified by the 'nodeName'
     **************************************************************************/
    public static Preferences getNode (String nodeName) {

	Preferences myNode = Preferences.userRoot();
	if (UUtils.notEmpty (nodeName))
	    myNode = myNode.node (nodeName);
	return myNode;
    }

    /**************************************************************************
     * Return a Preferences node represented by name created from the
     * name of a given class and a node name. If such node does not
     * exist it will be created in the user's Preferences tree. <p>
     *
     * @param c is a class whose full name will be used as a beginning
     * of a Preferences node name
     * @param nodeName is a name that will be added to the name
     * created from parameter 'c'
     * @return a node specified by the 'c' and 'nodeName'
     **************************************************************************/
    public static Preferences getNode (Class c, String nodeName) {

	if (UUtils.isEmpty (nodeName))
	    return getNode (nodeNameFromClass (c));
	else if (nodeName.startsWith ("/"))
	    return getNode (nodeNameFromClass (c) + nodeName);
	else
	    return getNode (nodeNameFromClass (c) + "/" + nodeName);
    }

    /**************************************************************************
     * Return a Preferences node represented by name created from the
     * given class. If such node does not exist it will be created in
     * the user's Preferences tree. <p>
     *
     * @param c is a class whose full name will be used as the
     * Preferences node name
     * @return a node specified by the 'c'
     **************************************************************************/
    public static Preferences getNode (Class c) {
	return getNode (nodeNameFromClass (c));
    }

    /**************************************************************************
     * Get all values from a given Preferences node. If such node does
     * not exist it will be created in the user's Preferences
     * tree. <p>
     *
     * This is useful when keys are not that important and all you
     * need is a list of values - they will be returned sorted by
     * their keys (but the keys themselves are not included in the
     * returned value). An example of such situation is a list of
     * "history" values for a JComboBox. <p>
     *
     * @param node whose values are being returned
     * @return all values from the given node sorted by their keys
     **************************************************************************/
    public static String[] getValues (Preferences node) {
	try {
	    String[] keys = node.keys();
	    Arrays.sort (keys);
	    String[] values = new String [keys.length];
	    int j = 0;
	    for (int i = keys.length - 1; i >= 0; i--)
		values[j++] = node.get (keys[i], "");
	    return values;
	} catch (BackingStoreException e) {
	    return new String[] {};
	}
    }

    /**************************************************************************
     * Remove all keys (and their values) from the given Preferences
     * node. <p>
     *
     * @param node who will be cleaned-up
     **************************************************************************/
    public static void removeKeys (Preferences node) {
	try {
	    node.clear();
// 	    String[] keys = node.keys();
// 	    for (int i = 0; i < keys.length; i++)
// 		node.remove (keys[i]);
	    node.flush();
	} catch (BackingStoreException e) {
	}
    }

    /**************************************************************************
     * Returns the absolute path name of the node corresponding to the
     * full class name of 'c'. This is a version of the nodeName()
     * from java.util.prefs.Preferences that keeps also the class name
     * (and not only its package name).
     **************************************************************************/
    private static String nodeNameFromClass (Class c) {
        String className = c.getName();
        return "/" + className.replace ('.', '/');
    }

    /**************************************************************************
     * Add 'key' and 'value' pair to the given node but if the 'value'
     * already exists in the node, update just its key. <p>
     *
     * @param node who will updated
     **************************************************************************/
    public static void updateKey (Preferences node, String key, String value) {
	try {
	    String[] keys = node.keys();
	    for (int i = 0; i < keys.length; i++) {
		if (value.equals (node.get (keys[i], ""))) {
		    node.remove (keys[i]);
		}
	    }
	    node.put (key, value);
	    node.sync();
	} catch (BackingStoreException e) {
	}
    }

}
