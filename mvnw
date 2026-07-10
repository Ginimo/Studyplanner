#!/bin/sh
# Apache Maven Wrapper script

if [ -z "$JAVA_HOME" ] && ! command -v java > /dev/null 2>&1; then
  echo "Error: JAVA_HOME is not set and no 'java' command found in PATH." >&2
  exit 1
fi

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
WRAPPER_PROPERTIES="$SCRIPT_DIR/.mvn/wrapper/maven-wrapper.properties"

if [ ! -f "$WRAPPER_PROPERTIES" ]; then
  echo "Cannot find .mvn/wrapper/maven-wrapper.properties" >&2
  exit 1
fi

DISTRIBUTION_URL=$(grep "^distributionUrl=" "$WRAPPER_PROPERTIES" | cut -d= -f2-)
DISTRIBUTION_ID=$(basename "$DISTRIBUTION_URL" .zip)

MAVEN_USER_HOME="${MAVEN_USER_HOME:-$HOME/.m2}"
WRAPPER_DIR="$MAVEN_USER_HOME/wrapper/dists/$DISTRIBUTION_ID"
MAVEN_HOME=$(ls -d "$WRAPPER_DIR"/apache-maven-* 2>/dev/null | head -1)

if [ -z "$MAVEN_HOME" ] || [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
  echo "Downloading $DISTRIBUTION_ID ..."
  mkdir -p "$WRAPPER_DIR"
  TMP_FILE="$WRAPPER_DIR/$DISTRIBUTION_ID.zip"

  if type curl > /dev/null 2>&1; then
    curl -fL "$DISTRIBUTION_URL" -o "$TMP_FILE" || { echo "Download failed." >&2; exit 1; }
  elif type wget > /dev/null 2>&1; then
    wget -q "$DISTRIBUTION_URL" -O "$TMP_FILE" || { echo "Download failed." >&2; exit 1; }
  else
    echo "Error: neither curl nor wget found." >&2
    exit 1
  fi

  unzip -q "$TMP_FILE" -d "$WRAPPER_DIR" && rm "$TMP_FILE"
  MAVEN_HOME=$(ls -d "$WRAPPER_DIR"/apache-maven-* 2>/dev/null | head -1)
fi

if [ -z "$MAVEN_HOME" ] || [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
  echo "Failed to locate Maven in $WRAPPER_DIR" >&2
  exit 1
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
