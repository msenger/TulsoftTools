// XMLErrorHandler.java
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

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/********************************************************************
 * An intermediate class laying between DefaultHandler
 * (defined in SAX 2.0) and project specific XML content handlers. It implements
 * methods for error handling - by printing errors on stderr.
 * <P>
 * @author <A HREF="mailto:senger@ebi.ac.uk">Martin Senger</A>
 * @version $Id: XMLErrorHandler.java,v 1.1 2005/09/03 10:58:18 marsenger Exp $
 ********************************************************************/
public class XMLErrorHandler extends DefaultHandler {
        
    /** Version and date of last update of this class. */
    public static final String VERSION = "$Id: XMLErrorHandler.java,v 1.1 2005/09/03 10:58:18 marsenger Exp $";

    /********************************************************************
     * Called when SAX parser reports a warning.
     * Prints a formatted message on standard error output.
     * <P>
     * @param ex exception reporting the warning
     ********************************************************************/
    public void warning (SAXParseException ex) {
        System.err.println ("[Warning] "+
                            XMLUtils2.getLocationString (ex) + ": " +
                            ex.getMessage());
    }

    /********************************************************************
     * Called when SAX parser reports an error.
     * Prints a formatted message on standard error output.
     * <P>
     * @param ex exception reporting the error
     ********************************************************************/
    public void error (SAXParseException ex) {
        System.err.println ("[Error] "+
                            XMLUtils2.getLocationString (ex) + ": " +
                            ex.getMessage());
    }

    /********************************************************************
     * Called when SAX parser reports a fatal error.
     * Prints a formatted message on standard error output and re-thow
     * SAX exception.
     * <P>
     * @param ex exception reporting a fatal error.
     * @throws SAXException always
     ********************************************************************/
    public void fatalError (SAXParseException ex) throws SAXException {
        System.err.println ("[Fatal Error] "+
                            XMLUtils2.getLocationString (ex) + ": " +
                            ex.getMessage());
        throw ex;
    }

}

