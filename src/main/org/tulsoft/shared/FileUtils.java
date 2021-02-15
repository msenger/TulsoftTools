// FileUtils.java
//
// Created: February 2000
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

import java.io.*;
import java.net.*;
import java.util.zip.*;
import java.util.*;

/** An abstract class containing various utilities dealing with files,
 *  directories, and compressed files.
 *  <P>
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: FileUtils.java,v 1.3 2007/03/25 18:55:17 marsenger Exp $
 */

public abstract class FileUtils {

    /********************************************************************
     * Attach a path to a file name, using platform-correct file separator.
     * If the file name seems to have already a path attached, it is
     * returned unchanged. Note that 'path' is not a colon-separated path in 
     * the sense of PATH and CLASSPATH; it is simply a directory<P>. Also,
     * there is no checking of whether the named file actually exists. <p>
     *
     * @param fileName a file name (which may contain already a path)
     * @param path a directory for 'fileName'
     * @return 'path' and 'fileName' joined together
     ********************************************************************/
    public static String resolvePath (String fileName, String path) {

	// do not resolve it, if it has path attached already
        if ((new File (fileName)).isAbsolute()) return fileName;
        String fileSep = System.getProperty ("file.separator");

	// resolve it...
        if (UUtils.isEmpty (path)) return fileName;
        if (path.endsWith (fileSep))
            return path + fileName;
        else
           return path + fileSep + fileName;
    }

    /********************************************************************
     * Find given file in directories defined in the environment variable
     * CLASSPATH (but excluding jar files).
     * If a file is found return the same file name with a path
     * attached.
     *<P>
     * The file we are looking for can also be a directory already
     * included as the last part of one of the directories defined in the
     * CLASSPATH.
     * <P>
     * @param fileName a file name (which may contain already a path)
     *                 to be found
     * @return a full file name including a path - if the file was found
     *         in the CLASSPATH, or if the fileName already had a path
     *         attached
     *
     * @throws GException if a fileName has a path attached but the
     *         file does not exist, or if the fileName was not found in
     *         any of the directories defined in CLASSPATH
     *
     * @see #resolvePathInJarFilesFromClassPath
     ********************************************************************/
    public static String resolvePathFromClassPath (String fileName)
	throws GException {

	// the given file can have already a path attached...
        File file = new File (fileName);
        if (file.isAbsolute()) {
            if (file.exists()) return fileName;
            throw new GException ("File " + fileName + " does not exist.");
	}

	// ...or we look for it in directories defined in CLASSPATH
        String classPath = System.getProperty ("java.class.path");
        String pathSep = System.getProperty ("path.separator");
        String fileSep = System.getProperty ("file.separator");
        String[] dirs = classPath.split ("\\Q" + pathSep + "\\E");
        String resultName;
        for (int i = 0; i < dirs.length; i++) {
            if (UUtils.isEmpty (dirs[i])) continue;

	    // the 'fileName' may be already in the dirs[i] - as its last part,
	    // in such case we will return just 'dirs[i]' but to find it
	    // is a bit combinatorial because of possible pending file separator...
	    if (dirs[i].endsWith (fileName) ||
		(dirs[i] + fileSep).endsWith (fileName) ||
		dirs[i].endsWith (fileName + fileSep)) {
		resultName = dirs[i];

	    } else {
		resultName = dirs[i] +
		    (dirs[i].endsWith (fileSep) ? "" : fileSep) +
		    fileName;
	    }

            if ((new File (resultName)).exists()) return resultName;
	}
        throw new GException ("File " + fileName + " does not exist.");
    }

