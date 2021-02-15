// SOAPToolkit.java
//
// Created: June 2002
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

package org.tulsoft.tools.soap;

import org.tulsoft.shared.GException;

import java.net.URL;
import java.util.Enumeration;

/**
 * An interface defining what can be provided by a SOAP toolkit (by
 * toolkit I mean server-side software surrounding a web service, and
 * example would be Apache Axis toolkit). I have put here only basic
 * functionality (like access to the parameters from the deployment
 * descriptors) - in fact I put here only what I needed.  Having these
 * methods separated in this interface, I can load a different
 * implementation without changing my code (I hope). Therefore, this
 * is the poor man's way how to achieve portability in time when all
 * Java SOAP toolkits are still very different. <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail,com">Martin Senger</A>
 * @version $Id: SOAPToolkit.java,v 1.2 2007/03/25 18:55:19 marsenger Exp $
 */

public interface SOAPToolkit {

    /**
     * Return a name of service which was called by the current
     * request.
     */
    String getServiceName();

    /**
     * Return a list of attribute names available within the context
     * of the invoked web service. These attributes have nothing to do
     * with the (SOAP-)request - they come from the deployment
     * descriptor of the web service (so they are not sent by
     * clients). <p>
     *
     * The implementation should try to include here both kinds of
     * attributes - those who are specific to the current context,
     * meaning specific to the servuice currently being invoked, and
     * those who are global to all services. <p>
     *
     * Use the {@link #getAttribute(String)} method with an attribute name
     * to get the value of an attribute.
     */
    String[] getAttributeNames();

    /**
     * Return a value of the named attribute, or null if the attribute
     * does not exist. Again, this attribute has nothing to do with
     * the (SOAP-)requests, it is rather related to the deployment of
     * a web service. <p>
     *
     * The implementation should try to find the attribute both in the
     * global space (available for all deployed services), and in the
     * current service space (giving the priority to the one which is
     * service-specific). <p>
     *
     * @see #getAttributeNames
     */
    String getAttribute (String name);

    /**
     * Returns an Enumeration of Strings containing the attribute names of all
     * objects bound to the underlying session which the current request is
     * part of.
     */
    Enumeration getSessionAttributeNames();

    /**
     * Returns the object bound with the specified name in this session,
     * or null if no object is bound under the name.
     * @see #getSessionAttributeNames
     */
    Object getSessionAttribute (String name);

    /**
     * Returns a string containing the unique identifier assigned to this session.
     */
    String getSessionId();

    /**
     * Return a full URL pointing to a deployed service, or null if
     * the service does not exist.  The 'serviceName' is not a full
     * URL of a web service but only its real name - because this
     * method is meant to work locally, on the same server where also
     * this toolkit resides.
     *<P>
     * @throws GException if the request cannot be satisfied
     */
    URL getServiceURL (String serviceName)
	throws GException;

    /**
     * Return a WSDL description of a service 'serviceName', or null if
     * the service does not exist.  The 'serviceName' is not a full
     * URL of a web service but only its real name - because this
     * method is meant to work locally, on the same server where also
     * this toolkit resides.
     *<P>
     * @throws GException if the request cannot be satisfied
     */
    String getServiceWSDL (String serviceName)
	throws GException;

}
