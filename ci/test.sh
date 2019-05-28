#!/bin/sh

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
    <profiles>
        <profile>
            <id>SUREFIRE-1588</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <argLine>-Djdk.net.URLClassPath.disableClassPathURLCheck=true</argLine>
            </properties>
        </profile>
    </profiles>
</settings>

EOF

mvn test