    /********************************************************************
     * Find given fileName in jar files defined in the  CLASSPATH variable.
     * If a file is found return the name of the jar file.
     * <P>
     * If 'perhapsDirectory' is true look also for jar entries starting
     * with 'fileNmae' followed by "/".
     * <P>
     * @param fileName a file name to be found
     * @param perhapsDirectory if true the 'fileName' is also considered
     *        as a directory name in the jar file's entries
     * @return a jar file containing 'fileName' or at least one entry
     *         starting with 'fileName'
     *
     * @throws GException if the fileName could not be located in any
     *         jar file defined in the CLASSPATH
     *
     * @see #resolvePathFromClassPath(String)
     ********************************************************************/
    public static File resolvePathInJarFilesFromClassPath (String fileName,
							   boolean perhapsDirectory)
	throws GException {

        String classPath = System.getProperty ("java.class.path");
        String pathSep = System.getProperty ("path.separator");
        String[] dirs = classPath.split ("\\Q" + pathSep + "\\E");
	File jarFile;
        for (int i = 0; i < dirs.length; i++) {

	    // dealing only with the non-empty and existing .jar files
            if (UUtils.isEmpty (dirs[i]) || ! isJarFile (dirs[i])) continue;
	    jarFile = new File (dirs[i]);
	    if (! jarFile.exists()) continue;
	    if (existsInJarFile (jarFile, fileName, perhapsDirectory))
		return jarFile;
	}
	throw new GException ("File " + fileName + " was not found.");
    }

    /********************************************************************
     * Return true if 'fileName' exists as an entry in the 'jarFile'.
     * If 'perhapsDirectory' is true look also for jar entries starting
     * with 'fileNmae' followed by "/".
     * <P>
     * @param jarFile a compressed file where to look for the 'fileName'
     * @param fileName a file name to be looked for
     * @param perhapsDirectory if true the 'fileName' is also considered
     *        as a directory name in the jar file's entries
     * @return true if 'fileName' found in 'jarFile'
     ********************************************************************/
    public static boolean existsInJarFile (File jarFile, String fileName,
					   boolean perhapsDirectory) {
	try {
	    ZipFile zf = new ZipFile (jarFile);
	    if (zf.getEntry (fileName) != null) return true;
	    if (perhapsDirectory) {
		if (! fileName.endsWith ("/")) fileName = fileName + "/";
		String name;
		for (Enumeration en = zf.entries(); en.hasMoreElements(); ) {
		    name = ((ZipEntry)en.nextElement()).getName();
		    if (name.startsWith (fileName)) return true;
		}
	    }
	} catch (Exception e) {
	}
	return false;
    }

    /********************************************************************
     * Return 'true' if the given file appears to be a compressed file
     * (with either .jar or .zip extension).
     * <P>
     * @param fileName a file being investigated
     * @return true if the 'fileName' could be a .jar or .zip file
     ********************************************************************/
    public static boolean isJarFile (String fileName) {
	File f = new File (fileName);
	String lastName = f.getName();
	if (UUtils.isEmpty (f.getName())) return false;
	String fileExt = getExtension (f).toLowerCase();
	return (fileExt.equals ("jar") || fileExt.equals ("zip"));
    }

    /********************************************************************
     * Return the extension portion of the file's name.
     * The extension is everything after last dot unless this dot is
     * before any path separator (path separator depends on the
     * platform, specifically is taken from the Java System property
     * "file.separator").
     * <P>
     * @param f is a file whose file extension is being looked for
     * @return a file extension found in the end of 'f', or an empty
     *         string if there is no extension
     ********************************************************************/
    public static String getExtension (File f) {
	if (f != null) {
	    String filename = f.getName();
	    int pos = filename.lastIndexOf ('.');
	    if (pos > 0 && pos < filename.length() - 1) {
		return filename.substring (pos + 1);
	    };
	}
	return "";
    }

    /********************************************************************
     * Return the extension portion of the file's name which is given as
     * a String.
     * <P>
     * @param f is a file whose file extension is being looked for
     * @return a file extension found in the end of 'f', or an empty
     *         string if there is no extension
     * @see #getExtension(File)
     ********************************************************************/
    public static String getExtension (String f) {
	return getExtension (new File (f));
    }

