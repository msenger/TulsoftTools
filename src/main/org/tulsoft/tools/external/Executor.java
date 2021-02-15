// Executor.java
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

import org.tulsoft.shared.GException;
import org.tulsoft.shared.UUtils;
import java.util.*;
import java.io.*;


/**
 * A class executing an external process and providing access to
 * its standard data streams. One instance can be used just for
 * one execution.
 * <P>
 *  Calling a constructor starts an external process. The process
 *  can be fed by data on its standard input. Subsequent call <tt>waitFor</tt>
 *  will block and wait until the process finishes.
 * <P>
 *  The user of this class can specify how to read standard streams
 *  of the external process. It can be read either fully by this class -
 *  in which case methods <tt>getStdout</tt> and <tt>getStderr</tt>
 *  (called after <tt>waitFor</tt>)
 *  will return the whole streams as single strings, or one or two
 *  user-defined threads can read them (see methods <tt>getStdoutStream</tt>
 *  and <tt>getStderrStream</tt>).
 * <P>
 * Here is an example how to start an external process, feed its
 * standard input by <tt>System.in</tt> stream, and print process standard
 * output to <tt>System.out</tt>:
 * <PRE>
 *    try {
 *
 *        // start an external process
 *        String[] envArr = new String[] {};   // no environment
 *        Executor executor = new Executor (params, envArr, System.in);
 *
 *        // reads its standard output by our own thread
 *        CatchOutput stdoutProcessor = new PrintOutput (executor.getStdoutStream());
 *        Thread stdoutThread = new Thread (stdoutProcessor);
 *        stdoutThread.start();
 *
 *        // wait here untill the external process ends
 *        int exitCode = executor.waitFor();
 *
 *        // any errors?
 *        if (exitCode != 0)
 *            System.err.println ("Exit code: " + exitCode);
 *        System.err.println (executor.getStderr());
 *
 *    } catch (GException e) {
 *        System.err.println ("ERROR: " + e.getMessage());
 *    } catch (InterruptedException e) {
 *    }
 *    ...
 *    ...
 *    class PrintOutput extends CatchOutputDefaultImpl {
 *
 *        public PrintOutput (InputStream stream) {
 *            super (stream);
 *        }
 *
 *        public void run() {
 *            int b;
 *            try {
 *                while ((b = processOutput.read()) != -1)
 *                    System.out.print ((char)b);
 *            } catch (IOException e) {
 *                errorMessage = "Error by reading process output: " + e.toString();
 *            }
 *        }
 *    }
 * </PRE>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: Executor.java,v 1.2 2007/03/25 18:55:18 marsenger Exp $
 * @see CatchOutputDefaultImpl
 */
public class Executor {

    /** Version and date of the last update.
     */
    public static final String VERSION = "$Id: Executor.java,v 1.2 2007/03/25 18:55:18 marsenger Exp $";

    CatchOutput stdoutProcessor;
    Thread stdoutThread = null;
    CatchOutput stderrProcessor = null;
    Thread stderrThread = null;
    SendInputThread   stdinThread  = null;

    Process child = null;
    boolean stdoutStreamAskedFor = false;
    boolean stderrStreamAskedFor = false;

    // a unique number of this execution
    protected long id = 0;

    /**********************************************************************
     * Start an external program. The program does not expect anything
     * on its standard input. Do not wait for the end of the started process.
     * <P>
     * @param cmdArr array of command line parameters, the first element
     *               contains the name of a program to be started
     * @param envArr array of environment variables (in format name=value)
     *               which are set before the program is started
     * @throws GException if the external process cannot be started
     *********************************************************************/
    public Executor (String[] cmdArr, String[] envArr)
	throws GException {

	// start an external process
        id = System.currentTimeMillis();
        child = startIt (cmdArr, envArr);
    }

    /**********************************************************************
     * Start an external program and feed its standard input from the given
     * string. It does not wait for the end of the started process.
     * <P>
     * @param cmdArr array of command line parameters, the first element
     *               contains the name of a program to be started
     * @param envArr array of environment variables (in format name=value)
     *               which are set before the program is started
     * @param toStdin a string sent to the standard input of the executed process,
     *                put here null if the process does not expect stdin
     * @throws GException if the external process cannot be started
     *********************************************************************/
    public Executor (String[] cmdArr, String[] envArr, String toStdin)
	throws GException {

	// start an external process
        id = System.currentTimeMillis();
        child = startIt (cmdArr, envArr);

	// feed process stdin (do it in a separate thread)
        if (toStdin != null) {
            stdinThread = new SendInputThread (child.getOutputStream(), toStdin);
            stdinThread.start();
	}
    }

