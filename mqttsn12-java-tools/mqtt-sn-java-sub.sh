#!/bin/sh
#set -x

JAVA_OPTS="-Xms64m -Xmx512m"

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

LOGDIR=$PRGDIR/log
CPATH=$PRGDIR/target/mqttsn12-java-tools-1.0.0-jar-with-dependencies.jar

if [ ! -d "$LOGDIR" ]
then
	mkdir "$LOGDIR"
fi

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    # IBM's JDK on AIX uses strange locations for the executables
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      JAVACMD="$JAVA_HOME/jre/sh/java"
    elif [ -x "$JAVA_HOME/jre/bin/java" ] ; then
      JAVACMD="$JAVA_HOME/jre/bin/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java 2> /dev/null `
    if [ -z "$JAVACMD" ] ; then
        JAVACMD=java
    fi
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "ERROR: JAVA_HOME is not defined correctly."
  echo "I cannot execute $JAVACMD"
  exit 1
fi

"$JAVACMD" $JAVA_OPTS -classpath $CPATH -Dlogfile.name=$LOGDIR/subscriber.log io.github.marcoratto.mqttsn.tools.subscriber.Runme "$@"
RET_CODE=$?
if [ $RET_CODE -ne 0 ]
then
    echo "ERROR: java return error code $RET_CODE."
    exit $RET_CODE
fi
exit 0
