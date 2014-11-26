#!/bin/bash

./fetchIdea.sh

PLUGIN_COMMIT_VERSION=`git describe`
sed -i "s/<\/version/-${PLUGIN_COMMIT_VERSION}<\/version/g" src/META-INF/plugin.xml

# Run the tests
if [ "$1" = "-d" ]; then
    ant -d -f build-package.xml -DIDEA_HOME=./idea-IC
else
    ant -f build-package.xml -DIDEA_HOME=./idea-IC
fi

# Was our build successful?
stat=$?

# Cleanup
if [ "${TRAVIS}" != true ]; then
    ant -f build-package.xml -q clean
    rm -rf idea-IC
    git checkout .
fi

# Return the build status
exit ${stat}