    /**********************************************************************
     * Start an external program and feed its standard input from the given
     * input stream. It does not wait for the end of the started process.
     * <P>
     * @param cmdArr array of command line parameters, the first element
     *               contains the name of a program to be started
     * @param envArr array of environment variables (in format name=value)
     *               which are set before the program is started
     * @param toStdin a stream whose contents is sent to the standard input
     *                of the executed process
     * @throws GException if the external process cannot be started
     *********************************************************************/
    public Executor (String[] cmdArr, String[] envArr, InputStream toStdin)
	throws GException {

	// start an external process
        id = System.currentTimeMillis();
        child = startIt (cmdArr, envArr);

	// feed process stdin (do it in a separate thread)
        if (toStdin != null) {
            stdinThread = new SendInputThread (child.getOutputStream(), toStdin);
            stdinThread.start();
	}
    }

    /*********************************************************************
     * startIt()
     *   Start an external process,
     *********************************************************************/
    protected Process startIt (String[] cmdArr, String[] envArr)
	throws GException {
        try {
            return Runtime.getRuntime().exec (cmdArr, envArr);
        } catch (java.io.IOException e) {
            String app = "[empty]";
            if (cmdArr.length > 0) app = cmdArr[0];
            throw new GException ("Can't launch an application " + app + ".\n" + e.toString());
	}
    }

    /*********************************************************************
     *   Block until the external process (which was started in constructor)
     *   is finished. But before blocking, check if somebody asked for
     *   stdout and stderr output streams (which may have been done by
     *   calling methods {@link #getStdoutStream} or {@link #getStderrStream}.
     *   If not, read from them and store the data. Later data may be
     *   reclaimed using methods {@link #getStdout} and/or {@link #getStderr}.
     * <P>
     * @return exit code ot the finished program (see details in method
     *         <tt>java.lang.Process.waitFor()</tt>)
     * @throws GException is there were problems reported by the reading
     *         threads
     * @throws InterruptedException if waiting was interrupted from
     *         another thread; it is not wrapped in a general GException
     *         because usually it does not mean an error
     *********************************************************************/
    public int waitFor()
	throws GException, java.lang.InterruptedException {

	if (!stdoutStreamAskedFor) {
            stdoutProcessor = new CatchOutputDefaultImpl (child.getInputStream());
            stdoutThread = new Thread (stdoutProcessor);
	    stdoutThread.start();
	}

        if (!stderrStreamAskedFor) {
	    stderrProcessor = new CatchOutputDefaultImpl (child.getErrorStream());
	    stderrThread = new Thread (stderrProcessor);
	    stderrThread.start();
	}

	// wait until child ends
        int exitLauncherCode = -1;
	try {
            exitLauncherCode = child.waitFor();
            if (stdoutThread != null) stdoutThread.join();
            if (stderrThread != null) stderrThread.join();
            if (stdinThread  != null) stdinThread.join();
	} catch (java.lang.InterruptedException e) {
            if (stdoutThread != null) stdoutThread.join();
            if (stderrThread != null) stderrThread.join();
            if (stdinThread  != null) stdinThread.join();
            e.fillInStackTrace();
	}

	// there could have been problem during life-time of the threads
        String msg = null;
        if (stdoutThread != null) msg = stdoutProcessor.getErrorMessage();
        if (msg == null && stderrThread != null) msg = stderrProcessor.getErrorMessage();
        if (msg == null && stdinThread  != null) msg = stdinThread.getErrorMessage();
        if (msg == null) return exitLauncherCode;

        throw new GException ("Error during running external program:\n" + msg);
    }

    /*********************************************************************
     * Get a unique identification of this executor.
     *********************************************************************/
    public String getId() {
        return "" + id;
    }
    /*********************************************************************
     * Get a formatted identification of this executor. Which may not be
     * unique (but mostly it is) but is formatted with the time when
     * the process was started.
     *********************************************************************/
    public String getFormattedId() {
        return "Started at " + UUtils.formatDate (id);
    }

