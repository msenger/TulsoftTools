// AxisCall.java
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

import org.tulsoft.shared.GException;

import org.apache.axis.AxisFault;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;

/**
 * A small wrapper around the Axis basic class for calling web
 * services. It also allows a simple profiling of the calls. <p>
 *
 * @author <A HREF="mailto:senger@ebi.ac.uk">Martin Senger</A>
 * @version $Id: AxisCall.java,v 1.2 2005/09/03 08:15:19 marsenger Exp $
 */

public class AxisCall {

    // the main beast
    protected Call call;

    // for calls profiling
    protected boolean profiling = false;
    protected StringBuffer profileBuf = new StringBuffer();
    protected long profilingTotal = 0;

    // for error messages
    protected URL target;

    /*************************************************************************
     * Usual constructor.
     *************************************************************************/
    public AxisCall (URL target)
	throws GException {
	this.target = target;
 	try {
 	    call = (Call) new Service().createCall();
	    call.setTargetEndpointAddress (target);
 	} catch (ServiceException e) {
 	    throw new GException (e.toString());
  	}
    }

    /*************************************************************************
     * A constructor that also sets timeout. It is convenient for constructs
     * where you do not keep the AxisCall instance, e.g.:
     * <pre>
     * new AxisCall (target, 0).doCall ("synchEcho",
     *                                  new Object[] { message });
     * <pre>
     *************************************************************************/
    public AxisCall (URL target, int timeout)
	throws GException {
	this (target);
	setTimeout (timeout);
    }

    /*************************************************************************
     * Make available the underlying (wrapped) Axis's Call object -
     * for things that are not supported by AxisCall class, such as
     * registering serializers.
     *************************************************************************/
    public Call getCall() {
	return call;
    }

    /*************************************************************************
     * Enable/disable profiling of calls.
     *************************************************************************/
    public void setProfiling (boolean enable) {
	profiling = enable;
    }

    /*************************************************************************
     * Return so-far collected profiling information,
     *************************************************************************/
    public String getProfile() {
	if (profileBuf.length() > 0) {
	    profileBuf.append ("\n");

	    // should be replaced by java.util.Formatter from Java 1.5
	    profileBuf.append (profilingTotal);
	    profileBuf.append ("\t");
	    profileBuf.append ("TOTAL\n");
// 	    profileBuf.append (Printf.format ("%7d\t%s\n",
// 					      new String[] {"" + profilingTotal, "TOTAL"}));
	    return new String (profileBuf);
	} else {
	    return "";
	}
    }

    /*************************************************************************
     * Set unlimited timeout.
     *************************************************************************/
    public void disableTimeout() {
	call.setTimeout (new Integer (0));
    }

    /*************************************************************************
     * Set timeout (unless it is negative).
     *************************************************************************/
    public void setTimeout (int timeout) {
	if (timeout >= 0)
	    call.setTimeout (new Integer (timeout));
    }

    /*************************************************************************
     * Call 'method' with 'parameters' and return its result. Method
     * can be given in several forms.
     *************************************************************************/
    public Object doCall (QName method, Object[] parameters)
	throws GException {
	try {
	    call.setOperationName (method);
	    if (profiling) {
		long start = System.currentTimeMillis();
		Object result = call.invoke (parameters);
		long time = System.currentTimeMillis() - start;
		profilingTotal += time;

		// should be replaced by java.util.Formatter from Java 1.5
		profileBuf.append (time);
		profileBuf.append ("\t");
		profileBuf.append (method.toString());
// 		profileBuf.append (Printf.format ("%7d\t%s\n",
// 						  new String[] { "" + time,
// 								 method.toString() }));
		return result;
	    } else {
		return call.invoke (parameters);
	    }

	} catch (AxisFault e) {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream (baos);
	    AxisUtils.formatFault (e, ps, target.toString(),
				   (call == null ? null : call.getOperationName()));
	    throw new GException (baos.toString());

	} catch (Exception e) {
	    throw new GException (e.toString());
 	}
    }

    public Object doCall (String method, Object[] parameters)
	throws GException {
	return doCall (new QName (method), parameters);
    }

    public Object doCall (String methodNamespace, String methodLocalName,
			  Object[] parameters)
	throws GException {
	return doCall (new QName (methodNamespace, methodLocalName), parameters);
    }

}
