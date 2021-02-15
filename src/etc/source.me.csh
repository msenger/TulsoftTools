#  Set environment variable CLASSPATH.
#  Usage: source run/source.me.csh
#
#  You do not need to use this source file if you use 'run/run-*'
#  scripts - those scripts set CLASSPATH for themselves. Use it if you
#  wish to run some clients that do not have their own run script, or
#  if you wish to set some Java properties on the command line
#  (because the run scripts do not support that).
#
#  $Id: source.me.csh,v 1.2 2007/03/25 18:55:17 marsenger Exp $
# ----------------------------------------------------

set PROJECT_HOME=@PROJECT_HOME@
set LIBS_PATH=@PROJECT_DEPS@

setenv CLASSPATH ${PROJECT_HOME}/build/classes
setenv CLASSPATH `echo ${PROJECT_HOME}/build/lib/*.jar | tr ' ' ':'`:${CLASSPATH}
setenv CLASSPATH ${LIBS_PATH}:${CLASSPATH}

