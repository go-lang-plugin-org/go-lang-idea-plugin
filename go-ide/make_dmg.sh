#!/bin/bash
#
# Author: Alexandre Normand (https://github.com/alexandre-normand)
# Date: July 30th, 2011
# 
# Making the dmg file

DMG_TEMP_NAME=${DMG_TEMP_NAME:-goIdeTemp.dmg}
SRC_FOLDER=${SRC_FOLDER:-`pwd`/../dist/darwin_amd64}
BACKGROUND_FILE=${BACKGROUND_FILE:-./resources/goide/about.png}
IMAGE_FILE=${IMAGE_FILE:-$FOLDER_DIST/goide-mac.dmg}

echo "Cleaning out old $IMAGE_FILE"
test -f "$IMAGE_FILE" && rm -f "$IMAGE_FILE"
echo "Adding symlink Applications to $SRC_FOLDER..."
ln -fs /Applications $SRC_FOLDER

echo "Creating disk image..."
test -f "${DMG_TEMP_NAME}" && rm -f "${DMG_TEMP_NAME}"
hdiutil create -srcfolder "$SRC_FOLDER" -volname "Go Ide" -fs HFS+ -fsargs "-c c=64,a=16,e=16" -format UDRW -size 600m "${DMG_TEMP_NAME}"

echo "Mounting image..."
MOUNT_DIR="/Volumes/Go Ide"
echo "Mount directory: $MOUNT_DIR"
DEV_NAME=$(hdiutil attach -readwrite -noverify -noautoopen "${DMG_TEMP_NAME}" | egrep '^/dev/' | sed 1q | awk '{print $1}')
echo "Device name:     $DEV_NAME"

mkdir "$MOUNT_DIR/.background"
cp "$BACKGROUND_FILE" "$MOUNT_DIR/.background/$BACKGROUND_FILE_NAME"

# run applescript
echo "Applying layout: osascript dmgLayout.applescript process_disk_image \"Go Ide\""
osascript dmgLayout.applescript process_disk_image "Go Ide"
echo "Done doing layout..."
sleep 6

# make sure it's not world writeable
echo "Fixing permissions..."
# workaround: output is sent to /dev/null to supress permission errors when chmodding .Trashes
chmod -Rf go-w "${MOUNT_DIR}" >& /dev/null || true
echo "Done fixing permissions."

# make the top window open itself on mount:
if [ -x /usr/local/bin/openUp ]; then
    echo "Applying openUp..."
    /usr/local/bin/openUp "${MOUNT_DIR}"
fi

# unmount
echo "Unmounting disk image..."
hdiutil detach "${DEV_NAME}"

# compress image
echo "Compressing disk image..."
hdiutil convert "${DMG_TEMP_NAME}" -format UDZO -imagekey zlib-level=9 -o "$IMAGE_FILE"
rm -f "${DMG_TEMP_NAME}"