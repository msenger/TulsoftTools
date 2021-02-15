// CatchOutput.java
//
// Created: December 1999
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

package org.tulsoft.tools.external;

import java.io.*;

/** An interface to be implemented by threads listening to the
 *  standard output stream of an external process.
 *
 * @see Executor
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: CatchOutput.java,v 1.1 2005/09/03 07:32:13 marsenger Exp $
 */

public interface CatchOutput extends Runnable {

    /*********************************************************************
     * It specifies a stream which will be read by the thread
     * implementing this interface.
     * <P>
     * Use this method, or use a constructor for the same purpose.
     * The data stream is coming (usually) from an external process, and
     * it will be read when the thread implementing this interface is
     * started (calling its <tt>run</tt> method).
     * <P>
     * @param stream data stream to be read
     ********************************************************************/
    public void setInputStream (InputStream stream);

    /*********************************************************************
     * It returns data which were read from the stream specified by
     * method {@link #setInputStream}. However, because the reading of
     * the data is done by the <tt>run</tt> method which can do with read
     * data whatever it wants, there may be no data to be returned at all.
     * <P>
     * @return data read and stored by the <tt>run</tt> method
     ********************************************************************/
    public String getData();

    /*********************************************************************
     * It returns what was stored by the <tt>run</tt> method as error
     * messages. It returns null if there was no errors during reading.
     ********************************************************************/
    public String getErrorMessage();

}

