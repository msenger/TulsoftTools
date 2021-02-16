// XMLUtils2.java
//
// Created: March 2001
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

package org.tulsoft.tools.xml;

import org.tulsoft.shared.UUtils;
import org.tulsoft.shared.GException;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.*;

/********************************************************************
 * This class contains several common utilities helping with dealing
 * XML parsers conforming SAX 2.0 specification. Mostly there are
 * small wrappers for various pieces of SAX parsers.
 * <P>
 * This class replaces now deprecated class {link XMLUtils}. The old
 * class used SAX 1.0, the new one uses SAX 2.0.
 * <P>
 * @author <A HREF="mailto:senger@ebi.ac.uk">Martin Senger</A>
 * @version $Id: XMLUtils2.java,v 1.2 2007/03/31 11:11:51 marsenger Exp $
 ********************************************************************/
public abstract class XMLUtils2 {

    /********************************************************************
     * A utility method returning an XML parser with registered
     * content and error handler <TT>outputHandler</TT>. It is a shortcut
     * for sequence <tt>makeParser</tt>, <tt>setContentHandler</tt> and
     * <tt>setErrorHandler</tt>, also wrapping all exceptions into one.
     * <P>
     * It uses default class name <b>org.apache.xerces.parsers.SAXParser</b>
     * representing a SAX parser.
     * <P>
     * @param outputHandler is an instance of class implementing
     *        content and error handler for SAX parser - to become
     *        a registered handler.
     * @return a SAX parser with a registrated handler
     * @throws GException if anything goes wrong (with the original exception
     *         available)
     ********************************************************************/
    public static XMLReader makeXMLParser (DefaultHandler outputHandler)
	throws GException {
        return makeXMLParser (null, outputHandler);
    }

    /********************************************************************
     * A utility method returning an XML parser with registered
     * content and error handler <TT>outputHandler</TT>. It is a shortcut
     * for sequence 'makeParser', 'setContentHandler' and
     * 'setErrorHandler', also wrapping all exceptions into one.
     * <P>
     *
     * @param parserClass is a class representing SAX parser which
     *        is being created; if it is null it uses
     *        default class name <b>org.apache.xerces.parsers.SAXParser</b>
     * @param outputHandler is an instance of class implementing
     *        content and error handler for SAX parser - to become
     *        a registered handler.
     * @return a SAX parser with a registered handler
     * @throws GException if anything goes wrong (with the original exception
     *         available)
     ********************************************************************/
    public static XMLReader makeXMLParser (String parserClass,
					   DefaultHandler outputHandler)
	throws GException {

        XMLReader parser = null;
	try {
	    if (parserClass == null) {
    		parser = XMLReaderFactory.createXMLReader();

//    	    parser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser().getXMLReader();

 	    } else {
 		parser = XMLReaderFactory.createXMLReader (parserClass);
 	    }
            parser.setContentHandler (outputHandler);
            parser.setErrorHandler (outputHandler);
	} catch (Exception e) {
            throw new GException ("Parser class: " + parserClass + "(" + e.getMessage() + ")", e);
	}
        return parser;
    }

    /********************************************************************
     * Returns a slightly formatted string with the location in an XML
     * file. It contains last part of SystemId, line number and column
     * number.
     *
     * @param ex is a caught exception from where this method is
     *           usually called
     * @return a formatted location string
     ********************************************************************/
    public static String getLocationString (SAXParseException ex) {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf ('/');
            if (index != -1) 
                systemId = systemId.substring (index + 1);
            str.append (systemId);
        }
        str.append (':');
        str.append (ex.getLineNumber());
        str.append (':');
        str.append (ex.getColumnNumber());

        return str.toString();
    }

    /********************************************************************
     * Formats information retrievable from a SAXException.
     * file. It contains SystemId, PublicId, line number and column
     * number.
     *
     * @param ex is a caught exception from where this method is
     *           usually called
     * @return a formatted error message
     ********************************************************************/
    public static String getFormattedError (SAXException ex) {
        StringBuffer buf = new StringBuffer();

	if (ex instanceof SAXParseException) {
            SAXParseException pex = (SAXParseException)ex;
	    buf.append ("SystemID: " + pex.getSystemId() + "\n");
            String publicId = pex.getPublicId();
            if (publicId != null)
		buf.append ("PublicID: " + publicId + "\n");
            int line = pex.getLineNumber();
            if (line > 0)
                buf.append ("Line (perhaps): " + line + "\n");
            int column = pex.getColumnNumber();
            if (column > 0)
                buf.append ("Column: " + column + "\n");
	}

        Exception inner = ex.getException();
        if (inner != null) {
            String msg = inner.getMessage();
            if (! UUtils.isEmpty (msg))
                buf.append ("Message: " + msg + "\n");
	    ByteArrayOutputStream bout = new ByteArrayOutputStream (1024);
	    PrintWriter out = new PrintWriter (bout, true);
            inner.printStackTrace (out);
	    buf.append (bout.toString());
	}

	return new String (buf);
    }

}