    /*************************************************************************
     * Reads file and returns its contents as a string. The file is supposed
     * to be a text file (it is read line by line - which also implicates that
     * if the last line of input does not end with a newliner, it is added).
     * <P>
     * @param fName a filename of a file to be read
     * @return contents of the <tt>fName</tt> file, or an empty string if
     *         something goes wrong
     * @see #getFile(String,boolean) getFile(String,boolean)
     *************************************************************************/
    public static String getFile (String fName) {
        try {
	    return getFile (fName, true);
	} catch (java.io.IOException e) {
	    return "";   // should not come here
	} catch (GException e) {
	    return "";   // should not come here either
	}
    }

    /******************************************************************************
     * Reads file and returns its contents as a string, and decides whether to raise
     * an exception or not.
     * <P>
     * The file is supposed
     * to be a text file (it is read line byte line - which also implicates that
     * if the last line of input does not end with a newliner, it is added).
     * <P>
     * Do not raise an exception if <tt>silent</tt> is true' - just return an empty
     * string.
     * <P>
     * @param fName a filename of a file to be read
     * @return contents of the <tt>fName</tt> file, or an empty string if
     *         something goes wrong  and <tt>silent</tt> is true
     * @throws java.io.IOException if <tt>silent</tt> is false and an I/O exception
     *        occured
     * @throws GException if <tt>silent</tt> is false and some serious error occured
     *        (such as out-of-memory error)
     * @see #getFile(String) getFile(String)
     ******************************************************************************/
    public static String getFile (String fName, boolean silent)
  	throws java.io.IOException, GException {
	StringBuffer contents = new StringBuffer();
	String line;
	BufferedReader data = null;
	try {
	    data = new BufferedReader
		(new InputStreamReader (new FileInputStream (fName)));
	    while ((line = data.readLine()) != null) {
		contents.append (line);
		contents.append ("\n");
	    }
	} catch (IOException e) {
	    if (!silent)
		throw (java.io.IOException)e.fillInStackTrace();
  	} catch (Error e) {     // be prepare for "out-of-memory" error
  	    if (!silent)
                  throw new GException ("Serious error. " + e.toString(), e);

	} finally {
	    try {
		if (data != null)
		    data.close();
	    } catch (IOException e) {
	    }
	}

	return new String (contents);
    }

    /******************************************************************************
     * Read a file and return its contents as an array of bytes.
     * <P>
     * @param fName a filename of a file to be read
     * @return contents of the <tt>fName</tt> file
     * @throws java.io.IOException if an I/O exception occured
     * @throws GException if some serious error occured
     *        (such as out-of-memory error)
     * @see #getFile(String,boolean) getFile
     * @see #getBinaryFile(InputStream) getBinaryFile(InputStream)
     ******************************************************************************/
    public static byte[] getBinaryFile (String fName)
	throws java.io.IOException, GException {
	return getBinaryFile (new FileInputStream (fName));
    }

    /******************************************************************************
     * Read the whole input stream  and return it as an array of bytes.
     * <P>
     * @param in an input stream to be read
     * @return contents of the input stream
     * @throws java.io.IOException if an I/O exception occured
     * @throws GException if some serious error occured
     *        (such as out-of-memory error)
     * @see #getBinaryFile(String) getBinaryFile(String)
     ******************************************************************************/
    public static byte[] getBinaryFile (InputStream in)
	throws java.io.IOException, GException {

        if (in == null) return new byte[0];
        ByteArrayOutputStream bout = new ByteArrayOutputStream (1024);
       
	try {
	    byte[] buffer = new byte[256];
	    int bytesRead;

	    while ((bytesRead = in.read (buffer)) != -1) {
                bout.write (buffer, 0, bytesRead);
	    }
	    bout.close();
            return bout.toByteArray();

	} catch (Throwable e) {     // be prepare for "out-of-memory" error
	    throw new GException ("Serious error. " + e.toString(), e);

	} finally {
	    try {
		in.close();
	    } catch (IOException e) {
	    }
	}
    }

