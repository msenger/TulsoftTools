// GException.java
//
//    Created: February 2005
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

/** A general exception which can be used as a wrapper
 *  around other exceptions. Using this exception
 *  simplifies the code (but on the other hand it makes it less
 *  "type-safe" - which is usually not a problem when dealing
 *  with exceptions).
 *  <P>
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: GException.java,v 1.1 2005/09/03 07:32:13 marsenger Exp $
 */

public class GException extends Exception {

    /** @serial
     */
    private Throwable theOriginalException = null;

    public GException ()         { super(); }

    /******************************************************************************
     * A constructor specifying a reason of this exception.
     * @param s message/reason
     ******************************************************************************/
    public GException (String s) { super (s); }

    /******************************************************************************
     * A constructor used for storing another exception in GException.
     * It allows access to the original (wrapped) exception and its message.
     *
     * @param s message (or reason of)
     * @param theOriginalException
     ******************************************************************************/
    public GException (String s, Throwable theOriginalException) {
	super (s);
        this.theOriginalException = theOriginalException;
    }

    /******************************************************************************
     * Retrieve the original exception. 
     *
     * @return an original exception which was wrapped by this GException, or
     *         null if there was no original exception involved
     ******************************************************************************/
    public Throwable getOriginalException() {
        return theOriginalException;
    }

}

