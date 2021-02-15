// UUtils.java
//
//    Created: April 1999
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

import java.text.*;
import java.util.*;
import java.io.*;

/**
 * An abstract class containing various utilities, including string,
 * array and some formatting utilities. <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: UUtils.java,v 1.5 2007/03/25 18:55:17 marsenger Exp $
 */

public abstract class UUtils {

    /** Version and date of the last update. */
    public static final String VERSION = "$Id: UUtils.java,v 1.5 2007/03/25 18:55:17 marsenger Exp $";

    /**************************************************************************
     * Check if the given string is null or empty. <p>
     *
     * @param value a tested string
     * @return true if the string is empty
     **************************************************************************/
    public static boolean isEmpty (String value) {
        return ( value == null || "".equals (value.trim()) );
    }

    /**************************************************************************
     * Opposite to {@link #isEmpty}. <p>
     *
     * @param value a tested string
     * @return true if the string is not null and is not an empty string
     **************************************************************************/
    public static boolean notEmpty (String value) {
	return ! isEmpty (value);
    }

    /**************************************************************************
     * Check if the given string represents value 'true' or 'false'.
     * It is similar to Boolean.valueOf(String) but recognizes as true
     * also "yes", "+", "1", "on" (and the usual "true"), in
     * case-insensitive way.
     * 
     * @param str a tested string
     * @return true if the given string represents a 'true' value
     **************************************************************************/
    public static boolean is (String str) {
        return ( "true".equalsIgnoreCase (str) ||
		 "yes".equalsIgnoreCase (str)  ||
		 "on".equalsIgnoreCase (str)   ||
		 "+".equals (str)              ||
		 "1".equals (str)
		 );
    }

    private static int counter = 0;
    private static long lastReturned = new Date().getTime();
    /******************************************************************************
     * Return a unique string - made from the current time.<P>
     *
     * @return a unique string
     ******************************************************************************/
    public static String unique () {
        long now = new Date().getTime();
        if (now == lastReturned) return now + "." + (++counter);
        lastReturned = now;
        return "" + now;
    }

    /******************************************************************************
     * Given a date, it formats it for the current Time Zone.<P>
     *
     * @param date a date to be formatted
     * @return formatted date
     ******************************************************************************/
    public static String formatDate (Date date) {
	DateFormat df = DateFormat.getDateTimeInstance ();
	df.setCalendar (Calendar.getInstance());
	return df.format (date) + " (" + df.getTimeZone().getID() + ")";
    }

    /******************************************************************************
     * Return a formatted string representing today date using the current Time Zone.<P>
     *
     * @return formatted today date
     ******************************************************************************/
    public static String formatDate() {
        return formatDate (new Date());
    }

    /******************************************************************************
     * Given a string containing the number of non-leap seconds since "the epoch"
     * (Jan 1, 1970, UTC), it returns a formatted string date for the current TZ.<P>
     *
     * @param seconds time in seconds from "the epoch"
     * @return formatted date, or "unknown" if input string is not numeric or zero
     ******************************************************************************/
    public static String formatDate (String seconds) {
        Date date;
	try {
	    date = new Date (  new Long (seconds).longValue() * 1000  );
	} catch (NumberFormatException e) {
	    return "unknown";
	}
	return formatDate (date);
    }

    /******************************************************************************
     * Given a number of miliseconds since "the epoch"
     * (Jan 1, 1970, UTC), it returns a formatted string date for the current TZ.<P>
     *
     * @param miliSeconds time in miliseconds from "the epoch"
     * @return formatted date or "unknown" if input string is not numeric or zero
     ******************************************************************************/
    public static String formatDate (long miliSeconds) {
	Date date;
	try {
	    date = new Date (miliSeconds);
	} catch (NumberFormatException e) {
	    return "unknown";
	}
	return formatDate (date);
    }

    /*****************************************************************************
     * Return a number created from a string.
     *
     * @deprecated Use instead
     * <tt>org.apache.commons.lang.math.NumberUtils.toInt()</tt>.
     *
     * @param str a string containing digits (and "digit usual" characters)
     * @return a converted integer or zero if string was not so numeric
     *****************************************************************************/
    public static int toInt (String str) {
	int i;
	try {
	    if (str == null) i = 0;
	    else             i = Integer.valueOf (str).intValue();
	} catch (java.lang.NumberFormatException e) {
	    i = 0;
	}
	return i;
    }

    /******************************************************************************
     * Given an array of arrays, flaten it into one array.
     *
     * @param arr an array of String arrays
     * @return one array containing all elements of input 'arr' (in the same order)
     ******************************************************************************/
    public static String[] arraymerge (String[][] arr) {
        int entireLen = 0;
        for (int i = 0; i < arr.length; i++) entireLen += arr[i].length;
        String[] result = new String [entireLen];

        int cumulativeLen = 0;
        for (int i = 0; i < arr.length; i++) {
            System.arraycopy (arr[i], 0, result, cumulativeLen, arr[i].length);
            cumulativeLen += arr[i].length;
	}
        return result;
    }

    /******************************************************************************
     * As <tt>arraymerge()</tt> but do not put duplicates into the result array.
     *
     * @param arr an array of String arrays
     * @return one array containing all elements (but without duplicated elements)
     *         of the input array of arrays (in the same order)
     * @see #arraymerge(String[][]) arraymerge
     ******************************************************************************/
    public static String[] uniqueArraymerge (String[][] arr) {
        Vector<String> v = new Vector<String>();
        Hashtable<String,String> h = new Hashtable<String,String>();
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                String value = arr[i][j];
                if (h.containsKey (value)) continue;
                h.put (value, "");
                v.addElement (value);
	    }
	}
        String[] merged = new String [v.size()];
        v.copyInto (merged);
        return merged;
    }

    /******************************************************************************
     * Join together two string arrays.
     *
     * @param arr1 the first array
     * @param arr2 the second array
     * @return an array containing elements from <tt>arr1</tt> and <tt>arr2</tt>
     *         (in this oder)
     ******************************************************************************/
    public static String[] join (String[] arr1, String[] arr2) {
	synchronized (arr1) {
	    synchronized (arr2) {
		String[] result = new String [arr1.length + arr2.length];
                System.arraycopy (arr1, 0, result, 0, arr1.length);
                System.arraycopy (arr2, 0, result, arr1.length, arr2.length);
		return result;
	    }
	}
    }

    /******************************************************************************
     * Set a System property. This method is similar to the Java 1.2 method
     * System.setProperty() with one difference: it does not set the given
     * property if the property already exists.
     *
     * @param name of a property that is being set
     * @param value of the property
     * @see #setSystemProperty(String,String,boolean)
     ******************************************************************************/
    public static void setSystemProperty (String name, String value) {

	try {
	    if (System.getProperty (name) == null) {
		Properties props = System.getProperties();
		props.put (name, value);
		System.setProperties (props);
	    }
	} catch (SecurityException e) {
	}
    }

    /******************************************************************************
     * Set a System property. This method is similar to the Java 1.2 method
     * System.setProperty() with one difference: it does not set the given
     * property if the property already exists _and_ 'forceOverwrite' is not true.
     *
     * @param name of a property that is being set
     * @param value of the property
     * @param forceOverwrite if true then new property overwrites the existing one
     * @see #setSystemProperty(String,String)
     ******************************************************************************/
    public static void setSystemProperty (String name, String value,
					  boolean forceOverwrite) {

	try {
	    if (forceOverwrite || (System.getProperty (name) == null)) {
		Properties props = System.getProperties();
		props.put (name, value);
		System.setProperties (props);
	    }
	} catch (SecurityException e) {
	}
    }

}
