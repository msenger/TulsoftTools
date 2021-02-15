// ICreator.java
//
// Created: March 2001, Refresh: November 2006
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

package org.tulsoft.tools.loaders;

import org.tulsoft.shared.GException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * An instance creator.  It instantiates an object of a given class
 * with a given set of arguments. It plays the same role as a normal
 * constructor.  <P>
 *
 * For example, let's assume that a class A has a constructor with the
 * following signature:
 * <PRE>
 *    public A (String[] args, int size);
 * </PRE>
 * Than the following code creates an instance of such class (note that type 'int'
 * is replaced by its "object" type Integer), passing a string array
 * { "fish", "crab" } and a number 324 as constructor parameters:
 *
 * <PRE>
 * try {
 *    A a = (A)ICreator.createInstance ("y.favourite.A",
 *                                      new Class[] { String[].class, int.class },
 *                                      new Object[] { { "fish", "crab" }, new Integer (324) });
 * } catch (GException e) {
 *    System.err.println (e.getMessage());
 * }
 * </PRE>
 *
 * The example above loaded dynamically class "y.favourite.A" and
 * created an instance of this class.
 *
 * @author <A HREF="mailto:martin.senger@gmail.com">Martin Senger</A>
 * @version $Id: ICreator.java,v 1.3 2007/03/25 18:55:18 marsenger Exp $
 */
public final class ICreator {

    /******************************************************************************
     * Create a new instance of an object of class 'fromClass' with
     * given parameters. <p>
     *
     * @param fromClass is a class whose instance should be created
     * @param argsClasses is a list of Classes representing types of the constructor
     *        which will be used for the instance creation
     * @param argsValues are the arguments which will be used to feed the
     *        the instance
     * @return a newly created object
     * @throws GException is anything gets wrong
     ******************************************************************************/
    public static Object createInstance (Class fromClass,
					 Class[] argsClasses,
					 Object[] argsValues)
	throws GException {
	try {
	    Constructor constructor = fromClass.getConstructor (argsClasses);
	    return constructor.newInstance (argsValues);

	} catch (InstantiationException e) {
	    throw new GException ("InstantiationException: " + e.getMessage(), e);
	} catch (IllegalAccessException e) {
	    throw new GException ("IllegalAccessException: " + e.getMessage(), e);
	} catch (IllegalArgumentException e) {
	    throw new GException ("IllegalArgumentException: " + e.getMessage(), e);
	} catch (InvocationTargetException e) {
// 	    throw new GException ("InvocationTargetException: " + e.getTargetException().toString(), e.getTargetException());
	    e.getTargetException().printStackTrace();
	    throw new GException ("InvocationTargetException: " + e.getTargetException().toString(), e);
	} catch (NoSuchMethodException e) {
	    StringBuffer buf = new StringBuffer();
            for (int i = 0; i < argsClasses.length; i++)
		buf.append (argsClasses[i].getName() + " ");
	    throw new GException ("NoSuchMethodException: " +
				  fromClass.getName() + "(" + new String (buf) + ")", e);
//  	    throw new GException ("NoSuchMethodException: " + e.getMessage(), e);
	}
    }

    /******************************************************************************
     * Create a new instance of an object of class 'className' with given
     * parameters. The class is given by name and has to be first dynamically loaded.
     *<P>
     * @param className is a class name whose instance should be created
     * @param argsClasses is a list of Classes representing types of the constructor
     *        which will be used for the instance creation
     * @param argsValues are the arguments which will be used to feed the
     *        the instance
     * @return a newly created object
     * @throws GException is anything gets wrong
     ******************************************************************************/
    public static Object createInstance (String className,
					 Class[] argsClasses,
					 Object[] argsValues)
	throws GException {

	try {
	    Class classOf =  Class.forName (className);
	    return createInstance (classOf, argsClasses, argsValues);

	} catch (GException e) {
	    throw e;
	} catch (Exception e) {
	    throw new GException ("Can't load class '" + className + "'. " + e.toString());
	}
    }

    /******************************************************************************
     * Create a new instance of an object of class 'fromClass' using
     * an empty constructor. <p>
     *
     * @param fromClass is a class whose instance should be created
     * @return a newly created object
     * @throws GException is anything gets wrong
     ******************************************************************************/
    public static Object createInstance (Class fromClass)
	throws GException {
	return createInstance (fromClass, new Class[] {}, new Object[] {});
    }

    /******************************************************************************
     * Create a new instance of an object of class 'className' using
     * an empty constructor. The class is given by name and has to be
     * first dynamically loaded. <p>
     *
     * @param className is a class name whose instance should be created
     * @return a newly created object
     * @throws GException is anything gets wrong
     ******************************************************************************/
    public static Object createInstance (String className)
	throws GException {
	return createInstance (className, new Class[] {}, new Object[] {});
    }
}
