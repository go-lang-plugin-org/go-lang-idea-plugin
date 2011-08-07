#!/bin/bash

# the go sdk and idea community builds are time consuming.
#
# Once you have built them once you can skip the build again by invoking this script like this
#
# SKIP_GO_SDK_BUILD=true SKIP_IDEA_BUILD=true ./package_linux.sh
#
# Every variable below (except for SOURCE_PATH_IDEA_BUILT) can be overridden in this fashion.
#


SOURCE_PATH_GO=${SOURCE_PATH_GO:-${HOME}/Tools/google-go/release/}
SOURCE_PATH_IDEA=${SOURCE_PATH_IDEA:-${HOME}/Work/IntellijIdea/idea/}
SOURCE_PATH_IDEA_BUILT=idea_community_not_built
SOURCE_PATH_GO_PLUGIN=${SOURCE_PATH_GO_PLUGIN:-`pwd`/..}

SKIP_GO_SDK_BUILD=${SKIP_GO_SDK_BUILD:-false}
SKIP_IDEA_BUILD=${SKIP_IDEA_BUILD:-false}

RELEASE_TAG_GO=${RELEASE_TAG_GO:-release.r58.1}
RELEASE_TAG_IDEA=${RELEASE_TAG_IDEA:-idea/108.857}

GO_IDE_VERSION=${GO_IDE_VERSION:-0.4.18}

FOLDER_DIST=${FOLDER_DIST:-$SOURCE_PATH_GO_PLUGIN/dist}

TARGET_GO_HOST=${TARGET_GO_HOST:-linux}
TARGET_OS=${TARGET_OS:-linux}

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
    if [[ "x$RELEASE_TAG_IDEA" =~ "x$ACTUAL_RELEASE_TAG_IDEA" ]]; then
        echo "Error: Intellij Idea Community source tree is at the wrong tag: $ACTUAL_RELEASE_TAG_IDEA"
        echo -e " Try this:"
        echo -e "\tpushd '$SOURCE_PATH_IDEA' && git pull && git checkout $RELEASE_TAG_IDEA && popd"
#        echo -e "\tYou will also need to edit & commit the platform/platform-main/src/com/intellij/idea/MainImpl.java"
#        echo -e "\tby replacing the System.setProperty(\"idea.platform.prefix\", \"Idea\"); line (at the start of the start method with this:"
#        echo -e "\t    if ( System.getProperty(\"idea.platform.prefix\", \"Idea\").equals(\"Idea\")) {"
#    	echo -e "\t      System.setProperty(\"idea.platform.prefix\", \"Idea\");"
#        echo -e "\t    }"
        echo
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
    rm -rf $SOURCE_PATH_GO/pkg/*
#    GOROOT_FINAL="$FOLDER_DIST/go-sdk-${TARGET_GO_HOST}_$1" GOHOSTARCH=$1 GOARCH=amd64 ./all.bash
#    GOROOT_FINAL="$FOLDER_DIST/go-sdk-${TARGET_GO_HOST}_$1" GOHOSTARCH=$1 GOARCH=386 ./all.bash
    GOHOSTARCH=$1 ./all.bash
    popd >/dev/null

    rm -rf "$FOLDER_DIST/go-sdk-${TARGET_GO_HOST}_$1"
    mkdir -p "$FOLDER_DIST/go-sdk-${TARGET_GO_HOST}_$1"

    cp -r "$SOURCE_PATH_GO/bin" "$FOLDER_DIST/go-sdk-${TARGET_GO_HOST}_$1/bin"
    cp -r "$SOURCE_PATH_GO/src" "$FOLDER_DIST/go-sdk-${TARGET_GO_HOST}_$1/src"
    cp -r "$SOURCE_PATH_GO/pkg/" "$FOLDER_DIST/go-sdk-${TARGET_GO_HOST}_$1/pkg"
}

function build_idea_community() {
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
    ant -Didea.community.build=$sourcePathIdeaBuilt -f build-package.xml

    popd
}

function getIdeaPackageFileNameSuffixForOS()
{
    if [ "$1" == "linux" ]; then
       ideaFileNameSuffix=".tar.gz"
    elif [ "$1" == "mac" ]; then
       ideaFileNameSuffix=".mac.zip"
    elif [ "$1" == "windows" ]; then
       ideaFileNameSuffix=".win.zip"
    fi
}

function decompressBuild()
{
    if [ "$1" == "linux" ]; then
       tar -xzvf $2
    elif [ "$1" == "mac" ]; then
       unzip -f $2
    elif [ "$1" == "windows" ]; then
       unzip $2
    fi 
}

function decompressBuild()
{
    if [ "$1" == "linux" ]; then
       tar -xzvf $2
    elif [ "$1" == "mac" ]; then
       unzip -f $2
    elif [ "$1" == "windows" ]; then
       unzip $2 -d idea-IC-win
    fi 
}

function getSourcePathIdeaBuilt()
{   
    if [ "$1" == "linux" ]; then
       sourcePathIdeaBuilt=$FOLDER_DIST/idea-IC-$IDEA_BUILD_VERSION
    elif [ "$1" == "mac" ]; then
       sourcePathIdeaBuilt=$FOLDER_DIST/IntelliJ\ IDEA\ 10\ CE.app
    elif [ "$1" == "windows" ]; then
       sourcePathIdeaBuilt=$FOLDER_DIST/idea-IC-win
    fi
}

function extract_idea_community_build() {
     IDEA_BUILD_VERSION=`cat "$SOURCE_PATH_IDEA/build.txt"`

    if [ -d "$FOLDER_DIST/idea-$IDEA_BUILD_VERSION" ]; then
        rm -rf "$FOLDER_DIST/idea-IC-$IDEA_BUILD_VERSION"
    fi

    mkdir -p "$FOLDER_DIST/idea-IC-$IDEA_BUILD_VERSION"

    pushd "$FOLDER_DIST" >/dev/null
    getIdeaPackageFileNameSuffixForOS $TARGET_OS    
    buildFile=$SOURCE_PATH_IDEA/out/artifacts/ideaIC-$IDEA_BUILD_VERSION$ideaFileNameSuffix 
    decompressBuild $TARGET_OS $buildFile
    
    getSourcePathIdeaBuilt $TARGET_OS     
    echo $sourcePathIdeaBuilt
    popd
}

function assemble_distribution() {
    echo
    echo
    echo "Assembling distribution for arch $1"
    echo
    pushd "$SOURCE_PATH_GO_PLUGIN" >/dev/null

    # the default build target in \ Community will do a clean,build
    ant \
        -Dgo.ide.target.package=$SOURCE_PATH_GO_PLUGIN/dist/goide-${TARGET_GO_HOST}_$1.zip \
        -Didea.community.build=$sourcePathIdeaBuilt \
        -Dgo.sdk.build=$SOURCE_PATH_GO_PLUGIN/dist/go-sdk-${TARGET_GO_HOST}_$1 \
        -Dgo.plugin=$SOURCE_PATH_GO_PLUGIN/dist/ro.redeul.google.go.jar \
        -f build-distribution.xml

    popd
}

# enable fail on command errors
set -eo pipefail

validate_go_sdk_location
validate_idea_community_location

if [ "$SKIP_GO_SDK_BUILD" != "true" ]; then
    build_go_sdk 386
    build_go_sdk amd64
fi

if [ "$SKIP_IDEA_BUILD" != "true" ]; then
    build_idea_community
fi

extract_idea_community_build
build_idea_go_plugin

assemble_distribution 386
assemble_distribution amd64
