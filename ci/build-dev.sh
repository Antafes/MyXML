#!/bin/sh

ROOT_FOLDER="$( pwd )/../"
M2_HOME="${HOME}/.m2"
M2_CACHE="${ROOT_FOLDER}/maven"

echo "Generating symbolic link for cache"

if [ -d "${M2_CACHE}" ] && [ ! -d "${M2_HOME}" ]
then
    ln -s "${M2_CACHE}" "${M2_HOME}"
fi

# Start build without tests
mvn -Dmaven.test.skip=true clean package

cp target/*.zip ../dist
DATE=`date +%d%m%Y%H%M`
echo "build-$DATE" >> ../dist/name
TAG=`cat ../myxml-release/tag`
echo "$TAG-$DATE" >> ../dist/tag
cp .git/ref ../dist/commit_sha