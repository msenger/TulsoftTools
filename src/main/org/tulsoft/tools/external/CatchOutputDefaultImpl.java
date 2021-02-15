// CatchOutputDefaultImpl.java
//
// Created: November 1999
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

/** A default implementation of the interface CatchOutput.
 *  It listens to a standard stream of an external process (usually).
 *  It is often used together with an {@link Executor}. Remember that
 *  because it implements CatchOutput it represents a separate thread,
 *  which means that the main job is done in the method <tt>run</tt>.
 * <P>
 *  This class is a convenient parent to similar threads.
 *  The subclasses usually need to overwrite just the <tt>run</tt>
 *  method.
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: CatchOutputDefaultImpl.java,v 1.1 2005/09/03 07:32:13 marsenger Exp $
 * @see Executor
 */

public class CatchOutputDefaultImpl
    implements CatchOutput {

    /** Version and date of the last update.
     */
    public static final String VERSION = "$Id: CatchOutputDefaultImpl.java,v 1.1 2005/09/03 07:32:13 marsenger Exp $";

    protected String errorMessage = null;             // run() puts here an error msg
    protected BufferedReader processOutput;           // this stream is being read
    protected StringBuffer buf = new StringBuffer();  // here run() stores data

    /*********************************************************************
     * No-arg constructor.
     ********************************************************************/
    public CatchOutputDefaultImpl() {
    }

    /*********************************************************************
     * A convenient way how to specify what stream should be read.
     * The same can be, however, achieved by calling {@link #setInputStream}.
     ********************************************************************/
    public CatchOutputDefaultImpl (InputStream stream) {
        setInputStream (stream);
    }


    /*********************************************************************
     * It specifies a stream which will be read by the {@link #run} method.
     * Use this method, or use a
     * {@link #CatchOutputDefaultImpl(InputStream) constructor} for the
     * same purpose.
     * <P>
     * @param stream data stream to be read
     ********************************************************************/
    public void setInputStream (InputStream stream) {
        processOutput = new BufferedReader (
                            new InputStreamReader (stream));
    }

    /*********************************************************************
     * Read the input stream and do something with the data.
     * Is supposed to be overwriten by a subclass to do more meaningful
     * thing. This implementation just reads data and stores them in big
     * string buffer. To get the data one can call {@link #getData}.
     * Any errors during reading are retrievable by calling
     * {@link #getErrorMessage}.
     *
     * This method can be called only after an input data steam was set.
     * Which may have been done in a
     * {@link #CatchOutputDefaultImpl(InputStream) constructor}, or by
     * {@link #setInputStream} method.
     ********************************************************************/
    public void run() {
        if (! checkInput()) return;
	String line;
	try {
	    while ((line = processOutput.readLine()) != null) {
		buf.append (line);
		buf.append ("\n");
	    }
	} catch (IOException e) {
	    errorMessage = "Error by reading application output: " + e.toString();
	}
    }

    /*********************************************************************
     * It returns data which were read from the stream specified by
     * method {@link #setInputStream}. However, because the reading of
     * the data is done by the <tt>run</tt> method which can be overwritten
     * and therefore with unknown behaviour, this method may return an
     * empty string even when some data were read.
     * <P>
     * @return data read and stored by the <tt>run</tt> method
     ********************************************************************/
    public String getData() {
	return new String (buf);
    }

    /*********************************************************************
     * It returns what was stored by the <tt>run</tt> method as error
     * messages. It returns null if there was no errors during reading.
     ********************************************************************/
    public String getErrorMessage() {
	return errorMessage;
    }



    /*********************************************************************
     * checkInput()
     *    If we do not know what to catch, it seems to be a developer
     *    mistake. Report it to her...
     ********************************************************************/
    static final String PRG_ERROR_MSG =
        "Input is missing (for programmers: use setInputStream())";

    protected boolean checkInput() {
        if (processOutput == null) {
            errorMessage = PRG_ERROR_MSG;
            return false;
	}
        return true;
    }


}

