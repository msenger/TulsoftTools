// BaseCmdLine.java
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

package org.tulsoft.tools;

import java.util.Vector;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A class that takes all command-line arguments and returns them on
 * demand by names. Each argument name can be associated with one, or
 * more subsequent parameters (called its values). <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: BaseCmdLine.java,v 1.5 2007/03/25 18:55:17 marsenger Exp $
 */

public class BaseCmdLine {

    // (already asked for) parameters and their values
    protected Hashtable<String,Object> knownParams;

    // how to compare argument names
    protected boolean ignoreCase = false;

    /** A copy of argumentss that haven't been asked for yet. */
    public String params[];

    /** A name of a system property. See {@link #BaseCmdLine(String[])} for details. */
    public static final String DO_NOT_ARGS = "donotargs";

    /** A name of a system property. See {@link #BaseCmdLine(String[])} for details. */
    public static final String KEEP_QUOTES = "keepquotes";

    /**
     * It stores command-line arguments internally. Later the arguments can be
     * asked for by name. There is no checking for valid names, or for syntax of
     * the arguments. <p>
     *
     * An example: Let's have a command-line
     * <PRE>
     *   -name Katrin Sengerova -age 7 -v bar -foo
     * </PRE>
     * A method call <TT>getParam ("-name", 2)<TT> returns </TT>{"Katrin", "Sengerova"}</TT>.
     * Then <TT>getParam ("-age")</TT> returns "7". After these two calls, public
     * member <TT>params</TT> contains <TT>{ "-v", "bar", "-foo" }</TT>. It does not prevent
     * to call later the same methods, with the same parameters, and get
     * the same result - now without any further changes in field <TT>params</TT>. <p>
     *
     * If a system property {@link #DO_NOT_ARGS} does not exist (which
     * is a usual case) then there is a special treatments for an
     * argument named <em>-argsfile</em>. If it exists it should have
     * a value which is a filename where additional command-line
     * arguments are read from. They are included in the place where
     * the the <em>-argsfile</em>) was found. More <em>-argsfile</em>
     * is allowed.<p>
     *
     * The file format of such file is: <ul>
     *
     * <li> Blank lines and lines started by # are ignored.
     * <li> Each argument and its (possible) value are on a separate line.
     * <li> Each line will be first trimmed of whitespaces.
     * <li> Then each line will be trimmed of quotes, if it starts and
     * ends with the same kind of quotes.
     *
     * </ul> <p>
     *
     * And there is one more peculiarity: arguments containing only
     * two double quotes are replaced with an empty arguments - unless
     * a system property {@link #KEEP_QUOTES} is set. This is because
     * of the Windows command processor that lets disappear empty
     * arguments but is willing to keep the arguments containing only
     * two double quotes. So this is the way how to simulate an empty
     * argument under Windows. This is particularly useful when the
     * program using BaseCmdLine is invoked from an Ant where the
     * command-line was created from Ant's properties, and some of
     * them may be empty - so their default value was set to two
     * double quotes. <p>
     *
     * @param argv array of strings (usually coming from argument of
     * <tt>main()</tt> method)
     */  
    public BaseCmdLine (String[] argv) {

	if (System.getProperty (DO_NOT_ARGS) == null) {

	    // read additional parameters from a file?
	    Vector<String> v = new Vector<String>();
            for (int i = 0; i < argv.length; i++) {
                if (argv[i].equals ("-argsfile")) {
		    // name found, extract its value
                    if (i < argv.length - 1) {
			i++;
			String argsfile = argv[i];
			BufferedReader data = null;
			try {
			    data = new BufferedReader
				(new InputStreamReader (new FileInputStream (argsfile)));
			    String line;
			    while ((line = data.readLine()) != null) {
				String contents = line.trim();
				if (contents.startsWith ("#")) continue;
				if ("".equals (contents))      continue;
				if ( contents.startsWith ("\"") &&  contents.endsWith ("\"") ||
				     contents.startsWith ("'")  &&  contents.endsWith ("'") )
				    contents = contents.substring (1, contents.length() - 1);
				v.addElement (contents);
			    }
			} catch (IOException e) {
			    System.err.println ("Problems with file '" + argsfile + "': " + e.toString());
			} finally {
			    try {
				if (data != null)
				    data.close();
			    } catch (IOException e) {
			    }
			}
		    }
		} else {
		    v.addElement (argv[i]);
		}
	    }
	    params = new String [v.size()];
	    v.copyInto (params);

	} else {

	    // argument files are not considered
	    params = new String [argv.length];
	    System.arraycopy (argv, 0, params, 0, argv.length);
	}

	if (System.getProperty (KEEP_QUOTES) == null)
            for (int i = 0; i < params.length; i++)
		if ("\"\"".equals (params[i]))
		    params[i] = "";

        knownParams = new Hashtable<String,Object>();
    }

