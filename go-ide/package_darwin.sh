#!/bin/bash
#
# Author: Alexandre Normand (https://github.com/alexandre-normand)
# Date: July 30th, 2011
# 
# Many variables (including source paths) can/must be overriden. 
# Refer to package.sh for the list

SOURCE_PATH_GO_PLUGIN=${SOURCE_PATH_GO_PLUGIN:-`pwd`/..}
#TARGET_HOST=darwin ./package.sh


function createDmg()
{
    echo
    echo "Creating dmg image from $SOURCE_PATH_GO_PLUGIN/dist/darwin_$1"
    echo 

    rm -r $SOURCE_PATH_GO_PLUGIN/dist/darwin_$1
    unzip -o $SOURCE_PATH_GO_PLUGIN/dist/goide-darwin_$1.zip -d $SOURCE_PATH_GO_PLUGIN/dist/darwin_$1
    mv -f "$SOURCE_PATH_GO_PLUGIN/dist/darwin_$1/go-ide" "$SOURCE_PATH_GO_PLUGIN/dist/darwin_$1/Go Ide.app"
    IMAGE_FILE=$SOURCE_PATH_GO_PLUGIN/dist/goide_$1.dmg
    SRC_FOLDER="$SOURCE_PATH_GO_PLUGIN/dist/darwin_$1" IMAGE_FILE=$SOURCE_PATH_GO_PLUGIN/dist/goide_$1.dmg ./make_dmg.sh
}

createDmg 386
#createDmg amd64
