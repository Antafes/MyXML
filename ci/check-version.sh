#!/bin/sh

VERSION_REPO=`cat ../myxml-release/version`
VERSION=`cat VERSION`

if [ "$VERSION_REPO" -ne "$VERSION"]
then
    echo "New version found."
    exit 0
else
    echo "No new version, nothing to do here."
    exit 1
fi