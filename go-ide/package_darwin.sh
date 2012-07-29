#!/bin/bash
#
# Author: Alexandre Normand (https://github.com/alexandre-normand)
# Date: July 30th, 2011
#
# Many variables (including source paths) can/must be overriden.
# Refer to package.sh for the list

SOURCE_PATH_GO_PLUGIN=${SOURCE_PATH_GO_PLUGIN:-`pwd`/..}
TARGET_HOST=darwin ./package.sh
DIST_FOLDER=${DIST_FOLDER:-${SOURCE_PATH_GO_PLUGIN}/../dist}

function createDmg()
{
    echo
    echo "Creating dmg image from ${DIST_FOLDER}/darwin_$1"
    echo

    rm -r "${DIST_FOLDER}/darwin_$1"
    unzip -o "${DIST_FOLDER}/goide-darwin_$1.zip" -d "${DIST_FOLDER}/darwin_$1"
    mv -f "${DIST_FOLDER}/darwin_$1/go-ide" "${DIST_FOLDER}/darwin_$1/Go Ide.app"
    SRC_FOLDER="${DIST_FOLDER}/darwin_$1" IMAGE_FILE=${DIST_FOLDER}/goide_osx_$1.dmg ./make_dmg.sh
}

#createDmg 386
createDmg amd64
