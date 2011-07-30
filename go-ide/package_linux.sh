#!/bin/bash

SOURCE_PATH_GO=~/Tools/google-go/release/
SOURCE_PATH_IDEA=~/Work/IntellijIdea/idea/
SOURCE_PATH_GO_PLUGIN=~/Work/google-go-language/

RELEASE_TAG_GO="release.r58.1"
RELEASE_TAG_IDEA=idea/107.322

function validate_go_sdk_location {

    if [ ! -d "${SOURCE_PATH_GO}" ]; then
        echo "Error: '${SOURCE_PATH_GO}' is not a folder"
        exit 1
    fi

    if [ ! -d "${SOURCE_PATH_GO}/.hg" ]; then
        echo "Error: '${SOURCE_PATH_GO}' is not a go checkout folder (is missing Mercurial metadata)"
        exit 1
    fi

    pushd "${SOURCE_PATH_GO}" >/dev/null
    ACTUAL_RELEASE_TAG_GO=`hg identify -t`
    if [ "xrelease ${RELEASE_TAG_GO}" != "x${ACTUAL_RELEASE_TAG_GO}" ]; then
        echo "Error: Go Source code is at the wrong tag: ${ACTUAL_RELEASE_TAG_GO}."
        echo -e " Try this:"
        echo -e "\tpushd '${SOURCE_PATH_GO}' && hg pull && hg update -r ${RELEASE_TAG_GO} && popd"
        exit 1
    fi

    echo "GOOD: Go SDK sources are at the proper release ('${RELEASE_TAG_GO}' vs '${ACTUAL_RELEASE_TAG_GO}')"
    popd >/dev/null
}

function validate_idea_community_location() {

    if [ ! -d "$SOURCE_PATH_IDEA" ]; then
        echo "Error: '$SOURCE_PATH_IDEA' is not a folder"
        exit 1
    fi

    if [ ! -d "$SOURCE_PATH_IDEA/.git" ]; then
        echo "Error: '$SOURCE_PATH_IDEA' is not a idea checkout folder (is missing Git metadata)"
        exit 1
    fi

    pushd "$SOURCE_PATH_IDEA" >/dev/null
    ACTUAL_RELEASE_TAG_IDEA=`git describe`
    if [ "x$RELEASE_TAG_IDEA" != "x$ACTUAL_RELEASE_TAG_IDEA" ]; then
        echo "Error: Intellij Idea Community source tree is at the wrong tag: $ACTUAL_RELEASE_TAG_IDEA"
        echo -e " Try this:"
        echo -e "\tpushd '$SOURCE_PATH_IDEA' && git pull && git checkout $RELEASE_TAG_IDEA && popd"
        exit 1
    fi

    echo "GOOD: Intellij Idea Community source tree is at the proper release ('$RELEASE_TAG_IDEA' vs '$ACTUAL_RELEASE_TAG_IDEA')"
    popd >/dev/null
}

function build_go_sdk() {
    echo "Clean building Go Sdk"

    pushd "$SOURCE_PATH_GO/src"

    # fixup for when you also have a weekly go installed inside on your system.
    /home/mtoader/Tools/google-go/release/bin
    PATH=${SOURCE_PATH_GO}bin:$PATH

    export PATH
    # call the build
    echo $PATH
    ./clean.bash && ./all.bash && popd

    #    rm -r $IDEASOURCEPATH/build/conf/mac/go-sdk
    #    cp -r $GOSOURCEPATH $IDEASOURCEPATH/build/conf/mac/go-sdk
}

function build_idea_community() {
    cp "$SOURCE_PATH_GO_PLUGIN/go-ide/resources/idea_community_{about,logo}.png" "$SOURCE_PATH_IDEA/community-resources/src"

    echo "Cleanly building Idea Community"
    pushd ${SOURCE_PATH_IDEA}

    # the default build target in Intellij Community will do a clean,build
    ant
}

validate_go_sdk_location
validate_idea_community_location

build_go_sdk && build_idea_community
#build_idea_community

exit 0;

# Build the go sdk
cd $GOSOURCEPATH/src
./all.bash
rm -r $IDEASOURCEPATH/build/conf/mac/go-sdk
cp -r $GOSOURCEPATH $IDEASOURCEPATH/build/conf/mac/go-sdk

# Copy Go logo and about images
cp $PLUGINSOURCEPATH/go-ide/resources/idea_community_about.png $IDEASOURCEPATH/community-resources/src/
cp $PLUGINSOURCEPATH/go-ide/resources/idea_community_logo.png $IDEASOURCEPATH/community-resources/src/

cd $IDEASOURCEPATH
ant build
VERSION=$(cat build.txt)
cp $IDEASOURCEPATH/out/artifacts/ideaIC-$VERSION.mac.zip $PLUGINSOURCEPATH/go-ide
cd $PLUGINSOURCEPATH/go-ide
rm -r Community\ Edition-IC-$VERSION.app
unzip ideaIC-$VERSION.mac.zip

unzip ../google-go-language.zip -d Community\ Edition-IC-$VERSION.app/plugins
rm ideaIC-$VERSION.mac.zip

echo "Now open up dmgCreator to make the dmg."