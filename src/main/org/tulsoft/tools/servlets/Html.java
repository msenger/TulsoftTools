// Html.java
//
// Created: August 2001
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

package org.tulsoft.tools.servlets;

import org.tulsoft.shared.UUtils;

import javax.servlet.http.*;
import java.util.*;
import java.io.*;

/**
 * A generator of the HTML pages.  <P>
 *
 * Inspired by the Perl module CGI.pm but doing only very small part
 * of its functionality. I found a package JHTML (www.sourceforge.org)
 * doing the same - but it was empty at the time I needed
 * it. Therefore, I wrote a basic part of it here - but it lacks many
 * of the various HTML form elements. <P>
 *
 * The documentation is not ready - please use common sense or look
 * into the source code where are some comments. <P>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: Html.java,v 1.2 2007/03/25 18:55:19 marsenger Exp $
 */

public class Html
    implements HtmlConstants {

    private static final Hashtable<String,Object> emptyHashtable =
	new Hashtable<String,Object>();

    // properties - they can be set/get, and they influence generating code
    protected HttpServletRequest request = null;
    protected boolean autoEscape = true;

    /*************************************************************************
     * An empty constructor.
     *************************************************************************/
    public Html() {
    }

    /*************************************************************************
     * The constructor remembering what the servlet request needs this
     * instance.
     * @param req see {@link #setRequester setRequester}
     *************************************************************************/
    public Html (HttpServletRequest req) {
	setRequester (req);
    }

    /*************************************************************************
     * Set requester for whom the HTML code is generated. The main reaosn
     * to set this is to have access to the previous values of the HTML
     * forms - and to use them as default values in the HTML form elements.
     *************************************************************************/
    public void setRequester (HttpServletRequest request) {
	this.request = request;
    }

    public boolean setAutoEscape (boolean newValue) {
	boolean oldValue = autoEscape;
	autoEscape = newValue;
	return oldValue;
    }

    public String gen (String tag, String contents) {
	return gen (tag, emptyHashtable, contents);
    }

    public String gen (String tag, String[] attrs, String contents) {
	return gen (tag, array2hash (attrs), contents);
    }

    public String gen (String tag, String[] attrs) {
	return gen (tag, array2hash (attrs), null);
    }

    public String gen (String tag, Hashtable<String,Object> attrs) {
	return gen (tag, attrs, null);
    }

    public String gen (String tag) {
	return gen (tag, emptyHashtable, null);
    }

    public String gen (String tag,
		       String attrName, String attrValue,
		       String contents) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	ht.put (attrName, attrValue);
	return gen (tag, ht, contents);
    }

    //
    // attribute values are escaped here automatically - but not the contents
    //
    public String gen (String tag, Hashtable<String,Object> attrs, String contents) {
	StringBuffer buf = new StringBuffer();
	buf.append ("<" + tag);
	String name;
	Object value;
	for (Enumeration en = attrs.keys(); en.hasMoreElements(); ) {
	    name = (String)en.nextElement();
	    if (UUtils.isEmpty (name)) continue;
	    value = attrs.get (name);
	    buf.append (" " + name);
	    if (! (value instanceof NullObject))
		buf.append ("=\"" +
			    (autoEscape ? esc ((String)value) : value) +
			    "\"");
	}
	buf.append (">");
	if (contents == null)
	    return new String (buf);
	buf.append (contents);
	buf.append ("</" + tag + ">");
	return new String (buf);
    }

    public String end (String tag) {
	return ("</" + tag + ">");
    }



    // special 'gen' for tag "A" because:
    // - it does not end with newline (as 'gen' does)
    // - it does not escape automatically its attributes
    //   (so you do not need to switch off setAutoEscape
    //    manually for URLs with ampersands)
    public String a (String href, String contents) {
	return a (href, null, contents);
    }

    public String a (String href, String target, String contents) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	ht.put (HREF, href);
	if (target != null) ht.put (TARGET, target);
	return a (ht, contents);
    }

    public String a (String[] attrs, String contents) {
	return a (array2hash (attrs), contents);
    }

    public String a (Hashtable<String,Object> attrs, String contents) {
	boolean localAutoEscape = setAutoEscape (false);
	StringBuffer buf = new StringBuffer();
	buf.append (gen (A, attrs));
	buf.append (contents);
	buf.append (end (A));
	setAutoEscape (localAutoEscape);
	return new String (buf);
    }




    public String startHtml (String title) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	ht.put (TITLE, title);
	return startHtml (ht, null);
    }

    public String startHtml (String[] attrs) {
	return startHtml (array2hash (attrs), null);
    }

    //
    // recognized attributes are put in the 'head' section - they are
    // TITLE, SCRIPT, NOSCRIPT; the others are put in the 'body' section;
    // 'theRest' - if not null - is put literally into the 'head' section
    // (it is for 'meta', 'styles, 'link, etc.)
    //
    public String startHtml (Hashtable<String,Object> attrs, String theRest) {
	StringBuffer buf = new StringBuffer();
	buf.append (gen (HTML) + gen (HEAD));
        Hashtable<String,Object> ht = new Hashtable<String,Object>();
	String key;
	for (Enumeration en = attrs.keys(); en.hasMoreElements(); ) {
	    key = (String)en.nextElement();
	    if (key.equalsIgnoreCase ("TITLE"))
		buf.append (gen (TITLE, (String)attrs.get (key)));
	    else if (key.equalsIgnoreCase ("SCRIPT")) {
		Hashtable<String,Object> ht2 = new Hashtable<String,Object>();
		ht2.put (TYPE, "text/javascript");
		ht2.put (LANGUAGE, "JavaScript");
		buf.append (gen (SCRIPT, ht2,
				 "<!-- Hide script\n" +
				 attrs.get (key) + "\n" +
				 "// End script hiding -->\n"));
	    } else if (key.equalsIgnoreCase ("NOSCRIPT"))
		buf.append (gen (NOSCRIPT, (String)attrs.get (key)));
	    else
		ht.put (key, attrs.get (key));
	}
	if (theRest != null) buf.append (theRest);
	buf.append (end (HEAD));
	buf.append (gen (BODY, ht));
	return new String (buf);
    }

    public String endHtml() {
	return end (BODY) + end (HTML);
    }



    public String text (String name) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	ht.put (NAME, name);
	return text (ht);
    }

    public String text (String name, String dflt) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	ht.put (NAME, name);
	ht.put (VALUE, dflt);
	return text (ht);
    }

    public String text (String name, String dflt, int size) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	ht.put (NAME, name);
	ht.put (VALUE, dflt);
	ht.put (SIZE, "" + size);
	return text (ht);
    }

    public String text (String name, String[] attrs) {
	Hashtable<String,Object> ht = array2hash (attrs);
	ht.put (NAME, name);
	return text (ht);
    }

    public String text (Hashtable<String,Object> attrs) {
	stickyPreviousValue (attrs, VALUE);
	return gen (INPUT, attrs, null);
    }



    public String hidden (String name, String value) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	ht.put (NAME, name);
	if (value != null) ht.put (VALUE, value);
	return hidden (ht);
    }

    public String hidden (String name, String value, boolean force) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	ht.put (NAME, name);
	if (value != null) ht.put (VALUE, value);
	ht.put (FORCE, new Boolean (force));
	return hidden (ht);
    }

    public String hidden (Hashtable<String,Object> attrs) {
	attrs.put (TYPE, "hidden");
	stickyPreviousValue (attrs, VALUE);
	attrs.remove (FORCE);
	return gen (INPUT, attrs, null);
    }




    public String checkbox (String name) {
	return checkbox (name, false, "on");
    }

    public String checkbox (String name, boolean checked) {
	return checkbox (name, checked, "on");
    }

    public String checkbox (String name, boolean checked, String value) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	ht.put (TYPE, "checkbox");
	ht.put (NAME, name);
	if (value != null) ht.put (VALUE, value);
	if (checked) ht.put (CHECKED, getNullObject());
	return gen (INPUT, ht);
    }

    public String checkbox (String name, String[] attrs) {
	Hashtable<String,Object> ht = array2hash (attrs);
	ht.put (TYPE, "checkbox");
	return gen (INPUT, ht);
    }



    //
    // it may replace 'attrName' attribute in 'attrs' (which usually
    // represents a default value) by the previous value, if there is
    // a parameter NAME both in 'attrs' (which usually is) and in the
    // current Http request object (which is when this page is generated
    // not the first time);
    // it does not happen if there is a FORCE attribute in 'attrs';
    // 
    //
    protected void stickyPreviousValue (Hashtable<String,Object> attrs, String attrName) {
	Object force = attrs.get (FORCE);
	if (force != null) {
	    if (force instanceof String && UUtils.is ((String)force))
		return;
	    if (force instanceof Boolean && ((Boolean)force).booleanValue())
		return;
	}

	String name = (String)attrs.get (NAME);   // now 'name' is the name of the form element
	if (request != null && name != null) {
	    String previousValue = (String)request.getParameter (name);
	    if (previousValue != null)
		attrs.put (attrName, previousValue);
	}
    }



    public String submit (String label) {
	return submit (label, null);
    }

    public String submit (String label, String name) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	ht.put (TYPE, "submit");
	if (name != null) ht.put (NAME, name);
	if (label != null) ht.put (VALUE, label);
	return gen (INPUT, ht);
    }


    public String button (String label) {
	return button (label, null, label);
    }

    public String button (String label, String onClick) {
	return button (label, onClick, label);
    }

    public String button (String label, String onClick, String name) {
	return
	    "<INPUT TYPE=\"button\" NAME=\"" + name + "\"" +
	    " VALUE=\"" + label + "\"" +
	    (UUtils.isEmpty (onClick) ? "" : " ONCLICK=\"" + onClick + "\"") +
	    ">\n";
    }



    public String list (String name, String[] labels) {
	return list (name, labels, null, null);
    }

    public String list (String name, String[] labels,
			Hashtable<String,String> selected) {
	return list (name, labels, null, selected);
    }

    public String list (String name, String[] labels, String[] values,
			Hashtable<String,String> selected) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	ht.put (NAME, name);
	return list (ht, labels, values, selected);
    }

    public String list (String[] attrs, String[] labels, String[] values,
			Hashtable<String,String> selected) {
	return list (array2hash (attrs), values, labels, selected);
    }

    //
    // 'labels' cannot be null;
    // 'values' are used only if they are not null and if their length is at least
    //   as big as the length of labels;
    // 'selected' can contain names from 'labels' (this one has priority) or from
    //   'values'; can be null;
    // both 'labels' and 'values' are escaped here if necessary
    //
    public String list (Hashtable<String,Object> attrs,
			String[] labels,
			String[] values,
			Hashtable<String,String> selected) {
	StringBuffer buf = new StringBuffer();
	buf.append (gen (SELECT, attrs));
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	NullObject nullObj = new NullObject();
	if (values == null || values.length < labels.length) {
	    for (int i = 0; i < labels.length; i++) {
		if (selected != null && selected.get (labels[i]) != null)
		    ht.put (SELECTED, nullObj);
		else
		    ht.remove (SELECTED);
		buf.append (gen (OPTION, ht, esc (labels[i])));
	    }
	} else {
	    for (int i = 0; i < labels.length; i++) {
		ht.put (VALUE, values[i]);
		if (selected != null &&
		    (selected.get (labels[i]) != null || selected.get (values[i]) != null))
		    ht.put (SELECTED, nullObj);
		else
		    ht.remove (SELECTED);
		buf.append (gen (OPTION, ht, esc (labels[i])));
	    }
	}
	buf.append (end (SELECT));
	return new String (buf);
    }



    /*************************************************************************
     * Make 'value' HTML friendly. <p>
     *
     * @param value will be checked
     * @return escaped 'value'
     *************************************************************************/
    public static String esc (String value) {
	int len = value.length();
	StringBuffer buf = new StringBuffer (len);
	char c;
	for (int i = 0; i < len; i++) {
	    c = value.charAt(i);
            if (c == '"')       buf.append ("&quot;");
            else if (c == '&')  buf.append ("&amp;");
            else if (c == '<')  buf.append ("&lt;");
            else if (c == '>')  buf.append ("&gt;");
            else {
                int ci = 0xffff & c;
                if (ci < 160 ) {
                    // has only 7 bits => nothing special
                    buf.append(c);
                } else {
                    // has 8 bits => escape them
                    buf.append ("&#");
                    buf.append (new Integer(ci).toString());
                    buf.append (';');
		}
            }
        }
	return buf.toString();
    }

    public static String[] trimList (int maxLen, String[] source) {
	if (maxLen < 1) return source;   // nothing done

	synchronized (source) {
	    String[] result = new String [source.length];
	    boolean changed = false;
	    for (int i = 0; i < source.length; i++) {
		if (source[i].length() > maxLen + 3) {
		    result[i] = source[i].substring (0, maxLen-3) + "...";
		    changed = true;
		} else {
		    result[i] = source[i];
		}
	    }
	    return (changed ? result : source);
	}
    }





    
    public static Hashtable<String,Object> array2hash (String[] attrs) {
	Hashtable<String,Object> ht = new Hashtable<String,Object>();
	for (int i = 0; i < attrs.length - 1; i += 2)
	    ht.put (attrs[i], attrs [i+1]);
	return ht;
    }

    public NullObject getNullObject() {
	return new NullObject();
    }

    public class NullObject {
    }

}
