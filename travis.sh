#!/bin/bash

# Get the current plugin directory
pluginDir=pwd

ideaVersion="13.1.2"

# Get our IDEA dependency
if [ -f ~/Tools/ideaIC-${ideaVersion}.tar.gz ];
then
    cp ~/Tools/ideaIC-${ideaVersion}.tar.gz .
else
    wget http://download-cf.jetbrains.com/idea/ideaIC-${ideaVersion}.tar.gz
fi

# Unzip IDEA
tar zxf ideaIC-${ideaVersion}.tar.gz
rm -rf ideaIC-${ideaVersion}.tar.gz

# Move the versioned IDEA folder to a known location
ideaPath=$(find . -name 'idea-IC*' | head -n 1)
mv ${ideaPath} ./idea-IC

# Run the tests
if [ "$1" = "-d" ]; then
    ant -d -f build-test.xml -DIDEA_HOME=./idea-IC
else
    ant -f build-test.xml -DIDEA_HOME=./idea-IC
fi;

# Was our build successful?
stat=$?

# Cleanup
ant -f build-test.xml -q clean
rm -rf idea-IC

# Return the build status
exit ${stat}
