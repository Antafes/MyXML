#!/bin/sh

ACCESS_TOKEN="$1"
ROOT_FOLDER="$( pwd )/../"
M2_HOME="${HOME}/.m2"
M2_CACHE="${ROOT_FOLDER}/maven"

echo "Generating symbolic link for cache"

if [ -d "${M2_CACHE}" ] && [ ! -d "${M2_HOME}" ]
then
    ln -s "${M2_CACHE}" "${M2_HOME}"
fi

cat > ${M2_HOME}/settings.xml <<EOF
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <servers>
        <server>
            <id>github</id>
            <password>${ACCESS_TOKEN}</password>
        </server>
    </servers>
</settings>
EOF

VERSION=`cat VERSION`

# Start build without tests
mvn -Dmaven.test.skip=true clean package

cp target/*.zip ../dist

echo "v$VERSION" >> ../dist/name
echo "v$VERSION" >> ../dist/tag