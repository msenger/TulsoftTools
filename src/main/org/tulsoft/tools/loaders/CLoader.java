// CLoader.java
//
// Created: November 1999
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

package org.tulsoft.tools.loaders;

import org.tulsoft.shared.UUtils;
import java.util.*;
import java.io.*;

/** A Class loader.
 *  It reads the bytecodes representing the individual
 *  applications and install them as usable classes.
 *  <P>
 *
 * This is an example how to use this class to create an instance of
 * a given class:
 * <PRE>
 *	try {
 *	    CLoader loader = new CLoader();
 *	    loader.setDebug (true);
 *	    Class classOf = loader.loadClass ("embl.ebi.mypackage.MyClass", true);
 *	    MyClass newObject = (MyClass)classOf.newInstance();
 *	} catch (Exception e) {
 *           System.err.println ("...");
 *      }
 * </PRE>
 *
 *  <H5>Bugs, to-be-done, developer comments</H5>
 *  <UL>
 *    <LI> It would be nice to be able to specify a class name
 *         by using a URL - and load a remote class. Maybe later...
 *  </UL>
 *
 *  <H5>Last changes (in backwards order)</H5>
 *  <UL>
 *    <LI> {@link #DYNACLASSPATH} property added.
 *    <LI> There was a bug reporting 'Duplicate name' when
 *         a path was added to the class name (a case when the
 *         class is not located on the CLASSPATH, but o a path
 *         given in the CLoader constructor)
 *  </UL>
 *  <P>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: CLoader.java,v 1.2 2007/03/25 18:55:18 marsenger Exp $
 */
public class CLoader extends ClassLoader {

    private Hashtable<String,Class> Classes =
	new Hashtable<String,Class>();   // already loaded classes
    private boolean debug = false;                 // debug mode
    private String path = "";                      // path to the class files

    /** A System property name ("<b>DYNACLASSPATH</b>") containing path from where
     *  to load classes. It is used only if {@link #loadClass(String,boolean) loadClass}
     *  could not find the class otherwise, and if there ws no path given in the
     *  constructor.
     */
    public static final String DYNACLASSPATH = "DYNACLASSPATH";    // place of dynamically loaded classes

    /****************************************************************************
     * A default constructor.
     ****************************************************************************/
    public CLoader() {}

    /****************************************************************************
     * A constructor specifying a path to be used to locate all class files.
     * The path is used only if the parameter <tt>name</tt> in method
     * {@link #loadClass(String,boolean) loadClass} was not sufficient
     * (e.g. the given class was not found using CLASSPATH environment variable).<P>
     *
     * @param path where to look for files being loaded
     ****************************************************************************/
    public CLoader (String path) {
        this.path = path;
    }

    /****************************************************************************
     * Set or unset debug mode.
     * When in debug mode some methods print messages on the standard output.<P>
     *
     * @param toggle a new status of debug mode
     ****************************************************************************/
    public void setDebug (boolean toggle) {
	debug = toggle;
    }

    /****************************************************************************
     *   Load the class of the given name. <P>
     *
     *   The most of this code was taken from <em>The Java Programming Language</em>
     *   by K.Arnold and J.Gosling.<P>
     *
     *   Note that <tt>name</tt> should be an expected name of the class, using '.'
     *   and not '/' as the separator, and without a trailing ".class" suffix.
     *   If a class of this name is not found in directories specified in the
     *   CLASSPATH, then a copy of 'name' is "fixed" to become a real file name
     *   containing class bytecodes.<P>
     *
     * @param name class name to be loaded
     * @param resolve if true, the method invokes
     *   the <tt>java.lang.ClassLoader.resolveClass()</tt> method to ensure that 
     *   all classes referred to by the class are also loaded
     * @return a newly loaded class
     * @throws java.lang.ClassNotFoundException when the file with class bytecodes
     *         was not found, or is not readable, or has a wrong contents
     ****************************************************************************/
    public Class loadClass (String name, boolean resolve)
	throws ClassNotFoundException {

	try {
	    Class newClass = Classes.get (name);
	    if (newClass == null) {   // not yet defined
	        try {                 // check if system class
	            newClass = findSystemClass (name);
	            if (newClass != null) {
		        if (debug) System.out.println ("Found " + name);
			return newClass;
		    }
		} catch (ClassNotFoundException e) {
		    // keep on looking
		} catch (NoClassDefFoundError e) {
		    // keep on looking
		}

		// class not found (means: it is not on the defined CLASSPATH) -- need to load it
	        byte[] buf = bytesForClass (name);
                try {
	            newClass = defineClass (name, buf, 0, buf.length);
		} catch (Error e2) {
		    // keep on trying: now without specifying a name
		    newClass = defineClass (null, buf, 0, buf.length);
		}
	        Classes.put (name, newClass);
	    }
	    if (resolve)
	        resolveClass (newClass);
	    return newClass;
        } catch (IOException e) {
  	    throw new ClassNotFoundException (e.getMessage());
	} catch (Error e) {  // usually: 'wrong name'
            throw new ClassNotFoundException (e.getMessage());
	}
    }

