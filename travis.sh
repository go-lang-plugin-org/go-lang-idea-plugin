#!/bin/bash

# Get the current plugin dir
pluginDir=pwd

# Get our IDEA dependency
if [ -f ~/Tools/ideaIC-13.tar.gz ];
then
    cp ~/Tools/ideaIC-13.tar.gz .
else
    wget http://download-ln.jetbrains.com/idea/ideaIC-13.tar.gz
fi

tar zxf ideaIC-13.tar.gz
rm -rf ideaIC-13.tar.gz

# Pput IDEA folder in a folder we expect it
ideaPath=$(find . -name 'idea-IC*' | head -n 1)
mv ${ideaPath} ./idea-IC

#Run the tests
ant -f build-test.xml

# Was our build succesfull?
stat=$?

# Cleanup
ant -f build-test.xml clean
rm -rf idea-IC

# Return the build status
exit ${stat}