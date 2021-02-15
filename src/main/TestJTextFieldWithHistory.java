// TestJTextFieldWithHistory.java
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

import org.tulsoft.tools.gui.JTextFieldWithHistory;
import org.tulsoft.tools.gui.SwingUtils;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;

/** 
 * A class for testing JTextFieldWithHistory class. Just start it
 * without any parameters, or with a parameter <tt>-help</tt>. <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: TestJTextFieldWithHistory.java,v 1.2 2005/09/18 09:25:41 marsenger Exp $
 */
public class TestJTextFieldWithHistory {

    public TestJTextFieldWithHistory() {
    }

    public static void main (String args[]) {
        if ( args.length == 1 && (args[0].equals ("-help") || args[0].equals ("-h")) ) {
            System.out.println
		("Usage:\n"
		 + "\tjava TestJTextFieldWithHistory [<name>,...]\n"
		 + "where\n"
		 + "\t<name> is an arbitrary name\n"
		 + "\t(number of names indicates how many TextField will be created)."
		 );
            System.exit(0);
        }

	String[] names;
	if (args.length > 0)
	    names = args;
	else
	    names = new String[] { "alltools2/testing" };

	JPanel panel = new JPanel();
	for (int i = 0; i < names.length; i++) {
	    panel.add (new JTextFieldWithHistory ("",
						  TestJTextFieldWithHistory.class, names[i]));
	}

  	JFrame frame = SwingUtils.createMainFrame (panel, "Testing JTextFieldWithHistory");
  	SwingUtils.showMainFrame (frame, new Dimension (300, 200));
    }

}