    /****************************************************************************
     *   Read and return bytecodes for the class of the given name.<P>
     *
     * @param name represents a file name with class bytecodes
     * @return class bytecodes
     * @throw java.io.IOException, java.lang.ClassNotFoundException
     ****************************************************************************/
    protected byte[] bytesForClass (String name)
	throws IOException, ClassNotFoundException {

	FileInputStream in = streamFor (name);
	int length = in.available();   // get byte count
	if (length == 0)
	    throw new ClassNotFoundException (name);
	byte[] buf = new byte[length];
	in.read (buf);    // read the bytes
	if (debug)
	    System.out.println ("Loaded " + name + " (length: " + length + ")");
	return buf;

//  	try {
//  	    byte[] buf = bytesOf (name);
//  	    if (buf == null || buf.length == 0)
//  		throw new ClassNotFoundException (name);
//  	    if (debug)
//  		System.out.println ("Loaded " + name + " (length: " + buf.length + ")");
//  	    return buf;
//  	} catch (GException e) {
//  	    throw new ClassNotFoundException (name);
//  	}
    }

//      /****************************************************************************
//       *   Return a FileInputStream attached to a file with the class of the given
//       *   name.
//       ****************************************************************************/
//      protected byte[] bytesOf (String name)
//  	throws IOException, GException {

//          String fileSep = System.getProperty ("file.separator");
//          if (!name.endsWith (".class"))
//              name = name.replace ('.', fileSep.charAt (0)) + ".class";

//  	// if file not found try to resolve path using 'path' given in constructor
//          if (!(new File (name)).exists()) {
//              if (name.startsWith (fileSep))
//  		return FileUtils.getBinaryFile (name);

//              if (UUtils.isEmpty (path))
//  		path = System.getProperty (DYNACLASSPATH);

//              if (!UUtils.isEmpty (path)) {
//                  if (path.endsWith (fileSep))
//  		    name = path + name;
//  		else if (path.endsWith (".jar")) {
//  		    if (debug)
//  			System.out.println ("Loading class file: " + name + " from " + path);
//  		    return FileUtils.getJarEntry (new File (path), name);
//                  } else
//  		    name = path + fileSep + name;
//  	    }
//  	}
//      if (debug) System.out.println ("Loading class file: " + name);
//  	return FileUtils.getBinaryFile (name);
//      }

    /****************************************************************************
     *   Return a FileInputStream attached to a file with the class of the given
     *   name.
     ****************************************************************************/
    protected FileInputStream streamFor (String name)
	throws IOException {

        String fileSep = System.getProperty ("file.separator");
        if (!name.endsWith (".class"))
            name = name.replace ('.', fileSep.charAt (0)) + ".class";

	// if file not found try to resolve path using 'path' given in constructor
        if (!(new File (name)).exists()) {
            if (UUtils.isEmpty (name) ||
                name.startsWith (fileSep)) return new FileInputStream (name);

            if (UUtils.isEmpty (path))
		path = System.getProperty (DYNACLASSPATH);

            if (!UUtils.isEmpty (path)) {
                if (path.endsWith (fileSep)) name = path + name;
                else 		             name = path + fileSep + name;
	    }
	}
        if (debug) System.out.println ("Loading class file: " + name);
	return new FileInputStream (name);
    }

}
