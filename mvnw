#!/bin/sh
# Maven wrapper — uses system Maven if available, otherwise downloads it.

BASEDIR="$(cd "$(dirname "$0")" && pwd)"
PROPERTIES="$BASEDIR/.mvn/wrapper/maven-wrapper.properties"

# Use system Maven if available
if command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
fi

# Read distribution URL from properties
if [ ! -f "$PROPERTIES" ]; then
    echo "Error: $PROPERTIES not found." >&2
    exit 1
fi
DISTRIBUTION_URL=$(grep "^distributionUrl=" "$PROPERTIES" | sed 's/distributionUrl=//' | tr -d '\r\n')

# Determine local cache directory
DIST_NAME=$(basename "$DISTRIBUTION_URL" .zip)
MAVEN_HOME="${MAVEN_USER_HOME:-$HOME/.m2}/wrapper/dists/$DIST_NAME"

# Download Maven if not cached
if [ ! -f "$MAVEN_HOME/bin/mvn" ]; then
    echo "Downloading Maven from $DISTRIBUTION_URL ..." >&2
    TMP_DIR="$(mktemp -d)"
    TMP_ZIP="$TMP_DIR/maven.zip"
    if command -v curl >/dev/null 2>&1; then
        curl -fL -o "$TMP_ZIP" "$DISTRIBUTION_URL"
    elif command -v wget >/dev/null 2>&1; then
        wget -q -O "$TMP_ZIP" "$DISTRIBUTION_URL"
    else
        echo "Error: Neither curl nor wget found. Install Maven manually." >&2
        rm -rf "$TMP_DIR"
        exit 1
    fi
    mkdir -p "$MAVEN_HOME"
    unzip -qq -d "$TMP_DIR" "$TMP_ZIP"
    mv "$TMP_DIR"/apache-maven-*/* "$MAVEN_HOME/"
    rm -rf "$TMP_DIR"
    echo "Maven installed to $MAVEN_HOME" >&2
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
