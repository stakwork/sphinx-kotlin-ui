#!/usr/bin/env bash

ls
#./gradlew clean
#./gradlew package

SPHINX_DIR="/sphinx-kotlin-ui"
if [ -d "$SPHINX_DIR" ]; then
  echo "Packaging the linux app"
  cd $SPHINX_DIR
  echo "Cleaning the build directory"
  ./gradlew clean
  echo "Packaging the linux sphinx-kotlin app"
  ./gradlew package
else
  echo "Error: ${SPHINX_DIR} not found. Can not continue. Please run image from ${SPHINX_DIR} directory"
  exit 1
fi