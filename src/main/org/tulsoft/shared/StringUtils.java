// StringUtils.java
//
// Created: April 2001
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

import java.util.Vector;

/**
 * An abstract class with several utilities dealing with strings. <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: StringUtils.java,v 1.3 2007/03/25 18:55:17 marsenger Exp $
 **/

public abstract class StringUtils { 
  
    /** Version and date of the last update.
     */
    public static final String VERSION = "$Id: StringUtils.java,v 1.3 2007/03/25 18:55:17 marsenger Exp $";


    /** 
     * unto each string in <var>strings</var>, prepend the string Prefix and
     * return the resulting list of strings.
     * <p>
     * @param prefix  The string to be prepended
     * @param strings The strings unto which to prepend
     * @return        An array of strings that all have <var>prefix</var> 
     *                prepended.
     **/
    public static String[] addPrefix(String prefix, String [] strings) {
	synchronized (strings) {
	    String [] list = new String[strings.length];
	    for(int i=0; i<list.length; i++) 
		list[i]= prefix + strings[i];
	    return list;
	}
    } // addPrefix

    /** 
     * unto each string in <var>strings</var>, append the string
     * <var>suffix</var> and return the resulting list of strings.
     * <p>
     * @param suffix  The string to be appended
     * @param strings The strings unto which to append
     * @return        An array of strings that all have
     *                <suffix>suffix</suffix> appended. 
     **/
    public static String[] addSuffix(String suffix, String [] strings) {
	synchronized (strings) {
	    String [] list = new String[strings.length];
	    for(int i=0; i<list.length; i++) 
		list[i]= strings[i] + suffix;
	    return list;
	}
    } // addSuffix

    /**
     * remove occurrences of character CHAR from  string STR.
     * <p>
     * @param CHAR The character to be removed. 
     * @param str  The string from which to remove them
     * @return     string with CHARs removed
     **/
    public static String  stripChar(char CHAR,String  str) { 
	char s[] = str.toCharArray();
	int padds = 0;
	for ( int i = 0;  i < s.length; i++) {
	    if ( s[ i] == CHAR ) padds++;
	}
	char n[] = new char[ s.length - padds];
	int j = 0;
	for ( int i = 0;  i < s.length; i++) {
	    if ( s[ i] !=  CHAR ) 
		n[ j++] = s[ i] ;
	}
	return new String( n);
    } // stripChar()

    /**
     * number of occurrences of character CHAR before location loc in string
     * str. 
     * <p>
     * @param CHAR 	character whose occurrences should be counted
     * @param str 	the string in which to do this
     * @param loc 	the offset in str at which to start.
     * @return 		number of occurrences.
     *
     **/
    public static int numBeforeLocation( char CHAR, String str, int loc) { 
	char c[] = str.toCharArray();
	int ix = 0;
	for ( int i = 0 ; i < Math.min (loc, c.length) ; i++ ) {  // M.S. corrected
	    if ( c[i] == CHAR ) ix ++ ;
	}
	return ix;
    }

    /**
     * return list of positions of character CHAR inside string str.
     * <p>
     * @param CHAR 	the character whose positions are sought
     * @param str 	the string in which to find them
     * @return 		a (possibly empty) list of integers that lists the
                        positions at which CHAR was found.
     * 
     **/
    public static int[] positions( char CHAR, String str) { 
	char c[] = str.toCharArray();
	int ix = 0;
	for ( int i = 0 ; i < c.length ; i++ ) { 
	    if ( c[i] == CHAR ) ix ++ ;
	}
	int res[] = new int[ ix];
	ix = 0;
	for ( int i = 0 ; i < c.length ; i++ ) { 
	    if ( c[i] == CHAR ) { 
		res[ ix++] =  i; 
	    }
	}
	return res;
    }
    
    /**
     * removes all whitespace and 'unprintables characters' from String str,
     * and returns new one. 
     * <p>
     * @param  str The string on which  to operate
     * @return a string that doesn't have the whitespace nor unprintables
     */
    public static String removeWhites(String str) {
	StringBuffer buf = new StringBuffer();   // M.S. changed to StringBuffer
	char c[] = str.toCharArray();
	for ( int i = 0 ; i < c.length ; i++ ) { 
	    if ( c[i] > ' ' )  
                buf.append (c[ i]);
	}
	return new String (buf);
    }

    /** Opposite of split; concatenates STRINGLIST using DELIMITER as the
     *  separator. The separator is only added between strings, so there will
     *  be no separator at the beginning or end. 
     * <p>
     * @param  stringList The list of strings that will to be put together
     * @param  delimiter  The string to put between the strings of stringList
     * @return            string that has DELIMITER put between each of the
                          elements of stringList
     */
    public static String join (String[] stringList, String delimiter) {
        int len = stringList.length;
        StringBuffer buf = new StringBuffer(len*20);
	synchronized (stringList) {
	    for (int i = 0; i < len - 1; i++) {
		buf.append (stringList [i]);
		buf.append (delimiter);
	    }
            if (len > 0)
		buf.append (stringList [len - 1]);
	}
        return buf.toString();
    }

    /** Make one string out of stringList by putting spaces between each
        element. Same as join(stringList, " "); 
     * <p>
     * @param  stringList the list of strings that will be put together
     * @return            a string that has ' ' put between each of the
                          elements of stringList. 
     * @see               #join(String[],String) join(String[],String)

     */
    public static String join(String [] stringList) {
        return join(stringList, " ");
    }