    /*************************************************************************
     * Get data specified by a URL.
     * <P>
     * @param url a URL pointing to a file that is to be read
     * @return contents of the file at the URL
     * @throws java.io.IOException if an I/O exception occured
     * @throws GException if some serious error occured
     *        (such as out-of-memory error)
     * @see #getBinaryFile(InputStream) getBinaryFile(InputStream)
     *************************************************************************/
    public static byte[] getURLFile (URL url)
	throws java.io.IOException, GException {

	URLConnection uc = url.openConnection();
	uc.connect();
	return getBinaryFile (uc.getInputStream());
    }


    /*************************************************************************
     * Find and read a file and return its contents as an array of bytes.
     * It looks for file 'fileName' in the current directory, then in the
     * directories defined in the CLASSPATH, and then finally in all jar files
     * defined on the CLASSPATH.
     * <P>
     * @param fileName a filename of a file to be read
     * @return contents of the <tt>fileName</tt> file
     * @throws GException if anything is wrong :-)
     *
     * @see #getFile(String,boolean) getFile
     * @see #getBinaryFile(String) getBinaryFile
     *************************************************************************/
    public static byte[] findAndGetBinaryFile (String fileName)
	throws GException {

	String fullFileName = null;
	File jarFile = null;

	// first let's suppose that the 'fileName' is a normal file
	// and try to locate it in the current directory or somewhere on the
	// CLASSPATH
	try {
	    if ((new File (fileName)).exists())
		fullFileName = (new File (fileName)).getAbsolutePath();
	    else
		fullFileName = FileUtils.resolvePathFromClassPath (fileName);
	} catch (GException e) {

	    // bad luck, it was not found, now try to look into all jar files
	    // which are on the CLASSPATH
	    jarFile = FileUtils.resolvePathInJarFilesFromClassPath (fileName, false);
	}
	
	// now we have either resolved fullFilename or a jarFile,
	// so let's read one or the other
	if (jarFile == null)
	    try {
		return getBinaryFile (fullFileName);
	    } catch (IOException e) {
		throw new GException (e.toString());
	    }
	else
	    return getJarEntry (jarFile, fileName);
    }

    /******************************************************************************
     * Read the contents of an 'entry' from a compressed (jar, zip) file.
     * <P>
     * @param jarFile a compressed file
     * @param entryName an entry to be read and returned
     * @return contents of the <tt>entryName</tt> as stored in <tt>jarFile</tt>
     * @throws GException if anything is wrong :-)
     ******************************************************************************/
    public static byte[] getJarEntry (File jarFile, String entryName)
	throws GException {

	try {
	    // find if the wanted entry exists and how big it is
	    ZipFile zf = new ZipFile (jarFile);
	    ZipEntry ze = zf.getEntry (entryName);
	    if (ze == null)
		throw new GException ("'" + entryName + "' was not found in '" +
				      jarFile.getAbsolutePath() + "'.");
	    if (ze.isDirectory())
		throw new GException ("'" + entryName + "' found in '" +
				      jarFile.getAbsolutePath() + "' is a directory.");
	    int size = (int)ze.getSize();
	    if (size == 1)
		throw new GException ("'" + entryName + "' was found in '" +
				      jarFile.getAbsolutePath() +
				      "' but it reports unknown size.");
	    zf.close();

	    // extract the entry
	    ZipInputStream zis = new ZipInputStream
		(new BufferedInputStream
		    (new FileInputStream (jarFile)));

	    int rb = 0; int chunk = 0;
	    byte[] buf = new byte [size];  // here we read contents in

	    while ((ze = zis.getNextEntry()) != null) {
		if (! ze.getName().equals (entryName)) continue;

		// yes, that's the entry we want to read...
		while ((size - rb) > 0) {
		    chunk = zis.read (buf, rb, size - rb);
		    if (chunk == -1) break;
		    rb += chunk;
		}
		zf.close();
		return buf;
	    }
	    throw new GException ("'" + entryName + "' was not found in '" +
				  jarFile.getAbsolutePath() + "'");

	} catch (Exception e) {
	    throw new GException ("Reading from '" + jarFile.getAbsolutePath() +
				  "' failed.\n" + e.toString());
	}
    }

}
