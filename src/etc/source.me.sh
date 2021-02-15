#  Set environment variable CLASSPATH.
#  Usage: . build/run/source.me.sh
#
#  You do not need to use this source file if you use 'run-*'
#  scripts - those scripts set CLASSPATH for themselves. Use it if you
#  wish to run some clients that do not have their own run script.
#
#  $Id: source.me.sh,v 1.2 2007/03/25 18:55:17 marsenger Exp $
# ----------------------------------------------------

PROJECT_HOME=@PROJECT_HOME@
LIBS_PATH=@PROJECT_DEPS@

CLASSPATH=${PROJECT_HOME}/build/classes
CLASSPATH=`echo ${PROJECT_HOME}/build/lib/*.jar | tr ' ' ':'`:$CLASSPATH
CLASSPATH=$LIBS_PATH:$CLASSPATH
export CLASSPATH