    /** Like join(String[], String), but for vectors of strings. Concatenates
     * <var>stringVector</var> using <var>delimiter</var>as the
     *  separator. The separator is only added between strings, so there will
     *  be no separator at the beginning or end. 
     * <p>
     * @param  stringVector the vector of strings that will be concatenated
     * @param  delimiter  The string to put between the strings of stringList
     * @return            string that has DELIMITER put between each of the
                          elements of stringList
     */
    public static String join (Vector stringVector, String delimiter) {
        int len = stringVector.size();
        StringBuffer buf = new StringBuffer(len*20);
	synchronized (stringVector) {
	    for (int i = 0; i < len - 1; i++) {
		buf.append( (String)stringVector.elementAt(i));
		buf.append (delimiter);
	    }
            if (len > 0)
		buf.append((String)stringVector.elementAt(len - 1));
	}
        return buf.toString();
    }


    /** Make one string out of the vector of strings by putting spaces
     *  between each element. Same as join(stringVector, " ");
     * <p>
     * @param  stringVector the vector of strings that will be put together
     * @return            a string that has ' ' put between each of the
                          elements of stringList. 
     * @see               #join(String[],String) join(String[],String)

     */
    public static String join(Vector stringVector) {
        return join(stringVector, " ");
    }


    /** In string <var>string</var>, replace all occurrences of
     * <var>from</var> by <var>to</var> and return the resulting string. 
     * <p>
     * If
     * string <var>from</var> is empty, this will be considered to match no
     * single occurrence of it in the target string, hence no replacements
     * will be made. Note that <var>to</var> cannot be null. It can be the
     * empty string, resulting in deletion of the substring; see also method
     * delete(String,String); 
     * <p>
     * @param string The string in which the replacements will be made
     * @param from String that, when it occurs in string, will be replaced
     * @param to The replacement of from in string
     * @return A new string 
     * @see #delete(String,String) 
     */
    public static String replace(String string, String from, String to) {
        if (from.equals("")) 
            return string;
        StringBuffer buf = new StringBuffer(2*string.length());

        int previndex=0;
        int index=0;
        int flen = from.length();
        while (true) { 
            index = string.indexOf(from, previndex);
            if (index == -1) {
                buf.append(string.substring(previndex));
                break;
            }
            buf.append( string.substring(previndex, index) + to );
            previndex = index + flen;
        }
        return buf.toString();
    } // replace

    /** Delete all occurrences of substrings <var>delete</var> from
     * <var>string</var>. Same as replace(string, from, "") 
     * <p>
     * @param  string The string from which substrings will be deleted.
     * @param  delete The substrings that are to be deleted.
     * @return        new string.
     * @see    #replace(String, String, String)
     */ 
    public static String delete(String string, String delete) {
        return replace(string, delete, "");
    }

    /** Opposit string. 
     * @param str string
     * @param pos positions of chars to be removed (must be ordered from
     *        lowest to highest and position can occur only once)
     * @return new string from which the chars are removed
     **/
    public static String removePositions( String str , int pos[] ) {
	char[] n = new char [ str.length() - pos.length ];
	char[] s = str.toCharArray();
	int shift = 0;
	for ( int i = 0; i < s.length ; i++ ) {
	    if ( shift < pos.length && i == pos[ shift]) {
		shift++;
	    } else {
		n[ i - shift] = s[ i];
	    }
	    
	}
	return new String ( n);
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
    public static int nthIndexOf(String s, String find, int n) {
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
     * @param from The position to start at.
     * @return  The offset of the found string, or -1 if no string found.
     */
    public static int nthIndexOf(String s, String find, int n, int from) {
        int f = 0; int i = 0;  int len = find.length();

        for(f = s.indexOf(find, f+from); 
            i<n && f != -1; f = s.indexOf(find, f+len))
            i++;
        return f;
    }

    /** Given a vector of Strings, returns an array of strings 
     *
     * @param v The vector (containing Strings) that needs to
     *             be converted
     * @return An array of containing the same strings.
     */
    static public String[] toStringArray(Vector v) {
        String[] s = new String [ v.size() ];
        v.copyInto(s);
        return s;
    } // toStringArray


    /**
     * Turn a string into a the same string starting with uppercase, rest all
     * lower case. E.g. "aBc" => "Abc"
     * @param string The string to be operated on
     * @return                  The capitalized string, or the same one if
     *                          it was null or empty
     *
     */
    public static String capitalize(String string) {
        if (string==null || string.equals(""))
            return string;
        return string.substring(0,1).toUpperCase() 
            + string.substring(1).toLowerCase();
    }

    /********************************************************************
     * Convert a given string to an array of lines, but with maximum
     * of 'maxLines' to prevent too lengthty output.
     *
     * @param str is an input message to be cut into pieces
     * @param maxLines a maximum number of line of the output
     * @return an array of lines cut from the intup string
     ********************************************************************/
    static public String[] customizeMsg (String str, int maxLines) {
	return customizeMsg (str, maxLines, -1);
    }

    /********************************************************************
     * Convert a given string to an array of lines, but with maximum
     * of 'maxLines', each line with a given maximum length. <p>
     *
     * @param str is an input message to be cut into pieces
     * @param maxLines a maximum number of line of the output
     * @param maxLineLength a maximum length (approx.) of each line
     * @return an array of lines cut from the intup string
     ********************************************************************/
    static public String[] customizeMsg (String str, int maxLines, int maxLineLength) {
	String[] msgLines = str.split ("\n");
        if (msgLines.length > maxLines) {
            String[] tmp = new String [maxLines];
            System.arraycopy (msgLines, 0, tmp, 0, tmp.length);
            msgLines = tmp;
	}

	if (maxLineLength > 0) {
	    for (int i = 0; i < msgLines.length; i++)
		if (msgLines[i].length() > maxLineLength)
		    msgLines[i] = msgLines[i].substring (0, maxLineLength) + "...";
	}

        return msgLines;
    }
           

} // StringUtils