    /*********************************************************************
     *   Kill the running process.
     *********************************************************************/
    public void killIt() {
        if (child != null) child.destroy();
    }

    /*********************************************************************
     *   Get a stream containing standard output of the external program.
     *   If this method is called, it is supposed that who called will
     *   use it and read it (unless [s]he wants to hang up the program :-)).
     *********************************************************************/
    public InputStream getStdoutStream() {
        stdoutStreamAskedFor = true;
        return child.getInputStream();
    }

    /*********************************************************************
     *   Get a stream containing standard error steam of the external program.
     *   If this method is called, it is supposed that who called will
     *   use it and read it (unless [s]he wants to hang up the program :-)).
     *********************************************************************/
    public InputStream getStderrStream() {
        stderrStreamAskedFor = true;
        return child.getErrorStream();
    }

    /*********************************************************************
     *   Return an entire standard output of the external program.
     *   But only if you did not asked before for stdout stream
     *   (by method {@link #getStdoutStream}) in which case you are supposed to
     *   read it yourself, and this method retuns an empty string.
     *********************************************************************/
    public String getStdout() {
        if (stdoutThread == null) return "";
        return stdoutProcessor.getData();
    }

    /*********************************************************************
     *   Return an entire standard error output of the external program.
     *   But only if you did not asked before for stderr stream
     *   (by method {@link #getStderrStream}) in which case you are supposed to
     *   read it yourself, and this method retuns an empty string.
     *********************************************************************/
    public String getStderr() {
        if (stderrThread == null) return "";
        return stderrProcessor.getData();
    }

    /******************************************************************************
     * Create an array with environment variables (in format expected by the Process
     * class) from given java properties. It converts always all property names
     * to upper cases.
     * <P>
     * @param properties to be converted
     * @return an array with string elements in the form "key=value"
     ******************************************************************************/
    public static String[] propertiesToEnv (Properties properties) {
        return propertiesToEnv (properties, true);
    }

    /******************************************************************************
     * Create an array with environment variables (in format expected by the Process
     * class) from given java properties.
     * <P>
     * @param properties to be converted
     * @param toUpperCase if true convert names of properties to upper case
     * @return an array with string elements in the form "key=value"
     ******************************************************************************/
    public static String[] propertiesToEnv (Properties properties,
					    boolean toUpperCase) {
	String[] envp;
  	synchronized (properties) {
	    envp = new String [properties.size()];
	    String key;
	    int i = 0;
            if (toUpperCase) {
		for (Enumeration en = properties.propertyNames() ; en.hasMoreElements() ;) {
		    key = (String)en.nextElement();
		    envp [i++] = key.toUpperCase() + "=" + properties.getProperty (key);
		}
	    } else {
		for (Enumeration en = properties.propertyNames() ; en.hasMoreElements() ;) {
		    key = (String)en.nextElement();
		    envp [i++] = key + "=" + properties.getProperty (key);
		}
	    }
  	}
        return envp;
    }

    /**********************************************************************
     * 
     *   class SendInputThread
     *
     *     Create a separate thread in which write 'contents' into
     *     the 'stream'. The 'contents' can be a string or an input stream.
     *
     *********************************************************************/
    class SendInputThread extends Thread {

        String errorMessage = null;
        BufferedWriter processInput;
        String contents = null;
        InputStream inputStream = null;
        boolean fromStream = false;

	public SendInputThread (OutputStream stream, String contents) {
            this.contents = contents;
            fromStream = false;
            processInput = new BufferedWriter (
                               new OutputStreamWriter (stream));
	}

	public SendInputThread (OutputStream stream, InputStream inputStream) {
            this.inputStream = inputStream;
            fromStream = true;
            processInput = new BufferedWriter (
                               new OutputStreamWriter (stream));
	}

	public void run() {
            try {
                if (fromStream) {
		    int b;
		    while ((b = inputStream.read()) != -1)
			processInput.write (b);
		} else {
		    processInput.write (contents);
		}
		processInput.close();
	    } catch (IOException e) {
                errorMessage = "Error by feeding the application input: " + e.toString();
	    }
	}

	// call this after run() finishes to find if anything wrong occured during run()
	public String getErrorMessage() {
            return errorMessage;
	}
    }

}
