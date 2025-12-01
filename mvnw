#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup script
# ----------------------------------------------------------------------------

set -euf

if [ "${MVNW_VERBOSE-}" = true ]; then
  set -x
fi

# set JAVACMD if not already set and ensure it is executable
if [ -z "${JAVACMD-}" ]; then
  if [ -n "${JAVA_HOME-}" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=$(command -v java || true)
  fi
fi

if [ -z "$JAVACMD" ] || [ ! -x "$JAVACMD" ]; then
  echo "Error: JAVA_HOME is not defined correctly." >&2
  echo "  We cannot execute $JAVACMD" >&2
  exit 1
fi

# Determine the project base directory
if [ -n "${MAVEN_PROJECTBASEDIR-}" ]; then
  BASE_DIR="$MAVEN_PROJECTBASEDIR"
else
  ## resolve links - $0 may be a link to mvnw
  PRG="$0"
  # need to dereference symlinks
  while [ -h "$PRG" ]; do
    ls=$(ls -ld "$PRG")
    link=$(expr "$ls" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' >/dev/null; then
      PRG="$link"
    else
      PRG="$(dirname "$PRG")/$link"
    fi
  done
  saveddir=$(pwd)
  cd "$(dirname "$PRG")" >/dev/null
  BASE_DIR=$(pwd -P)
  cd "$saveddir" >/dev/null
fi

MAVEN_PROJECTBASEDIR=${BASE_DIR:-$(pwd)}
export MAVEN_PROJECTBASEDIR

WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

# download maven if necessary
if [ ! -f "$WRAPPER_JAR" ]; then
  echo "Couldn't find $WRAPPER_JAR, please run 'mvn -N wrapper:wrapper' to create it" >&2
  exit 1
fi

# For Cygwin, ensure paths are in Unix format before anything is touched
if ${cygwin=false}; then
  case "$(uname)" in
  CYGWIN*)
    cygwin=true
    ;;
  esac
fi

# For Mingw, ensure paths are Unix-like
if ${mingw=false}; then
  case "$(uname)" in
  MINGW*)
    mingw=true
    ;;
  esac
fi

if ${cygwin}; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=$(cygpath --unix "$JAVA_HOME")
  MAVEN_PROJECTBASEDIR=$(cygpath --unix "$MAVEN_PROJECTBASEDIR")
fi

if ${mingw}; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=$(echo $JAVA_HOME | sed 's/\\/\//g')
  MAVEN_PROJECTBASEDIR=$(echo $MAVEN_PROJECTBASEDIR | sed 's/\\/\//g')
fi

# Provide a "standard" way to retrieve the CLI args that works with both Windows and non-Windows
MAVEN_CMD_LINE_ARGS="$*"
export MAVEN_CMD_LINE_ARGS

# Set MAVEN_OPTS if not already set
MAVEN_OPTS=${MAVEN_OPTS-}

# Ensure the multi-module directory property is available
WRAPPER_LAUNCHER_ARGS="-Dmaven.multiModuleProjectDirectory=${MAVEN_PROJECTBASEDIR}"

exec "$JAVACMD" \
  $MAVEN_OPTS \
  -classpath "$WRAPPER_JAR" \
  $WRAPPER_LAUNCHER_ARGS \
  $WRAPPER_LAUNCHER \
  "$@"
