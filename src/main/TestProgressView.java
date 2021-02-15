// TestProgressView.java
//
// Created: September 2005
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

import org.tulsoft.tools.gui.ProgressView;
import org.tulsoft.tools.BaseCmdLine;

/** 
 * A class for testing ProgressView class. Just start it
 * without any parameters, or with a parameter <tt>-help</tt>. <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: TestProgressView.java,v 1.1 2005/09/18 13:41:34 marsenger Exp $
 */
public class TestProgressView {

    public TestProgressView() {
    }

    public static void main (String args[]) {
	BaseCmdLine cmd = new BaseCmdLine (args, true);
	if (cmd.hasOption ("-h") || cmd.hasOption ("-help")) {
            System.out.println
		("Usage:\n"
		 + "\tjava TestProgressView [-s <number-of-steps>] [-t <millis-for-one-step>]"
		 );
            System.exit(0);
        }

	int steps;
	try {
	    steps = new Integer (cmd.getParam ("-s")).intValue();
	} catch (Throwable e) {
	    steps = 10;
	}
	if (steps < 0) steps = 10;
	int time;
	try {
	    time = new Integer (cmd.getParam ("-t")).intValue();
	} catch (Throwable e) {
	    time = 1000;
	}
	if (time < 0) time = 1000;

	// loading monitor
	ProgressView.monitor = new ProgressView (steps);
	ProgressView.monitor.show ("Welcome to the Progress View");

	// monitor
	for (int i = 0; i < steps; i++) {
	    ProgressView.monitor.setTextAndAdd ("Step: " + (i+1));
	    try { Thread.sleep (time); } catch (Exception e) {}
	}

	// destroy monitor
	ProgressView.monitor.destroy();
	System.exit (0);
    }
}
