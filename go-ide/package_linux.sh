#!/bin/bash

SOURCE_PATH_GO=${SOURCE_PATH_GO:-${HOME}/Tools/google-go/release/}
SOURCE_PATH_IDEA=${SOURCE_PATH_IDEA:-${HOME}/Work/IntellijIdea/idea/}
SOURCE_PATH_IDEA_BUILT=idea_community_not_built
SOURCE_PATH_GO_PLUGIN=${SOURCE_PATH_GO_PLUGIN:-`pwd`/..}

SKIP_GO_SDK_BUILD=${SKIP_GO_SDK_BUILD:-false}
SKIP_IDEA_BUILD=${SKIP_IDEA_BUILD:-false}

RELEASE_TAG_GO=${RELEASE_TAG_GO:-release.r58.1}
RELEASE_TAG_IDEA=${RELEASE_TAG_IDEA:-idea/107.322}

GO_IDE_VERSION=${GO_IDE_VERSION:-0.4.18}

FOLDER_DIST=${FOLDER_DIST:-$SOURCE_PATH_GO_PLUGIN/dist}

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
    echo
    echo
    echo "Clean building Go Sdk"
    echo

    pushd "$SOURCE_PATH_GO/src" >/dev/null

    ./clean.bash
    ./all.bash

    popd

    #    rm -r $IDEASOURCEPATH/build/conf/mac/go-sdk
    #    cp -r $GOSOURCEPATH $IDEASOURCEPATH/build/conf/mac/go-sdk
}

function build_idea_community() {

    cp "$SOURCE_PATH_GO_PLUGIN/go-ide/resources/idea_community_about.png" "$SOURCE_PATH_IDEA/community-resources/src"
    cp "$SOURCE_PATH_GO_PLUGIN/go-ide/resources/idea_community_logo.png" "$SOURCE_PATH_IDEA/community-resources/src"

    echo
    echo
    echo "Cleanly building Idea Community"
    echo
    pushd ${SOURCE_PATH_IDEA} >/dev/null

    # the default build target in Intellij Community will do a clean,build
    ant
    popd
}

function build_idea_go_plugin() {

    echo
    echo
    echo "Cleanly building Idea Go plugin"
    echo
    pushd "$SOURCE_PATH_GO_PLUGIN" >/dev/null

    # the default build target in Intellij Community will do a clean,build
    ant -Didea.community.build=$SOURCE_PATH_IDEA_BUILT -f build-package.xml

    popd
}

function extract_idea_community_build() {
     IDEA_BUILD_VERSION=`cat "$SOURCE_PATH_IDEA/build.txt"`

    if [ -d "$FOLDER_DIST/idea-$IDEA_BUILD_VERSION" ]; then
        rm -rf "$FOLDER_DIST/idea-$IDEA_BUILD_VERSION"
    fi

    mkdir -p "$FOLDER_DIST/idea-$IDEA_BUILD_VERSION"

    pushd "$FOLDER_DIST" >/dev/null
    tar -xzvf $SOURCE_PATH_IDEA/out/artifacts/ideaIC-$IDEA_BUILD_VERSION.tar.gz

    SOURCE_PATH_IDEA_BUILT=$FOLDER_DIST/idea-$IDEA_BUILD_VERSION
    popd
}

# fail on command errors
set -eo pipefail

validate_go_sdk_location
validate_idea_community_location

if [ $SKIP_GO_SDK_BUILD != "true" ]; then
    build_go_sdk
fi

if [ ! $SKIP_IDEA_BUILD != "true" ]; then
    build_idea_community
fi

extract_idea_community_build

echo

build_idea_go_plugin

assemble_distribution

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