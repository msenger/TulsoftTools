// SoapUtils.java
//
// Created: June 2002
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

package org.tulsoft.tools.soap;

import org.tulsoft.tools.loaders.ICreator;
import org.tulsoft.shared.GException;

import java.util.Vector;

/**
 * Several static utilities...
 *<P>
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: SoapUtils.java,v 1.3 2007/03/25 18:55:19 marsenger Exp $
 */

public abstract class SoapUtils {

    final static String DEFAULT_TOOLKIT_CLASS  ="org.tulsoft.tools.soap.axis.SOAPToolkitAxis";
    final static String PROP_TOOLKIT_CLASS = "toolkit_class";

    /**************************************************************************
     * Create an argument list with all attributes residing in the
     * given SOAP toolkit (the attributes comes, usually, from a
     * deployment descriptor).
     *************************************************************************/
    public static String[] createArgsList (SOAPToolkit toolkit) {
	Vector<String> v = new Vector<String>();
	String[] names = toolkit.getAttributeNames();
	for (int i = 0; i < names.length; i++) {
	    String value = toolkit.getAttribute (names[i]);
	    if (value != null) {    // should happen always :-)
		v.addElement (names[i]);
		v.addElement (value);
	    }
	}
	String[] args = new String [v.size()];
	v.copyInto (args);
	return args;
    }

    /**************************************************************************
     * Load a class implementing SOAPToolkit interface, and return an
     * instance of it.
     *************************************************************************/
    public static SOAPToolkit loadSOAPToolkit()
	throws GException {

	// before doing anything we need some configuration parameters;
	// to get this parameters we need access to the surrounding toolkit;
	// to get this access we need to decide what class represents it -
	// for that we need a configuration parameter - Catch 22 situation...
	// therefore...
	// ...try first a System property PROP_TOOLKIT_CLASS (which is hard to set
	//    because it has to be set before starting the whole 'tomcat' or
	//    whatever engine is serving this)
	String toolkitClassName = System.getProperty (PROP_TOOLKIT_CLASS);

	// ...TBD: then try to look what we have on the classpath (obviously
	//    the toolkit should be there)

	// ...and finally take a default name (so the easiest way to change
	//    it is to change the default name in this file and recompile)
	if (toolkitClassName == null)
	    toolkitClassName = DEFAULT_TOOLKIT_CLASS;

	// now load the toolkit wrapper
	return (SOAPToolkit)ICreator.createInstance (toolkitClassName);
    }

}
