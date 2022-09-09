#!/usr/bin/env bash

# Getting current framework version
ASSERTIONS_VERSION=$(egrep -o "assertionsSdkVersion.*=.*" gradle.properties | egrep -o "'(.*)'" | tr -d "\'")
echo "Running SDK publishing script for $ASSERTIONS_VERSION version."

### Check if it is snapshot build
if [[ $ASSERTIONS_VERSION == *"SNAPSHOT"* ]]; then
  echo "[SKIP] $ASSERTIONS_VERSION is development build and should not be published."
  exit 0
fi

### Checking is it already published
# TODO update version URL for version check
POM_URL="https://dl.bintray.com/dekalo-stanislav/heershingenmosiken/com/heershingenmosiken/assertions-android/$ASSERTIONS_VERSION/assertions-android-$ASSERTIONS_VERSION.pom"

if curl --output /dev/null --silent --head --fail "$POM_URL"; then
  echo "[SKIP] Framework version $ASSERTIONS_VERSION already published."
  exit 0
fi


### Publishing

if ./gradlew assemble publishToSonatype closeAndReleaseSonatypeStagingRepository; then
  git tag $ASSERTIONS_VERSION -a -m "$ASSERTIONS_VERSION" HEAD
  git push -q origin $ASSERTIONS_VERSION
  echo "[SUCCESS] $ASSERTIONS_VERSION successfully published."
else
  echo "[FAILURE] Failed to publish SDK $ASSERTIONS_VERSION"
  exit 1
fi
