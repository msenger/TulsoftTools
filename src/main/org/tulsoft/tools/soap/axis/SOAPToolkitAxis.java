// SOAPToolkitAxis.java
//
// Created: June 2005
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

import org.tulsoft.tools.soap.SOAPToolkit;
import org.tulsoft.shared.GException;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;

import javax.servlet.http.HttpServletRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Properties;
import java.util.NoSuchElementException;

/**
 * An Apache-Axis-specific implementation of the SOAPToolkit
 * interface. <p>
 *
 * It uses MessageContext to get to the actual parameters of the
 * requested Web Service. However, if the MessageContext does not
 * exist, it uses System properties as parameters. Because of that,
 * this toolkit can be used also outside any servlet engine (with some
 * exceptions regarding sessions). <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.org">Martin Senger</A>
 * @version $Id: SOAPToolkitAxis.java,v 1.3 2007/03/25 18:55:19 marsenger Exp $
 */

public class SOAPToolkitAxis
    implements SOAPToolkit {


    /**************************************************************************
     * Return name of service which was called by the current request.
     *************************************************************************/
    public String getServiceName() {
	MessageContext ctx = MessageContext.getCurrentContext();
	if (ctx == null)
	    return System.getProperty ("service.name");
	return ctx.getTargetService();
    }

    /**************************************************************************
     * Return a list containing the attribute names available within
     * the context of the invoked web service together with the global
     * context. <p>
     *
     * Or take all names of system properties (if this is not run
     * within any servlet engine).
     *************************************************************************/
    public String[] getAttributeNames() {
	MessageContext ctx = MessageContext.getCurrentContext();

	Vector<String> v = new Vector<String>();
	if (ctx == null) {
	    Properties props = System.getProperties();
	    for (Enumeration en = props.propertyNames(); en.hasMoreElements(); )
		v.addElement ((String)en.nextElement());
	} else {
	    for (Enumeration en = ctx.getAxisEngine().getOptions().keys(); en.hasMoreElements(); )
		v.addElement ((String)en.nextElement());
	    for (Enumeration en = ctx.getService().getOptions().keys(); en.hasMoreElements(); )
		v.addElement ((String)en.nextElement());
	}
	String[] results = new String [v.size()];
	v.copyInto (results);
	return results;
    }

    /**************************************************************************
     * Return a String containing the value of the named attribute, or
     * null if the attribute does not exist.
     *************************************************************************/
    public String getAttribute (String name) {
	MessageContext ctx = MessageContext.getCurrentContext();
	if (ctx == null)
	    return System.getProperty (name);
	Object value = ctx.getService().getOption (name);
	if (value == null)
	    value = ctx.getAxisEngine().getOption (name);
	if (value == null)
	    return null;
// 	if (value instanceof String)
// 	    return (String)value;
// 	else
	    return value.toString();
    }

    /**************************************************************************
     * Return an Enumeration of Strings containing the attribute names
     * of all objects bound to the underlying session which the
     * current request is part of. <p>
     *
     * The HttpSession of the surrounding servlet container is used
     * (because the Axis session object does not have similar method). <p>
     *
     * It returns an empty Enumeration object if not called within a
     * servlet context.
     *************************************************************************/
    public Enumeration getSessionAttributeNames() {
	MessageContext ctx = MessageContext.getCurrentContext();
	if (ctx == null)
	    return new Enumeration() {
		    public boolean hasMoreElements() { return false; };
		    public Object nextElement() { throw new NoSuchElementException(); }
		};
  	HttpServletRequest req = (HttpServletRequest)ctx.getProperty (HTTPConstants.MC_HTTP_SERVLETREQUEST);
	return req.getSession().getAttributeNames();
    }

    /**************************************************************************
     * Return the object bound with the specified name in this
     * session, or null if no object is bound under the name. <p>
     *
     * The AxisHttpSession from the current request is used.
     *************************************************************************/
    public Object getSessionAttribute (String name) {
	MessageContext ctx = MessageContext.getCurrentContext();
	if (ctx == null)
	    return null;
	return ctx.getSession().get (name);
    }

    /**************************************************************************
     * Return a string containing the unique identifier assigned to
     * this session. <p>
     *
     * The HttpSession of the surrounding servlet container is used
     * (because the Axis session object does not have similar method). <p>
     *
     * It returns null if called outside of a servlet engine.
     *************************************************************************/
    public String getSessionId() {
	MessageContext ctx = MessageContext.getCurrentContext();
	if (ctx == null)
	    return null;
  	HttpServletRequest req = (HttpServletRequest)ctx.getProperty (HTTPConstants.MC_HTTP_SERVLETREQUEST);
	return req.getSession().getId();
    }

    /**************************************************************************
     * Return a full URL pointing to a web service representing an
     * analysis given by 'serviceName' (but it does not check if such
     * service actually does exist).
     *************************************************************************/
    public URL getServiceURL (String serviceName)
	throws GException {

	MessageContext ctx = MessageContext.getCurrentContext();
	if (ctx == null)
	    return null;
	HttpServletRequest req = (HttpServletRequest)ctx.getProperty (HTTPConstants.MC_HTTP_SERVLETREQUEST);
  	String url = new String (req.getRequestURL());
	int pos = url.lastIndexOf ("/");
	try {
	    if (pos > -1)
		return new URL (url.substring (0, pos+1) + serviceName);
	    else
		return new URL (url + "/" + serviceName);
	} catch (MalformedURLException e) {
	    throw new GException ("'getRequestURL' returned bad URL: " + e.getMessage());
	}
    }

    /**************************************************************************
     * Return a WSDL description of a service 'serviceName', or null
     * if the service does not exist.
     *************************************************************************/
    public String getServiceWSDL (String serviceName)
	throws GException {
	if (wsdl == null) {
	    MessageContext ctx = MessageContext.getCurrentContext();
	    if (ctx == null)
		return null;
	    wsdl = ctx.getService().getServiceDescription().getWSDLFile();
	}
	return wsdl;
    }

    // cache
    private String wsdl = null;
}
