// AxisUtils.java
//
// Created: July 2002
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

package org.tulsoft.tools.soap.axis;

import org.tulsoft.shared.GException;

import org.apache.axis.AxisFault;
import javax.xml.namespace.QName;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Vector;

/**
 * This is a set of several utility methods which may be useful for
 * writing client code. The methods are (mostly) specific for Apache
 * Axis framework. <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: AxisUtils.java,v 1.3 2007/03/25 18:55:19 marsenger Exp $
 */

public abstract class AxisUtils {

    /*************************************************************************
     * Format an exception.
     *************************************************************************/
    public static void formatFault (AxisFault e, PrintStream out) {
	formatFault (e, out, null, null);
    }

    /*************************************************************************
     * Format an exception.
     *************************************************************************/
    public static String formatFault (AxisFault e, String endpoint, QName method) {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	formatFault (e, new PrintStream (baos), endpoint, method);
	return baos.toString();
    }

    /*************************************************************************
     * Format an exception.
     *************************************************************************/
    public static void formatFault (AxisFault e, PrintStream out,
				    String endpoint, QName method) {

	out.println ("===ERROR===");
	out.println ("Fault details:");
	// for some obvious errors I do not print all details (with a lenghty trace stack)
	String faultString = e.getFaultString();
	if ( (! faultString.startsWith ("java.net.ConnectException")) &&
             (faultString.indexOf ("Could not find class for the service named:") == -1)
	     ) {
	    org.w3c.dom.Element[] details = e.getFaultDetails();
	    for (int i = 0; i < details.length; i++) {
		String s = details[i].toString().replaceAll ("&lt;", "<");
		s = s.replaceAll ("&gt;", ">");
		out.println (s);
	    }
	}
	out.println ("Fault string: " + faultString);
	out.println ("Fault code:   " + e.getFaultCode());
	out.println ("Fault actor:  " + e.getFaultActor());
	if (endpoint != null || method != null)
	    out.println ("When calling:");
	if (endpoint != null)
	    out.println ("\t" + endpoint);
	if (method != null)
	    out.println ("\t" + method);
	out.println ("===========");
    }

    // TBD:
// public void printSOAPException(SOAPException x)
// {
//  Throwable cause = x.getCause();
//  if(cause != null && cause instanceof AxisFault)
//    printAxisFault((AxisFault)cause);
//  else if(cause != null)
//    System.out.println(x.getClass().getName()
//                      + " exception chained: "
//                      + x.getMessage());
//  else
//    System.out.println("SOAP exception: " + x.getMessage());
// }

// public void printAxisFault(AxisFault x)
// {
//  // tested with Axis 1.1 & Axis 1.2 alpha
//  if(x.getFaultCode().equals(new QName("http://xml.apache.org/axis/","HTTP")))
//  {
//    Element e =
//       x.lookupFaultDetail(
//         new QName("http://xml.apache.org/axis/","HttpErrorCode"));
//    if(null != e)
//    {
//       e.normalize();
//       String httpErrorCode = e.getFirstChild().getNodeValue().trim();
//       if(httpErrorCode.equals("407"))
//          System.out.println("Proxy password incorrect");
//       else if(httpErrorCode.equals("502") || httpErrorCode.equals("504"))
//          System.out.println("Proxy cannot find the server");
//       else if(httpErrorCode.equals("500"))
//          System.out.println("Proxy or server unavailable");
//       else if(httpErrorCode.equals("404"))
//          System.out.println("No Web service (404 File Not Found)");
//       else
//          System.out.println(x.getFaultString());
//    }
//    else
//       System.out.println("Network error: " + x.getFaultString());
//  }
//  else if(x.getFaultCode().equals(
//           new QName("http://schemas.xmlsoap.org/soap/envelope/",
//                     "Server.userException")))
//   System.out.println("Most likely a net error: " + x.getFaultString());
//  else
//   System.out.println("SOAP Fault: " + x.getFaultCode() + ", " 
//                     + x.getFaultString());
// }

    /*************************************************************************
     * Convert 'ArrayList of ArrayLists' (valye) to 'byte[][]'.
     * Because this is what Axis returns when the server sends 'byte[][]'.
     *
     * 18/07/2002: This seems to be true only if the value.length == 1,
     *             otherwise it returns it normally as byte[][] (no ArrayList)
     *************************************************************************/
    public static byte[][] aa2bb (Object value)
	throws GException {

        if (value instanceof byte[][])
	    return (byte[][])value;

	if (! (value instanceof ArrayList))
	    throw new GException
		("aa2bb: The input is not of type ArrayList (but '" + value.getClass().getName() + "'.");

	Object[] list = ((ArrayList)value).toArray();
	if (list.length == 0)   // can happen?
	    return new byte[][] { new byte[] {} };

	if (! (list[0] instanceof ArrayList))
	    throw new GException
		("aa2bb: The nested element is of a wrong type '" + list[0].getClass().getName() + "'.");

	Vector<byte[]> v = new Vector<byte[]>();
	for (int i = 0; i < list.length; i++) {
	    Object[] list2 = ((ArrayList)list[i]).toArray();
	    if (list2.length > 0 && (list2[0] instanceof Byte)) {
		byte[] bytes = new byte [list2.length];
		for (int j = 0; j < list2.length; j++)
		    bytes[j] = ((Byte)list2[j]).byteValue();
		v.addElement (bytes);
	    } else {
		throw new GException
		    ("aa2bb: The nested element is of an unexpected type '" + list2[0].getClass().getName());
	    }
	}
	byte[][] results = new byte[v.size()][];
	v.copyInto (results);
	return results;
    }




}
