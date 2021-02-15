// DGUtils.java
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

package org.tulsoft.tools.debug;

import java.text.*;
import java.util.*;
import java.io.*;

/** An abstract class with methods helping with debugging.
 *
 *  <H5>Last changes (in backwards order)</H5>
 *  <UL>
 *    <LI> Added method {@link #printAR printAR}.
 *  </UL>
 *  <P>
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: DGUtils.java,v 1.2 2007/03/25 18:55:18 marsenger Exp $
 */

public abstract class DGUtils {

    /******************************************************************************
     * Print a stack trace of a given throwable into a string. <P>
     *
     * @param e an throwable (exception) whose stack is being printed
     * @return a stack trace as a string
     ******************************************************************************/
    public static String stackTraceToString (Throwable e) {
	try {
	    ByteArrayOutputStream bout = new ByteArrayOutputStream (1024);
	    PrintWriter out = new PrintWriter (bout, true);
	    e.printStackTrace (out);
	    return bout.toString();
	} catch (Exception ex) {
	    return "Error during printing an error message :-( " + ex.toString();
	}
    }

    /**
     * Produce a concise string that just lists "class.function(file:line)" at
     * the point where the function is called. Pretty much like what C/C++
     * folks used to have with the __FILE__ and __LINE__ macros, but in fact
     * better. This is _not_ a stack trace. 
     * <P>
     * The file:line string has a format that is parseable by emacs
     * compilation mode. 
     * <P>
     * Note: this function produces a class.function(file:line) that
     * corresponds to _where_ it is called. If you want to call this
     * function in an error reporting function (e.g., assertMsg()), use the
     * function {@link #lineAndFile(int)}, because otherwise the
     * class.function(file:line) of the error reporting function
     * (e.g. assertMsg()) itself is returned.
     *
     * @return  a string that looks like "class.function(file:line)"
     * @see #lineAndFile(int) lineAndFile(int) */
    static public String lineAndFile() {
        return lineAndFile(2);
    }

    /**
     * Produce a concise string "class.function(file:line)" corresponding to
     * the call frame at level stackDepth.  It is typically used for
     * constructing error reporting functions, such as assertMsg(). Usually, the
     * stackDepth will be 2: top of stack (depth 0) is lineAndFile(int), that
     * is, the call frame of this function itself; one level up (depth 1)
     * is (e.g.) assertMsg(); depth 2 is the function that called assertMsg().
     * <P>
     * @param stackDepth The stack frame for which
     *    "class.function(file:line)" is wanted (0 is this function itself)
     * @return  a string that looks like "class.function(file:line)"
     * @see #lineAndFile() lineAndFile() 
     */
    static public String lineAndFile(int stackDepth) {
        try { throw new Exception();} catch (Exception e) {
            String trace = DGUtils.stackTraceToString(e);
            String find = "\tat ";
            int len = find.length();

            int start = nthIndexOf(trace, find, stackDepth, 0);
            int end = nthIndexOf(trace, find, 0, start+len);
            if (end == -1) end = trace.length();
            return trace.substring(start+len, end-1);
        }
    }


    /********************************************************************
     * Print a hashtable. <P>
     *
     * @param indent a level of indentation from the left margin
     * @param t a hashtable to be printed
     * @param out a stream where to print
     ********************************************************************/
    public static void printHT (int indent, Hashtable t, PrintStream out) {
        for (Enumeration en = t.keys(); en.hasMoreElements(); ) {
            Object key = en.nextElement();
            Object value = t.get (key);
            if (value instanceof Hashtable) {
                doIndent (indent, out);
                out.println (key);
                printHT (indent + 1, (Hashtable)value, out);
	    } else if (value instanceof Object[]) {
                doIndent (indent, out);
                out.println (key);
                printAR (indent, (Object[])value, out);
            } else {
                doIndent (indent, out);
                out.println (key + " = " + value);
	    }
	}
    }

    /********************************************************************
     * Print an array into System.out with a default indentation.<P>
     *
     * @param a an array to be printed
     ********************************************************************/
    public static void printAR (Object[] a) {
	printAR (0, a, System.out);
    }

    /********************************************************************
     * Print an array.<P>
     *
     * @param indent a level of indentation from the left margin
     * @param a an array to be printed
     * @param out a stream where to print
     ********************************************************************/
    public static void printAR (int indent, Object[] a, PrintStream out) {
	if (a == null) {
	    doIndent (indent, out);
	    out.println ("null");
	    return;
	}
        for (int i = 0; i < a.length; i++) {
            doIndent (indent, out);
            out.println ("[" + i + "]");
            if (a[i] == null) {
                doIndent (indent, out);
                out.println ("null");
	    } else if (a[i] instanceof Hashtable) {
                printHT (indent + 1, (Hashtable)a[i], out);
	    } else {
                doIndent (indent, out);
                out.println (a[i].toString());
	    }
	}
    }

    // print some indentation
    private static void doIndent (int indent, PrintStream out) {
        for (int i = 0; i < indent; i++) out.print ("\t");
    }
            
    /** 
     * analogous to java.lang.String.indexOf(string), but finding not the
     * first occurrence, but the Nth one. Returns the offset of the string
     * found, or -1 if not found.
     *
     * @param s The string to be searched through
     * @param find The string to find
     * @param n    The Nth occurrence. If n==0, this is equivalent to
     *             java.lang.String.indexOf(string).
     * @return  The offset of the found string, or -1 if no string found.
     */
    private static int nthIndexOf(String s, String find, int n) {
        return nthIndexOf(s, find, n, 0);
    }

    /** 
     * analogous to java.lang.String.indexOf(string, fromIndex), but finding
     * not the first occurrence, but the Nth one, starting at from. Returns
     * the offset of the string found, or -1 if not found.
     *
     * @param s The string to be searched through
     * @param find The string to find
     * @param n    The Nth occurrence. If n==0, this is equivalent to
     *             java.lang.String.indexOf(string)
     * @param find The position to start at.
     * @return  The offset of the found string, or -1 if no string found.
     */
    private static int nthIndexOf(String s, String find, int n, int from) {
        int f = 0; int i = 0;  int len = find.length();

        for(f = s.indexOf(find, f+from); 
            i<n && f != -1; f = s.indexOf(find, f+len))
            i++;
        return f;
    }

}
