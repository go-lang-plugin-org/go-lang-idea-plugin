#!/bin/bash
#
# Author: Alexandre Normand (https://github.com/alexandre-normand)
# Date: July 30th, 2011
# 
# Many variables (including source paths) can/must be overriden. 
# Refer to package.sh for the list

TARGET_HOST=darwin ./package.sh

echo
echo Please use the native .dmg package creator tool to finish the packages from here:
echo    $SOURCE_PATH_GO_PLUGIN/dist/goide-${TARGET_HOST}_386.zip
echo    $SOURCE_PATH_GO_PLUGIN/dist/goide-${TARGET_HOST}_amd64.zip