    /**
     * The same as {@link BaseCmdLine (String[])} but it also makes sure
     * that all arguments specified in <tt>names</tt> and having number of values specified
     * in <tt>lengths</tt> are removed from the argument list. <P>
     *
     * Note that "removing" does not mean that the arguments are lost - they are still
     * normally accessible by <tt>get...</tt> and <tt>has...</tt> methods.<P>
     *
     * This is a convenient way how to remove all known arguments from the list
     * without explicit calls of <tt>getParam()</tt> or/and <tt>hasOption()</tt> methods.
     * <P>
     * An example:  Let's have a command-line
     * <PRE>
     *   -name Katrin Sengerova -age 7 -v bar -foo
     * </PRE>
     * To make sure that no other argument appears on the command-line, we can do:
     * <PRE>
     *   public static void main (String[] args) {
     *     BaseCmdLine cmd =
     *        new BaseCmdLine (args,
     *                         new String[] {"-name", "-age", "-v", "-foo"},
     *                         new int[]    {2,        1,      1,    0} );
     *     if (cmd.params.length > 0)
     *        System.err.println ("Error on cmd line...");
     *     ...
     *   }
     * </PRE><P>
     *
     * @param argv array of arguments names and values
     * @param names array of argument names which should be removed from the <tt>argv</tt>
     * @param lengths array where each element represents a number of values associated with
     *                a corresponding argument from array <tt>names</tt>; note that sizes
     *                of <tt>names</tt> and <tt>lengths</tt> arrays should be the same
     */  
    public BaseCmdLine (String[] argv, String[] names, int[] lengths) {
        this (argv, false, names, lengths);
    }

    /**
     * The same as {@link BaseCmdLine (String[])} but it also sets
     * how are methods <tt>getParam()</tt> and <tt>hasParam</tt>
     * case sensitive.
     *
     * @param argv array of arguments names and values
     * @param ignoreCase if true all subsequent comparison of names will be case-insensitive
     *                   (default is case-sensitive)
     */  
    public BaseCmdLine (String[] argv, boolean ignoreCase) {
        this (argv);
        this.ignoreCase = ignoreCase;
    }

    /**
     * A combinantion of {@link BaseCmdLine (String[],boolean)} and 
     * {@link BaseCmdLine (String[],String[],int[])}. <P>
     *
     * @param argv array of arguments names and values
     * @param ignoreCase if true all subsequent comparison of names will be case-insensitive
     *                   (default is case-sensitive)
     * @param names array of argument names which should be removed from the <tt>argv</tt>
     * @param lengths array where each element represents a number of values associated with
     *                a corresponding argument from array <tt>names</tt>; note that sizes
     *                of <tt>names</tt> and <tt>lengths</tt> arrays should be the same
     */  
    public BaseCmdLine (String[] argv, boolean ignoreCase, String[] names, int[] lengths) {
        this (argv, ignoreCase);
        for (int i = 0; i < Math.min (names.length, lengths.length); i++)
            getParam (names[i], lengths[i]);
    }

    /**
     * Ask if a given argument exists. <P>
     *
     * Note that it does not change <tt>params</tt> field, which may result
     * (if used by multiple threads) that next time it can give a different
     * answer (when an another thread extracted the <tt>name</tt> as a value
     * of some other argument). Surely unprobable :-)
     * <P>
     * Why does it not change <tt>params</tt>? Because this method does not
     * know how many values are associated with this parameter. Therefore,
     * nothing can be removed from the list of arguments. However, if you
     * know that this argument has actually no value associated at all - and
     * you need to remove it from the list of arguments - use method
     * {@link #hasOption(String)} instead.
     * <P>
     *
     * @param name an argument we are looking for
     * @return true if <tt>name</tt> was found on the command-line
     */
    public boolean hasParam (String name) {
        if (ignoreCase)
            name = name.toUpperCase();
    
	// we may know already this argument
        if (knownParams.containsKey (name))
            return true;

	// or we have to find it on the command-line
        synchronized (params) {
            for (int i = 0; i <params.length; i++)
                if (ignoreCase && params[i].equalsIgnoreCase (name) ||
                    params[i].equals (name))
                    return true;
	}

	// not found
        return false;
    }

