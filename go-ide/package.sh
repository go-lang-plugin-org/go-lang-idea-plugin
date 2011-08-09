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
SOURCE_PATH_GO_WIN=${SOURCE_PATH_GO_WIN:-${HOME}/Tools/google-go/gowin32_release.r59.zip}

SOURCE_PATH_IDEA=${SOURCE_PATH_IDEA:-${HOME}/Work/IntellijIdea/idea/}
SOURCE_PATH_IDEA_BUILT=idea_community_not_built
SOURCE_PATH_GO_PLUGIN=${SOURCE_PATH_GO_PLUGIN:-`pwd`/..}

SKIP_GO_SDK_BUILD=${SKIP_GO_SDK_BUILD:-false}
SKIP_IDEA_BUILD=${SKIP_IDEA_BUILD:-false}

RELEASE_TAG_GO=${RELEASE_TAG_GO:-release.r59}
RELEASE_TAG_IDEA=${RELEASE_TAG_IDEA:-idea/108.857}

GO_IDE_VERSION=${GO_IDE_VERSION:-1.0.0}

FOLDER_DIST=${FOLDER_DIST:-$SOURCE_PATH_GO_PLUGIN/dist}

# linux | darwin | windows
TARGET_HOST=${TARGET_HOST:-linux}

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
    if [ "release $RELEASE_TAG_GO" != "$ACTUAL_RELEASE_TAG_GO" ]; then
        echo "Error: Go Source code is at the wrong tag: $ACTUAL_RELEASE_TAG_GO."
        echo -e " Try this:"
        echo -e "\tpushd '${SOURCE_PATH_GO}' && hg pull && hg update --clean ${RELEASE_TAG_GO} && popd"
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
    if [ "$RELEASE_TAG_IDEA" != "$ACTUAL_RELEASE_TAG_IDEA" ]; then
        echo "Error: Intellij Idea Community source tree is at the wrong tag: $ACTUAL_RELEASE_TAG_IDEA"
        echo -e " Try this:"
        echo -e "\tpushd '$SOURCE_PATH_IDEA' && git pull && git checkout $RELEASE_TAG_IDEA && popd"
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
    rm -rf "$SOURCE_PATH_GO"/{pkg,bin}/*
    GOHOSTARCH=$1 ./all.bash
    popd >/dev/null

    rm -rf "$FOLDER_DIST/go-sdk-${TARGET_HOST}_$1"
    mkdir -p "$FOLDER_DIST/go-sdk-${TARGET_HOST}_$1"

    cp -r "$SOURCE_PATH_GO"/{bin,src,pkg,doc,include} "$FOLDER_DIST/go-sdk-${TARGET_HOST}_$1"
    cp -r "$SOURCE_PATH_GO"/{AUTHORS,CONTRIBUTORS,LICENSE,PATENTS,README,robots.txt,favicon.ico} "$FOLDER_DIST/go-sdk-${TARGET_HOST}_$1"
}

function unpack_go_sdk_windows() {
    echo
    echo
    echo "Unpacking go windows prebuilt sdk"
    echo


#    SOURCE_PATH_GO_WIN

    rm -rf "$FOLDER_DIST/go-sdk-${TARGET_HOST}_386"
    mkdir -p "$FOLDER_DIST/go-sdk-${TARGET_HOST}_386"
    pushd "$FOLDER_DIST/go-sdk-${TARGET_HOST}_386" > /dev/null
    unzip "$SOURCE_PATH_GO_WIN"

    mv go/{bin,pkg,src,doc,include} .
    mv go/{AUTHORS,CONTRIBUTORS,LICENSE,PATENTS,README,robots.txt,favicon.ico} .
    rm -rf go
    popd >/dev/null
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
    ant -Didea.community.build=$SOURCE_PATH_IDEA_BUILT -f build-package.xml

    popd
}

#function getIdeaPackageFileNameSuffixForOS()
#{
#    if [ "$1" == "linux" ]; then
#       ideaFileNameSuffix=".tar.gz"
#    elif [ "$1" == "mac" ]; then
#       ideaFileNameSuffix=".mac.zip"
#    elif [ "$1" == "windows" ]; then
#       ideaFileNameSuffix=".win.zip"
#    fi
#}
#
#function decompressBuild()
#{
#    if [ "$1" == "linux" ]; then
#       tar -xzvf $2
#    elif [ "$1" == "mac" ]; then
#       unzip -f $2
#    elif [ "$1" == "windows" ]; then
#       unzip $2
#    fi
#}
#
#function decompressBuild()
#{
#    if [ "$1" == "linux" ]; then
#       tar -xzvf $2
#    elif [ "$1" == "mac" ]; then
#       unzip -f $2
#    elif [ "$1" == "windows" ]; then
#       unzip $2 -d idea-IC-win
#    fi
#}
#
#function getSourcePathIdeaBuilt()
#{
#    if [ "$1" == "linux" ]; then
#       sourcePathIdeaBuilt=$FOLDER_DIST/idea-IC-$IDEA_BUILD_VERSION
#    elif [ "$1" == "mac" ]; then
#       sourcePathIdeaBuilt=$FOLDER_DIST/IntelliJ\ IDEA\ 10\ CE.app
#    elif [ "$1" == "windows" ]; then
#       sourcePathIdeaBuilt=$FOLDER_DIST/idea-IC-win
#    fi
#}

function extract_idea_community_build() {
    IDEA_BUILD_VERSION=`cat "$SOURCE_PATH_IDEA/build.txt"`

    SOURCE_PATH_IDEA_BUILT=$FOLDER_DIST/idea-IC-$IDEA_BUILD_VERSION-$TARGET_HOST

    if [ -d "$SOURCE_PATH_IDEA_BUILT" ]; then
        rm -rf "$SOURCE_PATH_IDEA_BUILT"
    fi

    pushd "$FOLDER_DIST" >/dev/null
    if [ "$TARGET_HOST" == "linux" ]; then

        if [ -d "$FOLDER_DIST/idea-IC-$IDEA_BUILD_VERSION" ]; then
            rm -rf "$FOLDER_DIST/idea-IC-$IDEA_BUILD_VERSION"
        fi

        tar -xzvf $SOURCE_PATH_IDEA/out/artifacts/ideaIC-$IDEA_BUILD_VERSION.tar.gz
        mv $FOLDER_DIST/idea-IC-$IDEA_BUILD_VERSION $FOLDER_DIST/idea-IC-$IDEA_BUILD_VERSION-linux
    elif [ "$TARGET_HOST" == "darwin" ]; then

        if [ -d "$FOLDER_DIST/Community Edition-IC-$IDEA_BUILD_VERSION.app" ]; then
            rm -rf "$FOLDER_DIST/Community Edition-IC-$IDEA_BUILD_VERSION.app"
        fi

        unzip "$SOURCE_PATH_IDEA/out/artifacts/ideaIC-$IDEA_BUILD_VERSION.mac.zip"
        mv "$FOLDER_DIST/Community Edition-IC-$IDEA_BUILD_VERSION.app" "$SOURCE_PATH_IDEA_BUILT"
    elif [ "$TARGET_HOST" == "windows" ]; then
        mkdir $SOURCE_PATH_IDEA_BUILT
        pushd $SOURCE_PATH_IDEA_BUILT >/dev/null
        unzip $SOURCE_PATH_IDEA/out/artifacts/ideaIC-$IDEA_BUILD_VERSION.win.zip
        popd >/dev/null
    fi

    popd >/dev/null
    echo $SOURCE_PATH_IDEA_BUILT
}

function assemble_distribution() {
    echo
    echo
    echo "Assembling distribution for arch $1"
    echo
    pushd "$SOURCE_PATH_GO_PLUGIN" >/dev/null

    ant \
        -Dgo.ide.target.package=$SOURCE_PATH_GO_PLUGIN/dist/goide-${TARGET_HOST}_$1.zip \
        -Didea.community.build=$SOURCE_PATH_IDEA_BUILT \
        -Dgo.sdk.build=$SOURCE_PATH_GO_PLUGIN/dist/go-sdk-${TARGET_HOST}_$1 \
        -Dgo.plugin=$SOURCE_PATH_GO_PLUGIN/dist/ro.redeul.google.go.jar \
        -f build-distribution.xml

    popd > /dev/null
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

if [ "$TARGET_HOST" == "windows" ]; then
    unpack_go_sdk_windows
fi

extract_idea_community_build
build_idea_go_plugin

assemble_distribution 386
if [ "$TARGET_HOST" != "windows" ]; then
    assemble_distribution amd64
fi
