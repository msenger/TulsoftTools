// TestBaseCmdLine.java
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

import org.tulsoft.tools.BaseCmdLine;
import java.io.*;

/** 
 * A class for testing BaseCmdLine class. Just start it and follow the
 * usage report. <p>
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: TestBaseCmdLine.java,v 1.1 2005/09/03 13:33:19 marsenger Exp $
 */
public class TestBaseCmdLine {

    static boolean useAsOption = false;

    public TestBaseCmdLine() {
    }

    public static void main (String args[]) {
        if (args.length == 0) {
            System.err.println
		("Usage:\n"
		 + "\tjava -Dn=<name> [-Dl=<length>] TestBaseCmdLine <some arguments>\n"
		 + "where\n"
		 + "\t<name> is one of the command-line arguments, and\n"
		 + "\t<length> is a number of subsequent arguments to be taken."
		 );
            System.exit(1);
        }
        String name = System.getProperty ("n");
        String s = System.getProperty ("l");
	int len = -1;
        if (s == null) {
            useAsOption = true;
	} else {
            try {
                len = (new Integer(s)).intValue();
            } catch (NumberFormatException _ex) {}
            if (len < 0)
                len = 1;
	}
	if (name != null) {
	    System.out.println ("Case sensitive search:");
	    printIt (new BaseCmdLine (args), name, len);
	    System.out.println ("Case insensitive search:");
	    printIt (new BaseCmdLine (args, true), name, len);
	    System.out.println ("Pure argument list cleaning:");
	    printRest (new BaseCmdLine (args, new String[] { name },
					new int[] { useAsOption ? 0 : len }));
	    System.out.println ("All arguments:");
	}
	printRest (new BaseCmdLine (args));
    }

    private static void printIt (BaseCmdLine basecmdline, String name, int length) {
        System.out.println ("\thasParam(\"" + name + "\") =>\t" + basecmdline.hasParam(name));
        if (useAsOption) {
            System.out.println("\thasOption(\"" + name + "\") =>\t" + basecmdline.hasOption(name));
	} else {
            if(length == 1) {
                System.out.println("\tgetParam(\"" + name + "\") =>\t" + basecmdline.getParam(name));
            } else {
                String as[] = basecmdline.getParam (name, length);
                System.out.println ("\tgetParam(\"" + name + "\"," + length + ") =>");
                for (int j = 0; j < as.length; j++)
                    System.out.println("\t\t\t" + as[j]);
            }
	}
    }

    private static void printRest (BaseCmdLine basecmdline) {
        System.out.println ("\tparams => ");
        for (int i = 0; i < basecmdline.params.length; i++)
            System.out.println ("\t" + (i+1) + ":\t" + basecmdline.params[i]);
    }

}