    /**
     * Check if an argument <TT>name</TT> exists, and remove it from the
     * argument list (so it is not any more in <tt>params</tt> field). <P>
     *
     * This is a convenient combinantion of methods <tt>hasParam(name)</tt> (which
     * only checks existence) and <tt>getParam(name,0)</tt> (which removes
     * named argument from the list).
     *
     * @param name an argument we are looking for
     * @return true if <tt>name</tt> was found on the command-line
     *
     * @see #getParam(String,int) getParam
     * @see #hasParam(String) hasParam
     */
    public boolean hasOption (String name) {
        getParam (name, 0);      // this removes 'name' from the argument list
        return hasParam (name);  // and this checks if 'name' really exists
    }

    /**
     * Extract an argument <TT>name</TT> with its value.
     * This is a convenient method for <tt>getParam (String name, 1)</tt>.
     *
     * @param name an argument we are asking for
     * @return a value of this argument, or null if <tt>name</tt> was not found,
     *         or if it has no value at all
     *
     * @see #getParam(String,int) getParam
     */
    public String getParam (String name) {
        return (getParam (name, 1))[0];
    }
        
    /**
     * Extract an argument <TT>name</TT> with its one or more values.
     * <P>
     * An example:<BR>
     * <tt>params</tt> before: <tt>{"-foo", "bar", "-name", "Ernest", "Hemi", "7"}</tt><BR>
     * <tt>getParam ("-name", 2)</tt> returns <tt>{"Ernest", "Hemi"}</tt><BR>
     * <tt>params</tt> now: <tt>{"-foo", "bar", "7"}</tt><BR>
     * <tt>getParam ("-name", 2)</tt> returns again <tt>{"Ernest", "Hemi"}</tt><BR>
     * <tt>getParam ("-name", 1)</tt> returns <tt>{"Ernest", null}</tt><BR>
     * <tt>getParam ("-name", 4)</tt> returns <tt>{"Ernest", null, null, null}</tt><P>
     *
     * Note that nobody checks if an argument is a name or its value. An example:<BR>
     * <tt>params</tt> before: <tt>{"-foo", "bar", "-name", "Ernest", "Hemi", "7"}</tt><BR>
     * <tt>getParam ("-foo", 2)</tt> returns <tt>{"bar", "-name"}</tt><BR>
     * so <tt>params</tt> are now: <tt>{"Ernest", "Hemi", "7"}</tt><P>
     *
     * Note that this method works also for <tt>length</tt> equal zero by returning an
     * empty array. However, in this case you cannot distinguish if the argument was found or not.
     * Therefore, for non-value arguments (which are actually <em>options</em>) is better
     * to use method {@link #hasOption(String)} which checks for existence of an argument and
     * also removes it from the argument list.<P>
     *
     * @param name an argument we are asking for
     * @param length a number of expected values of requested <tt>name</tt>;
     *               it is always a positive number
     * @return a list with extracted values (not including the name);
     *         if <tt>name</tt> was not found, or if less values than requested was available
     *         all or some elements of the returned list may be null
     */
    public String[] getParam (String name, int length) {

        if (length < 0) length = 1;   // just precaution
	String[] results = new String [length];
        if (ignoreCase)
            name = name.toUpperCase();

	// we may know already this argument
	Object obj = knownParams.get (name);
	if (obj != null) {
            if (length > 0) {
	        String[] values  = (String[])obj;
 	        System.arraycopy (values, 0, results, 0, Math.min (results.length, values.length));
	    }
	    return results;
	}

	// or we have to find it on the command-line - and remember it for future
        synchronized (params) {
            for (int i = 0; i < params.length; i++) {
                if (ignoreCase && params[i].equalsIgnoreCase (name) ||
                    params[i].equals (name)) {

		    // name found, extract values into results...
                    if (length > 0 && i < params.length - 1)
                        System.arraycopy (params, i+1, results, 0, Math.min (length, params.length-i-1));

		    // ...and remember both name and value(s)
                    knownParams.put (name, results);

	            // ...and remove name and value(s) from params
                    Vector<String> v = new Vector<String>();
                    for (int j = 0; j < i; j++)
                        v.addElement (params[j]);   // elements before the match
                    for (int j = i + 1 + length; j < params.length; j++)
                        v.addElement (params[j]);   // elements after the match
                    params = new String [v.size()];
                    v.copyInto (params);
                    return results;
		}
	    }
	}

	// name was not found at all, return an empty array of the right size
	return results;
    }
 
}

