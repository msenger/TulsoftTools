// InputStreamProvider
//
// Created: November 2000
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
import java.io.*;

/**
 * InputStreamProvider.java
 *
 * Provides abstract base class of active input stream. The data 
 * is written into the stream via connected output stream in a separate thread. 
 * The actual writeing must be implemented in a sub-class
 * 
 * Overwrites the read method to give proper error message if something
 * goes wrong already when writeing data into the output stream
 * 
 * Created: Thu Nov 23 21:08:30 2000
 *
 * @author <a href="mailto@ebi.ac.uk">Juha Muilu</a>
 * @version $Id: InputStreamProvider.java,v 1.2 2005/09/03 08:15:19 marsenger Exp $
 */

public abstract  class InputStreamProvider extends PipedInputStream  implements Runnable   {

    protected PipedOutputStream _pos;
    protected Thread thread;
    protected String errMessage = "";
    protected boolean error = false;
    protected java.lang.Object data = null;
    protected String format = null;
    
    
    public InputStreamProvider( ) throws IOException   {
	init();
    }

    public InputStreamProvider( java.lang.Object _obj) throws IOException {
	data = _obj;
	init();
    }

    public InputStreamProvider( java.lang.Object _obj, String _format ) throws IOException {
	data = _obj;
	format = _format;
	init();
    }

    /**
     * Constructor which do not start the thread... this is for
     * sub-classes which do not want default behaviour
     **/
    protected InputStreamProvider( int _FAKE ) throws IOException {
    }


    protected void init ( ) throws IOException { 
	_pos = new PipedOutputStream();
	connect( _pos);
	thread = new Thread( this);
	thread.start();
    }
    
    
    public abstract void write( OutputStream out) throws GException ;
    
    public void run() { 
	
	try { 
	    write( _pos);
	} catch ( GException ex) {
	    errMessage =  "InputStreamProvider: "+ex.getMessage();
	    error = true;
	}
    } 
    

    public String getErrorMessage() {
	return errMessage;
    }

    public int read () throws IOException { 
	if ( error ) throw new IOException(errMessage);
	return super.read();
    }

    public int read ( byte[] b) throws IOException { 
	if ( error ) throw new IOException(errMessage);
	return super.read(b);
    }

    public int read ( byte[] b, int off, int len) throws IOException { 
	if ( error ) throw new IOException(errMessage);
	return super.read(b, off, len);
    }

    protected void receive( int b) throws IOException { 
	//this may be only one test needed... other read methods may use the receive
	//anyway
	if ( error ) { 
	    throw new IOException("pipe is broken because of :"+errMessage); 
	    
	}
	super.receive(b);
    }

} // Streamer



